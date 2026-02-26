package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ysminfosolution.realestate.dto.FlatDTO;
import com.ysminfosolution.realestate.dto.FloorDTO;
import com.ysminfosolution.realestate.dto.ProjectBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ProjectDTO;
import com.ysminfosolution.realestate.dto.WingDTO;
import com.ysminfosolution.realestate.dto.maincreationformdtos.ProjectCreationDTO;
import com.ysminfosolution.realestate.error.exception.ApiException;
import com.ysminfosolution.realestate.error.exception.ConflictException;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.EmployeeUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Floor;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.model.Organization;
import com.ysminfosolution.realestate.model.Wing;
import com.ysminfosolution.realestate.model.Project.Status;
import com.ysminfosolution.realestate.repository.EmployeeUserInfoRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FlatRepository;
import com.ysminfosolution.realestate.repository.OrganizationRepository;
import com.ysminfosolution.realestate.repository.ProjectRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.AmenityService;
import com.ysminfosolution.realestate.service.BankProjectInfoService;
import com.ysminfosolution.realestate.service.DisbursementService;
import com.ysminfosolution.realestate.service.DocumentService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;
import com.ysminfosolution.realestate.service.ProjectService;
import com.ysminfosolution.realestate.service.S3StorageService;
import com.ysminfosolution.realestate.service.WingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectServiceImpl implements ProjectService {

    // * Repository
    private final ProjectRepository projectRepository;
    private final OrganizationRepository organizationRepository;
    private final EmployeeUserInfoRepository employeeUserInfoRepository;
    private final EnquiryRepository enquiryRepository;
    private final FlatRepository flatRepository;

    // * All the other services used
    private final WingService wingService;
    private final BankProjectInfoService bankProjectInfoService;
    private final AmenityService amenityService;
    private final DocumentService documentService;
    private final DisbursementService disbursementService;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final ProjectResolver projectResolver;

    private final S3StorageService s3StorageService;

    public Organization getOrganizationById(UUID orgId) {
        return organizationRepository.findById(orgId)
                .filter(o -> !o.isDeleted())
                .orElseThrow(() -> new AccessDeniedException("Invalid Organization"));
    }

    @Override
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Project> createProject(ProjectCreationDTO newProjectDetails, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createProject");

        Project project = new Project();

        Organization organization = getOrganizationById(appUserDetails.getOrgId());

        // ^ Enforcing No more than one project name exists for a single Organization
        if (projectRepository.existsByProjectNameAndOrganization_OrgId(
                newProjectDetails.projectName(),
                organization.getOrgId())) {
            throw new ConflictException("Project name already exists");
        }

        project.setOrganization(organization);
        project.setProjectName(newProjectDetails.projectName());
        project.setProjectAddress(newProjectDetails.projectAddress());
        project.setPincode(newProjectDetails.pincode());
        project.setStartDate(newProjectDetails.startDate());
        project.setCompletionDate(newProjectDetails.completionDate());
        if (projectRepository.existsByMahareraNo(newProjectDetails.mahareraNo())) {
            throw new ConflictException("Maharera number already exists");
        }

        project.setLetterheadUrl(saveLetterHeadToS3(project, newProjectDetails.letterHeadFile(), appUserDetails));

        project.setMahareraNo(newProjectDetails.mahareraNo());
        project.setProgress((short) 0);
        project.setStatus(newProjectDetails.status());
        project.setDeleted(false);

        log.info("Project Created Successfully: " + project.getProjectName());
        Project savedProject = projectRepository.save(project);

        // * Saving Other Info

        // ! In micrroservices architecture, this would trigger an async request (Kafka)
        // to other services

        if (!wingService.createWingsForProject(savedProject, newProjectDetails.wings()) ||
                !bankProjectInfoService.createBankProjectInfosForProject(savedProject,
                        newProjectDetails.projectApprovedBanksInfo())
                ||
                !amenityService.createAmenitiesForProject(savedProject, newProjectDetails.amenities()) ||
                !documentService.createDocumentsForProject(savedProject, newProjectDetails.documents(), appUserDetails)
                ||
                !disbursementService.createDisbursementsForProject(savedProject, newProjectDetails.disbursements())) {

            log.error("Project Creation failed... Rolling Back.");

            // ! I am not performing soft delete here because the project was not completely
            // ! created
            // ! So, it is better to not keep track of broken project
            hardDeleteProjectRecursive(savedProject, appUserDetails);

            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Project creation failed");
        }

        return ResponseEntity.ok(savedProject);

    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Set<Project>> getAllProjects() {

        log.info("\n");
        log.info("Method: getAllProjects");

        return ResponseEntity.ok(projectRepository.findAll()
                .stream().filter(p -> !p.isDeleted()).collect(Collectors.toSet()));
    }

    
    @Override
    @Transactional
    public void hardDeleteProjectRecursive(Project project, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: hardDeleteProjectRecursive");

        deleteProjectDirectoryFromS3(project, appUserDetails);
        wingService.hardDeleteWingsRecursiveByProjectId(project.getProjectId());
        bankProjectInfoService.hardDeleteAllByProjectId(project.getProjectId());
        amenityService.hardDeleteAllByProjectId(project.getProjectId());
        documentService.hardDeleteAllByProjectId(project.getProjectId());
        disbursementService.hardDeleteAllByProjectId(project.getProjectId());

        projectRepository.deleteById(project.getProjectId());
        log.info("ROLLBACK SUCCESSFULY COMPLETED");
    }

    private void deleteProjectDirectoryFromS3(Project project, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: deleteProjectDirectoryFromS3");

        s3StorageService.deleteProjectDirectoryFromS3(project, appUserDetails);
    }

    @Override
    public Project getProjectById(UUID projectId) {

        log.info("\n");
        log.info("Method: getProjectById");

        Project project = projectResolver.resolve(projectId);
        if (project == null || project.isDeleted()) {
            throw new NotFoundException("Project not found");
        }
        return project;
    }

    @Override
    @Transactional
    public ResponseEntity<Project> updateProjectInfo(UUID projectId, Project incomingProject) {

        log.info("\n");
        log.info("Method: updateProjectInfo");

        Project project = projectResolver.resolve(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found for id: " + projectId);
        }

        if (incomingProject.getProgress() == 0) {
            project.setStatus(Status.UPCOMING);
        } else if (incomingProject.getProgress() > 0 && incomingProject.getProgress() < 100) {
            project.setStatus(Status.IN_PROGRESS);
        } else {
            project.setStatus(Status.COMPLETED);
        }

        project.setProjectName(incomingProject.getProjectName());
        project.setProgress(incomingProject.getProgress());
        project.setProjectAddress(incomingProject.getProjectAddress());
        project.setPincode(incomingProject.getPincode());
        project.setStartDate(incomingProject.getStartDate());
        project.setCompletionDate(incomingProject.getCompletionDate());
        project.setMahareraNo(incomingProject.getMahareraNo());

        return ResponseEntity.ok(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ResponseEntity<String> deleteById(UUID projectId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: deleteById");

        Project project = projectResolver.resolve(projectId);

        wingService.deleteWingsRecursiveByProjectId(projectId);
        bankProjectInfoService.deleteBankProjectInfosByProjectId(projectId, appUserDetails);
        amenityService.deleteAmenitiesByProjectId(projectId);
        documentService.deleteDocumentsByProjectId(projectId);
        disbursementService.deleteDisbursementsByProjectId(projectId);

        project.setDeleted(true);
        projectRepository.save(project);

        return ResponseEntity.ok("Project Deleted Successfully");
    }

    // ? Cache can be implemented but a lot of entitie change this data so not
    // ? implementing it
    @Override
    public ResponseEntity<ProjectDTO> getProjectStructureById(UUID projectId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getProjectStructureById");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        Set<Enquiry> projectEnquiries = enquiryRepository
                .findAllByProject_ProjectIdAndIsDeletedFalse(project.getProjectId());

        long projectTotalProperties = flatRepository.countByProject_ProjectIdAndIsDeletedFalse(project.getProjectId());

        long projectBooked = projectEnquiries.stream()
                .filter(e -> e.getStatus() == Enquiry.Status.BOOKED)
                .count();

        int projectCancelled = (int) projectEnquiries.stream()
                .filter(e -> e.getStatus() == Enquiry.Status.CANCELLED)
                .count();

        int projectAvailable = (int) (projectTotalProperties - projectBooked);

        ProjectDTO projectDTO = new ProjectDTO(
                project.getProjectId(),
                project.getProjectName(),
                projectTotalProperties,
                projectBooked,
                projectAvailable,
                projectEnquiries.size(),
                projectCancelled,
                project.getStartDate(),
                project.getCompletionDate(),
                project.getMahareraNo(),
                project.getStatus(),
                project.getProgress(),
                project.getProjectAddress(),
                project.getPincode(),
                new HashSet<>());

        for (Wing wing : wingService.getFullStructureByProjectId(projectId)) {

            if (wing.isDeleted())
                continue;

            WingDTO wingDTO = new WingDTO(
                    wing.getWingId(),
                    wing.getWingName(),
                    wing.getNoOfFloors(),
                    wing.getNoOfProperties(),
                    new TreeSet<>());

            for (Floor floor : wing.getFloors()) {

                if (floor.isDeleted())
                    continue;

                FloorDTO floorDTO = new FloorDTO(
                        floor.getFloorId(),
                        floor.getFloorNo(),
                        floor.getFloorName(),
                        floor.getPropertyType(),
                        floor.getProperty(),
                        floor.getArea(),
                        floor.getQuantity(),
                        new HashSet<>());

                for (Flat flat : floor.getFlats()) {

                    if (flat.isDeleted())
                        continue;

                    FlatDTO flatDTO = new FlatDTO(
                            flat.getPropertyId(),
                            flat.getPropertyNumber(),
                            flat.getStatus(),
                            flat.getArea());

                    floorDTO.flats().add(flatDTO);
                }

                wingDTO.floors().add(floorDTO);
            }

            projectDTO.wings().add(wingDTO);
        }

        return ResponseEntity.ok(projectDTO);

    }

    @Override
    public ResponseEntity<Set<ProjectBasicInfoDTO>> getListofBasicProjectInfo(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getListofBasicProjectInfo");

        Set<ProjectBasicInfoDTO> basicInfoDTOs = new HashSet<>();

        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            Set<Project> projects = projectRepository
                    .findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId()).stream()
                    .filter(p -> !p.isDeleted()).collect(Collectors.toSet());

            for (Project project : projects) {

                ProjectBasicInfoDTO dto = new ProjectBasicInfoDTO(
                        project.getProjectId(),
                        project.getProjectName(),
                        project.getProjectAddress(),
                        project.getPincode(),
                        project.getStatus(),
                        project.getProgress(),
                        project.getStartDate(),
                        project.getCompletionDate());

                basicInfoDTOs.add(dto);
            }

        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            EmployeeUserInfo employee = employeeUserInfoRepository
                    .findByUser_UserId(UUID.fromString(appUserDetails.getUserId()))
                    .orElse(null);
            if (employee == null) {
                return ResponseEntity.ok(Set.of());
            }
            Set<Project> projects = employee.getProjects();

            for (Project project : projects) {

                ProjectBasicInfoDTO dto = new ProjectBasicInfoDTO(
                        project.getProjectId(),
                        project.getProjectName(),
                        project.getProjectAddress(),
                        project.getPincode(),
                        project.getStatus(),
                        project.getProgress(),
                        project.getStartDate(),
                        project.getCompletionDate());

                basicInfoDTOs.add(dto);
            }

        }

        return ResponseEntity.ok(basicInfoDTOs);

    }

    private String saveLetterHeadToS3(Project project,
            MultipartFile letterHead,
            AppUserDetails user) {

        String key = user.getOrgId() + "/" +
                project.getProjectName() + "/LetterHead/" +
                letterHead.getOriginalFilename();

        return s3StorageService.uploadFile(key, letterHead);
    }

}
