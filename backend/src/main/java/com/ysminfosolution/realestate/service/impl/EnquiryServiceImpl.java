package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.ysminfosolution.realestate.dto.EnquiryBasicInfoDTO;
import com.ysminfosolution.realestate.dto.EnquiryResponseDTO;
import com.ysminfosolution.realestate.dto.NewEnquiryDTO;
import com.ysminfosolution.realestate.dto.UpdateEnquiryDTO;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.AreaOptions;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.EnquiryPropertyOptions;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.PropertyOption;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.PropertyOptionsFlat;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.PropertyTypeOption;
import com.ysminfosolution.realestate.error.exception.ApiException;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Employee;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.model.Floor.PropertyType;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.EmployeeRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FloorRepository;
import com.ysminfosolution.realestate.repository.ProjectRepository;
import com.ysminfosolution.realestate.repository.TaskRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.EnquiryService;
import com.ysminfosolution.realestate.service.FollowUpService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Validated
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EnquiryServiceImpl implements EnquiryService {

    private final EnquiryRepository enquiryRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;
    private final FloorRepository floorRepository;

    private final FollowUpService followUpService;
    private final ProjectAuthorizationService projectAuthorizationService;
    private final ProjectResolver projectResolver;

    @Override
    public ResponseEntity<EnquiryResponseDTO> createNewEnquiry(NewEnquiryDTO newEnquiryDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createNewEnquiry");

        Project project = projectResolver.resolve(newEnquiryDTO.projectId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        Enquiry enquiry = new Enquiry();
        enquiry.setProject(project);
        applyLeadFields(enquiry, newEnquiryDTO);
        enquiry.setPropertyType(newEnquiryDTO.propertyType());
        enquiry.setProperty(newEnquiryDTO.property());
        enquiry.setArea(newEnquiryDTO.area());
        enquiry.setBudget(newEnquiryDTO.budget());
        enquiry.setReference(newEnquiryDTO.reference());
        enquiry.setReferenceName(newEnquiryDTO.referenceName());
        enquiry.setStatus(Status.ONGOING);
        enquiry.setRemark(null);
        enquiry.setDeleted(false);

        try {
            enquiry = enquiryRepository.save(enquiry);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occured while saving enquiry");
        }

        if (!followUpService.createFollowUpForEnquiry(enquiry, appUserDetails)) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Error occured while creating follow-up for enquiry");
        }

        return ResponseEntity.ok(toResponseDTO(enquiry));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiries(AppUserDetails appUserDetails) {

        log.info("Method: getAllEnquiries");

        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.ok(enquiryRepository.findAllEnquiriesForOrg(appUserDetails.getOrgId()));
        }

        if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            Employee employee = employeeRepository
                    .findByUser_Id(UUID.fromString(appUserDetails.getUserId()))
                    .orElseThrow(() -> new AccessDeniedException("Employee not found"));

            Set<Project> allocatedProjects = employee.getProjects();
            if (allocatedProjects.isEmpty()) {
                return ResponseEntity.ok(Set.of());
            }

            return ResponseEntity.ok(enquiryRepository.findAllEnquiriesForProjects(allocatedProjects));
        }

        throw new AccessDeniedException("User does not have access to the enquiries");
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiriesForProject(UUID projectId,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllEnquiriesForProject");

        Project project = projectResolver.resolve(projectId);
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        Set<EnquiryResponseDTO> enquiries = enquiryRepository.findAllByProject_Id(projectId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toSet());

        return ResponseEntity.ok(enquiries);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EnquiryResponseDTO> getById(@NotNull UUID enquiryId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getById");

        Enquiry enquiry = findEnquiry(enquiryId);
        projectAuthorizationService.checkProjectAccess(appUserDetails, enquiry.getProject());

        return ResponseEntity.ok(toResponseDTO(enquiry));
    }

    @Override
    public ResponseEntity<String> cancelEnquiryWithRemark(@NotNull UUID enquiryId, String remark,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: cancelEnquiryWithRemark");

        Enquiry enquiry = findEnquiry(enquiryId);
        projectAuthorizationService.checkProjectAccess(appUserDetails, enquiry.getProject());

        enquiry.setRemark(remark);
        applyStatusChange(enquiry, Status.CANCELLED);

        try {
            enquiryRepository.save(enquiry);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occured while cancelling enquiry");
        }

        return ResponseEntity.ok("Enquiry Cancelled Successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Set<EnquiryBasicInfoDTO>> getListOfEnquiryBasicInfo(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getListOfEnquiryBasicInfo");

        Set<Project> projects = resolveAccessibleProjects(appUserDetails);
        Set<EnquiryBasicInfoDTO> basicInfoDTOs = new HashSet<>();

        for (Project project : projects) {
            Set<Enquiry> enquiries = enquiryRepository.findAllByProject_Id(project.getId());

            for (Enquiry enquiry : enquiries) {
                basicInfoDTOs.add(new EnquiryBasicInfoDTO(
                        enquiry.getId(),
                        enquiry.getCreatedAt(),
                        enquiry.getLeadName(),
                        enquiry.getLeadMobileNumber(),
                        project.getId(),
                        project.getProjectName(),
                        enquiry.getBudget(),
                        enquiry.getStatus()));
            }
        }

        return ResponseEntity.ok(basicInfoDTOs);
    }

    @Override
    public ResponseEntity<String> updateEnquiry(@NotNull UUID enquiryId, UpdateEnquiryDTO updateEnquiryDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: updateEnquiry");

        Enquiry enquiry = findEnquiry(enquiryId);
        projectAuthorizationService.checkProjectAccess(appUserDetails, enquiry.getProject());

        applyLeadFields(enquiry, updateEnquiryDTO);

        if (updateEnquiryDTO.budget() != null) {
            enquiry.setBudget(updateEnquiryDTO.budget());
        }

        if (updateEnquiryDTO.reference() != null) {
            enquiry.setReference(updateEnquiryDTO.reference());
        }

        if (updateEnquiryDTO.referenceName() != null) {
            enquiry.setReferenceName(updateEnquiryDTO.referenceName());
        }

        if (updateEnquiryDTO.remark() != null) {
            enquiry.setRemark(updateEnquiryDTO.remark());
        }

        if (updateEnquiryDTO.propertyType() != null) {
            enquiry.setPropertyType(updateEnquiryDTO.propertyType());
        }

        if (updateEnquiryDTO.property() != null) {
            enquiry.setProperty(updateEnquiryDTO.property());
        }

        if (updateEnquiryDTO.area() != null) {
            enquiry.setArea(updateEnquiryDTO.area());
        }

        if (updateEnquiryDTO.status() != null) {
            applyStatusChange(enquiry, updateEnquiryDTO.status());
        }

        enquiryRepository.save(enquiry);

        return ResponseEntity.ok("Enquiry Updated Successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<EnquiryPropertyOptions> getAllPropertyOptionsForProject(
            UUID projectId, AppUserDetails appUserDetails) {

        log.info("\nMethod: getAllPropertyOptionsForProject");

        Project project = projectResolver.resolve(projectId);
        if (!project.getOrganization().getId().equals(appUserDetails.getOrgId())) {
            throw new AccessDeniedException("User is not authorized to access this project");
        }

        Set<PropertyOptionsFlat> flatRows = floorRepository.getFlatPropertyOptions(projectId);

        Map<PropertyType, Map<String, Map<Double, Long>>> grouped = flatRows.stream().collect(
                Collectors.groupingBy(
                        PropertyOptionsFlat::getPropertyType,
                        Collectors.groupingBy(
                                PropertyOptionsFlat::getProperty,
                                Collectors.groupingBy(
                                        PropertyOptionsFlat::getArea,
                                        Collectors.summingLong(PropertyOptionsFlat::getQuantity)))));

        Set<PropertyTypeOption> typeOptions = grouped.entrySet().stream()
                .map(typeEntry -> {
                    Set<PropertyOption> propertyOptions = typeEntry.getValue().entrySet().stream()
                            .map(propEntry -> {
                                Set<AreaOptions> areaOptions = propEntry.getValue().entrySet().stream()
                                        .map(areaEntry -> new AreaOptions(areaEntry.getKey(), areaEntry.getValue()))
                                        .collect(Collectors.toSet());

                                long propertiesAvailable = areaOptions.stream()
                                        .mapToLong(AreaOptions::propertiesAvailable)
                                        .sum();

                                return new PropertyOption(propEntry.getKey(), areaOptions, propertiesAvailable);
                            })
                            .collect(Collectors.toSet());

                    long totalForType = propertyOptions.stream()
                            .mapToLong(PropertyOption::propertiesAvailable)
                            .sum();

                    return new PropertyTypeOption(typeEntry.getKey(), propertyOptions, totalForType);
                })
                .collect(Collectors.toSet());

        long totalAvailable = typeOptions.stream()
                .mapToLong(PropertyTypeOption::propertiesAvailable)
                .sum();

        return ResponseEntity.ok(new EnquiryPropertyOptions(typeOptions, totalAvailable));
    }

    @Override
    public ResponseEntity<String> changeEnquiryStatus(@NotNull UUID enquiryId, Status status,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: changeEnquiryStatus");

        Enquiry enquiry = findEnquiry(enquiryId);
        projectAuthorizationService.checkProjectAccess(appUserDetails, enquiry.getProject());

        if (status == Status.CANCELLED && (enquiry.getRemark() == null || enquiry.getRemark().isBlank())) {
            enquiry.setRemark("Enquiry cancelled");
        }

        applyStatusChange(enquiry, status);
        enquiryRepository.save(enquiry);

        return ResponseEntity.ok("Enquiry Status Updated Successfully");
    }

    private Enquiry findEnquiry(UUID enquiryId) {
        return enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));
    }

    private Set<Project> resolveAccessibleProjects(AppUserDetails appUserDetails) {
        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            return projectRepository.findAllByOrganization_Id(appUserDetails.getOrgId());
        }

        if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            return employeeRepository.findByUser_Id(UUID.fromString(appUserDetails.getUserId()))
                    .orElseThrow(() -> new AccessDeniedException("Employee not found"))
                    .getProjects();
        }

        throw new AccessDeniedException("User does not have access to the enquiries");
    }

    private void applyLeadFields(Enquiry enquiry, NewEnquiryDTO newEnquiryDTO) {
        enquiry.setLeadName(newEnquiryDTO.leadName());
        enquiry.setLeadMobileNumber(newEnquiryDTO.leadMobileNumber());
        enquiry.setLeadLandlineNumber(newEnquiryDTO.leadLandlineNumber());
        enquiry.setLeadEmail(newEnquiryDTO.leadEmail());
        enquiry.setLeadCity(newEnquiryDTO.leadCity());
        enquiry.setLeadAddress(newEnquiryDTO.leadAddress());
        enquiry.setLeadOccupation(newEnquiryDTO.leadOccupation());
        enquiry.setLeadCompany(newEnquiryDTO.leadCompany());
    }

    private void applyLeadFields(Enquiry enquiry, UpdateEnquiryDTO updateEnquiryDTO) {
        if (updateEnquiryDTO.leadName() != null) {
            enquiry.setLeadName(updateEnquiryDTO.leadName());
        }
        if (updateEnquiryDTO.leadMobileNumber() != null) {
            enquiry.setLeadMobileNumber(updateEnquiryDTO.leadMobileNumber());
        }
        if (updateEnquiryDTO.leadLandlineNumber() != null) {
            enquiry.setLeadLandlineNumber(updateEnquiryDTO.leadLandlineNumber());
        }
        if (updateEnquiryDTO.leadEmail() != null) {
            enquiry.setLeadEmail(updateEnquiryDTO.leadEmail());
        }
        if (updateEnquiryDTO.leadCity() != null) {
            enquiry.setLeadCity(updateEnquiryDTO.leadCity());
        }
        if (updateEnquiryDTO.leadAddress() != null) {
            enquiry.setLeadAddress(updateEnquiryDTO.leadAddress());
        }
        if (updateEnquiryDTO.leadOccupation() != null) {
            enquiry.setLeadOccupation(updateEnquiryDTO.leadOccupation());
        }
        if (updateEnquiryDTO.leadCompany() != null) {
            enquiry.setLeadCompany(updateEnquiryDTO.leadCompany());
        }
    }

    private void applyStatusChange(Enquiry enquiry, Status status) {
        switch (status) {
            case CANCELLED:
                enquiry.setStatus(Status.CANCELLED);
                taskRepository.deleteByFollowUp_Enquiry(enquiry);
                break;
            case BOOKED:
                enquiry.setStatus(Status.BOOKED);
                taskRepository.deleteByFollowUp_Enquiry(enquiry);
                break;
            default:
                enquiry.setStatus(status);
                break;
        }
    }

    private EnquiryResponseDTO toResponseDTO(Enquiry enquiry) {
        return new EnquiryResponseDTO(
                enquiry.getId(),
                enquiry.getProject().getId(),
                enquiry.getProject().getProjectName(),
                enquiry.getPropertyType(),
                enquiry.getProperty(),
                enquiry.getArea(),
                enquiry.getBudget(),
                enquiry.getReference(),
                enquiry.getReferenceName(),
                enquiry.getLeadName(),
                enquiry.getLeadMobileNumber(),
                enquiry.getLeadLandlineNumber(),
                enquiry.getLeadEmail(),
                enquiry.getLeadCity(),
                enquiry.getLeadAddress(),
                enquiry.getLeadOccupation(),
                enquiry.getLeadCompany(),
                enquiry.getStatus(),
                enquiry.getRemark());
    }
}
