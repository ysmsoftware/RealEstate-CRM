package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.EmployeeUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Floor.PropertyType;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.repository.ClientUserInfoRepository;
import com.ysminfosolution.realestate.repository.EmployeeUserInfoRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FloorRepository;
import com.ysminfosolution.realestate.repository.ProjectRepository;
import com.ysminfosolution.realestate.repository.TaskRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ClientService;
import com.ysminfosolution.realestate.service.EnquiryService;
import com.ysminfosolution.realestate.service.FollowUpService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EnquiryServiceImpl implements EnquiryService {

    // * Repository
    private final EnquiryRepository enquiryRepository;
    private final ClientUserInfoRepository clientUserInfoRepository;
    private final ProjectRepository projectRepository;
    private final EmployeeUserInfoRepository employeeUserInfoRepository;
    private final TaskRepository taskRepository;
    private final FloorRepository floorRepository;

    // * Other Services
    private final ClientService clientService;
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
        enquiry.setPropertyType(newEnquiryDTO.propertyType());
        enquiry.setProperty(newEnquiryDTO.property());
        enquiry.setArea(newEnquiryDTO.area());
        enquiry.setBudget(newEnquiryDTO.budget());
        enquiry.setStatus(Status.ONGOING);
        enquiry.setReference(newEnquiryDTO.reference());
        enquiry.setReferenceName(newEnquiryDTO.referenceName());
        enquiry.setRemark(null);

        ClientUserInfo client = clientUserInfoRepository.findByEmail(newEnquiryDTO.email()).orElse(null);

        // ^ Using same email is allowed for multiple clients. The clients will be
        // considered same if email matches.
        // if (client != null) {
        // throw new ConflictException("Client with the given email already exists.
        // Please use existing client or provide a different email.");
        // }

        client = new ClientUserInfo();

        client.setClientName(newEnquiryDTO.clientName());
        client.setAddress(newEnquiryDTO.address());
        client.setCity(newEnquiryDTO.city());
        client.setCompany(newEnquiryDTO.company());
        client.setEmail(newEnquiryDTO.email());
        client.setLandlineNumber(newEnquiryDTO.landlineNumber());
        client.setMobileNumber(newEnquiryDTO.mobileNumber());
        client.setOccupation(newEnquiryDTO.occupation());
        client.setDeleted(false);
        client = clientService.createClientForEnquiry(client);

        enquiry.setClient(client);
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

        EnquiryResponseDTO responseDTO = new EnquiryResponseDTO(
                enquiry.getEnquiryId(),
                enquiry.getProject().getProjectId(),
                enquiry.getProject().getProjectName(),
                enquiry.getPropertyType(),
                enquiry.getProperty(),
                enquiry.getArea(),
                enquiry.getBudget(),
                enquiry.getReference(),
                enquiry.getReferenceName(),
                enquiry.getClient().getClientId(),
                enquiry.getClient().getClientName(),
                enquiry.getClient().getMobileNumber(),
                enquiry.getClient().getLandlineNumber(),
                enquiry.getClient().getEmail(),
                enquiry.getClient().getCity(),
                enquiry.getClient().getAddress(),
                enquiry.getClient().getOccupation(),
                enquiry.getClient().getCompany(),
                enquiry.getStatus(),
                enquiry.getRemark());

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    public ResponseEntity<EnquiryResponseDTO> createNewEnquiryForClient(NewEnquiryDTO newEnquiryDTO,
            @NonNull UUID clientId,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createNewEnquiryForClient");

        Project project = projectResolver.resolve(newEnquiryDTO.projectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        Enquiry enquiry = new Enquiry();

        enquiry.setProject(project);
        enquiry.setPropertyType(newEnquiryDTO.propertyType());
        enquiry.setProperty(newEnquiryDTO.property());
        enquiry.setArea(newEnquiryDTO.area());
        enquiry.setBudget(newEnquiryDTO.budget());
        enquiry.setStatus(Status.ONGOING);
        enquiry.setReference(newEnquiryDTO.reference());
        enquiry.setReferenceName(newEnquiryDTO.referenceName());
        enquiry.setRemark(null);

        ClientUserInfo client = clientUserInfoRepository.findById(clientId).orElse(null);
        if (client == null || client.isDeleted()) {
            throw new NotFoundException("Client not found");
        }

        enquiry.setClient(client);
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

        EnquiryResponseDTO responseDTO = new EnquiryResponseDTO(
                enquiry.getEnquiryId(),
                enquiry.getProject().getProjectId(),
                enquiry.getProject().getProjectName(),
                enquiry.getPropertyType(),
                enquiry.getProperty(),
                enquiry.getArea(),
                enquiry.getBudget(),
                enquiry.getReference(),
                enquiry.getReferenceName(),
                enquiry.getClient().getClientId(),
                enquiry.getClient().getClientName(),
                enquiry.getClient().getMobileNumber(),
                enquiry.getClient().getLandlineNumber(),
                enquiry.getClient().getEmail(),
                enquiry.getClient().getCity(),
                enquiry.getClient().getAddress(),
                enquiry.getClient().getOccupation(),
                enquiry.getClient().getCompany(),
                enquiry.getStatus(),
                enquiry.getRemark());

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiries(
            AppUserDetails appUserDetails) {

        log.info("Method: getAllEnquiries");

        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            return ResponseEntity.ok(enquiryRepository.findAllEnquiriesForOrg(appUserDetails.getOrgId()));

        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {

            EmployeeUserInfo employee = employeeUserInfoRepository
                    .findByUser_UserId(UUID.fromString(appUserDetails.getUserId()))
                    .orElseThrow(() -> new AccessDeniedException("Employee not found"));

            Set<Project> allocatedProjects = employee.getProjects();

            if (allocatedProjects.isEmpty()) {
                return ResponseEntity.ok(Set.of());
            }

            Set<EnquiryResponseDTO> enquiries = enquiryRepository
                    .findAllEnquiriesForProjects(allocatedProjects);

            return ResponseEntity.ok(enquiries);

        } else {
            throw new AccessDeniedException("User does not have access to the enquiries");
        }

    }

    @Override
    public ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiriesForProject(UUID projectId,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllEnquiriesForProject");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(enquiryRepository.findAll()
                .stream()
                .filter(e -> projectId.equals(e.getProject().getProjectId()))
                .map(enquiry -> new EnquiryResponseDTO(
                        enquiry.getEnquiryId(),
                        enquiry.getProject().getProjectId(),
                        enquiry.getProject().getProjectName(),
                        enquiry.getPropertyType(),
                        enquiry.getProperty(),
                        enquiry.getArea(),
                        enquiry.getBudget(),
                        enquiry.getReference(),
                        enquiry.getReferenceName(),
                        enquiry.getClient().getClientId(),
                        enquiry.getClient().getClientName(),
                        enquiry.getClient().getMobileNumber(),
                        enquiry.getClient().getLandlineNumber(),
                        enquiry.getClient().getEmail(),
                        enquiry.getClient().getCity(),
                        enquiry.getClient().getAddress(),
                        enquiry.getClient().getOccupation(),
                        enquiry.getClient().getCompany(),
                        enquiry.getStatus(),
                        enquiry.getRemark()))
                .collect(Collectors.toSet()));
    }

    @Override
    public ResponseEntity<EnquiryResponseDTO> getById(@NonNull UUID enquiryId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getById");

        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        Project project = projectResolver.resolve(enquiry.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        EnquiryResponseDTO responseDTO = new EnquiryResponseDTO(
                enquiry.getEnquiryId(),
                enquiry.getProject().getProjectId(),
                enquiry.getProject().getProjectName(),
                enquiry.getPropertyType(),
                enquiry.getProperty(),
                enquiry.getArea(),
                enquiry.getBudget(),
                enquiry.getReference(),
                enquiry.getReferenceName(),
                enquiry.getClient().getClientId(),
                enquiry.getClient().getClientName(),
                enquiry.getClient().getMobileNumber(),
                enquiry.getClient().getLandlineNumber(),
                enquiry.getClient().getEmail(),
                enquiry.getClient().getCity(),
                enquiry.getClient().getAddress(),
                enquiry.getClient().getOccupation(),
                enquiry.getClient().getCompany(),
                enquiry.getStatus(),
                enquiry.getRemark());

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    public ResponseEntity<String> cancelEnquiryWithRemark(@NonNull UUID enquiryId, String remark,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: cancelEnquiryWithRemark");

        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        Project project = projectResolver.resolve(enquiry.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        enquiry.setRemark(remark);
        enquiry.setStatus(Status.CANCELLED);

        // * Remove FollowUp associated with Enquiry from the Tasks
        taskRepository.deleteByFollowUp_Enquiry(enquiry);

        // ^ Still visible to the user therefore not deleted
        // enquiry.setDeleted(true);

        // followUpService.deleteFollowUpForEnquiry(enquiryId);

        try {
            enquiryRepository.save(enquiry);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Error occured while cancelling enquiry");
        }

        return ResponseEntity.ok("Enquiry Cancelled Successfully");
    }

    @Override
    public ResponseEntity<Set<EnquiryBasicInfoDTO>> getListOfEnquiryBasicInfo(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getListOfEnquiryBasicInfo");

        Set<EnquiryBasicInfoDTO> basicInfoDTOs = new HashSet<>();
        Set<Project> projects = new HashSet<>();

        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            projects = projectRepository.findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId());

        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            projects = employeeUserInfoRepository.findByUser_UserId(UUID.fromString(appUserDetails.getUserId())).get()
                    .getProjects();

        }

        for (Project project : projects) {

            Set<Enquiry> enquiries = enquiryRepository
                    .findAllByProject_ProjectIdAndIsDeletedFalse(project.getProjectId());
            for (Enquiry enquiry : enquiries) {

                EnquiryBasicInfoDTO basicInfoDTO = new EnquiryBasicInfoDTO(
                        enquiry.getEnquiryId(),
                        enquiry.getCreatedAt(),
                        enquiry.getClient().getClientName(),
                        project.getProjectId(),
                        project.getProjectName(),
                        enquiry.getBudget(),
                        enquiry.getStatus());

                basicInfoDTOs.add(basicInfoDTO);
            }

        }

        return ResponseEntity.ok(basicInfoDTOs);

    }

    @Override
    public ResponseEntity<String> updateEnquiry(@NonNull UUID enquiryId, UpdateEnquiryDTO updateEnquiryDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: updateEnquiry");

        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        Project project = projectResolver.resolve(enquiry.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        ClientUserInfo clientUserInfo = enquiry.getClient();

        if (clientUserInfo == null) {
            throw new NotFoundException("Client not found for enquiry");
        }

        if (updateEnquiryDTO.clientName() != null)
            clientUserInfo.setClientName(updateEnquiryDTO.clientName());

        if (updateEnquiryDTO.address() != null)
            clientUserInfo.setAddress(updateEnquiryDTO.address());

        if (updateEnquiryDTO.city() != null)
            clientUserInfo.setCity(updateEnquiryDTO.city());

        if (updateEnquiryDTO.landlineNumber() != null)
            clientUserInfo.setLandlineNumber(updateEnquiryDTO.landlineNumber());

        if (updateEnquiryDTO.email() != null)
            clientUserInfo.setEmail(updateEnquiryDTO.email());

        if (updateEnquiryDTO.mobileNumber() != null)
            clientUserInfo.setMobileNumber(updateEnquiryDTO.mobileNumber());

        if (updateEnquiryDTO.occupation() != null)
            clientUserInfo.setOccupation(updateEnquiryDTO.occupation());

        if (updateEnquiryDTO.company() != null)
            clientUserInfo.setCompany(updateEnquiryDTO.company());

        clientUserInfoRepository.save(clientUserInfo);

        if (updateEnquiryDTO.budget() != null)
            enquiry.setBudget(updateEnquiryDTO.budget());

        if (updateEnquiryDTO.status() != null) {
            changeEnquiryStatus(enquiryId, updateEnquiryDTO.status(), appUserDetails);
        }

        if (updateEnquiryDTO.reference() != null)
            enquiry.setReference(updateEnquiryDTO.reference());

        if (updateEnquiryDTO.referenceName() != null)
            enquiry.setReferenceName(updateEnquiryDTO.referenceName());

        if (updateEnquiryDTO.remark() != null)
            enquiry.setRemark(updateEnquiryDTO.remark());

        if (updateEnquiryDTO.propertyType() != null)
            enquiry.setPropertyType(updateEnquiryDTO.propertyType());

        if (updateEnquiryDTO.property() != null)
            enquiry.setProperty(updateEnquiryDTO.property());

        if (updateEnquiryDTO.area() != null)
            enquiry.setArea(updateEnquiryDTO.area());

        enquiryRepository.save(enquiry);

        return ResponseEntity.ok("Enquiry Updated Successfully");
    }

    @Override
    public ResponseEntity<Set<EnquiryBasicInfoDTO>> getListOfEnquiryBasicInfoForClient(AppUserDetails appUserDetails,
            UUID clientId) {

        log.info("\n");
        log.info("Method: getListOfEnquiryBasicInfoForClient");

        ClientUserInfo clientUserInfo = clientUserInfoRepository.findByClientIdAndIsDeletedFalse(clientId).orElse(null);
        if (clientUserInfo == null) {
            throw new NotFoundException("Client not found");
        }

        Set<EnquiryBasicInfoDTO> basicInfoDTOs = new HashSet<>();
        Set<Enquiry> enquiries = enquiryRepository.findAllByClientAndProject_Organization_OrgIdAndIsDeletedFalse(
                clientUserInfo, appUserDetails.getOrgId());

        for (Enquiry enquiry : enquiries) {

            EnquiryBasicInfoDTO basicInfoDTO = new EnquiryBasicInfoDTO(
                    enquiry.getEnquiryId(),
                    enquiry.getCreatedAt(),
                    enquiry.getClient().getClientName(),
                    enquiry.getProject().getProjectId(),
                    enquiry.getProject().getProjectName(),
                    enquiry.getBudget(),
                    enquiry.getStatus());

            basicInfoDTOs.add(basicInfoDTO);
        }

        return ResponseEntity.ok(basicInfoDTOs);

    }

    @Override
    public ResponseEntity<EnquiryPropertyOptions> getAllPropertyOptionsForProject(
            UUID projectId, AppUserDetails appUserDetails) {

        log.info("\nMethod: getAllPropertyOptionsForProject");

        // * Authorization
        Project project = projectResolver.resolve(projectId);
        if (project == null || project.isDeleted()) {
            throw new NotFoundException("Project not found");
        }
        if (!project.getOrganization().getOrgId().equals(appUserDetails.getOrgId())) {
            throw new AccessDeniedException("User is not authorized to access this project");
        }

        // * Get flat rows
        Set<PropertyOptionsFlat> flatRows = floorRepository.getFlatPropertyOptions(projectId);

        // * Build nested response
        Map<PropertyType, Map<String, Map<Double, Long>>> grouped = flatRows.stream().collect(
                Collectors.groupingBy(
                        PropertyOptionsFlat::getPropertyType,
                        Collectors.groupingBy(
                                PropertyOptionsFlat::getProperty,
                                Collectors.groupingBy(
                                        PropertyOptionsFlat::getArea,
                                        Collectors.summingLong(PropertyOptionsFlat::getQuantity)))));

        // Build DTO hierarchy
        Set<PropertyTypeOption> typeOptions = grouped.entrySet().stream()
                .map(typeEntry -> {

                    Set<PropertyOption> propertyOptions = typeEntry.getValue().entrySet().stream()
                            .map(propEntry -> {

                                Set<AreaOptions> areaOptions = propEntry.getValue().entrySet().stream()
                                        .map(areaEntry -> new AreaOptions(
                                                areaEntry.getKey(),
                                                areaEntry.getValue()))
                                        .collect(Collectors.toSet());

                                long propertiesAvailable = areaOptions.stream()
                                        .mapToLong(AreaOptions::propertiesAvailable).sum();

                                return new PropertyOption(
                                        propEntry.getKey(),
                                        areaOptions,
                                        propertiesAvailable);
                            })
                            .collect(Collectors.toSet());

                    long totalForType = propertyOptions.stream().mapToLong(PropertyOption::propertiesAvailable).sum();

                    return new PropertyTypeOption(
                            typeEntry.getKey(),
                            propertyOptions,
                            totalForType);
                })
                .collect(Collectors.toSet());

        long totalAvailable = typeOptions.stream().mapToLong(PropertyTypeOption::propertiesAvailable).sum();

        EnquiryPropertyOptions response = new EnquiryPropertyOptions(typeOptions, totalAvailable);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<String> changeEnquiryStatus(@NonNull UUID enquiryId, Status status,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: changeEnquiryStatus");

        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        Project project = projectResolver.resolve(enquiry.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        switch (status) {
            case CANCELLED:
                enquiry.setRemark("Enquiry cancelled");
                enquiry.setStatus(Status.CANCELLED);

                // * Remove FollowUp associated with Enquiry from the Tasks
                taskRepository.deleteByFollowUp_Enquiry(enquiry);
                break;

            // ! This is only temporary until the booking flow is implemented
            case BOOKED:

            
                enquiry.setStatus(Status.BOOKED);

            default:
                enquiry.setStatus(status);
                break;
        }

        enquiryRepository.save(enquiry);

        return ResponseEntity.ok("Status changed successfully");

    }

}
