package com.ysminfosolution.realestate.service.impl;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.FollowUpBasicInfoDTO;
import com.ysminfosolution.realestate.dto.FollowUpNodeRequestDTO;
import com.ysminfosolution.realestate.dto.FollowUpNodeResponseDTO;
import com.ysminfosolution.realestate.dto.FollowUpResponseDTO;
import com.ysminfosolution.realestate.exception.NotFoundException;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.EmployeeUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.FollowUpNode;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Task;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.EmployeeUserInfoRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FollowUpRepository;
import com.ysminfosolution.realestate.repository.ProjectRepository;
import com.ysminfosolution.realestate.repository.TaskRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.FollowUpNodeService;
import com.ysminfosolution.realestate.service.FollowUpService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class FollowUpServiceImpl implements FollowUpService {

    // * Repository
    private final FollowUpRepository followUpRepository;
    private final ProjectRepository projectRepository;
    private final EnquiryRepository enquiryRepository;
    private final EmployeeUserInfoRepository employeeUserInfoRepository;
    private final TaskRepository taskRepository;

    // * Services
    private final FollowUpNodeService followUpNodeService;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final ProjectResolver projectResolver;

    @Override
    public boolean createFollowUpForEnquiry(Enquiry enquiry, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: createFollowUpForEnquiry");

        FollowUp followUp = new FollowUp();

        followUp.setDescription("First FollowUp");
        followUp.setEnquiry(enquiry);
        followUp.setFollowUpNextDate(LocalDate.now().plusDays(3));
        followUp.setDeleted(false);

        try {
            followUpRepository.save(followUp);
        } catch (Exception e) {
            return false;
        }

        return followUpNodeService.createFirstNodeForFollowUp(followUp, appUserDetails);
    }

    @Override
    public ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUpsForProject(UUID projectId,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllFollowUpsForProject");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        // Fetch all followUps with enquiry and client in one query
        Set<FollowUp> followUps = followUpRepository.findAllByProjectIdWithFetch(projectId);

        // Batch fetch all followUpNodes for all followUps in one query
        Set<UUID> followUpIds = followUps.stream()
                .map(FollowUp::getFollowUpId)
                .collect(Collectors.toSet());
        Set<FollowUpNode> allFollowUpNodes = followUpNodeService.getAllByFollowUpIds(followUpIds);

        // Group followUpNodes by followUpId
        Map<UUID, Set<FollowUpNode>> nodesByFollowUpId = allFollowUpNodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.getFollowUp().getFollowUpId(),
                        Collectors.toSet()));

        Set<FollowUpResponseDTO> followUpResponseDTOs = new HashSet<>();

        for (FollowUp followUp : followUps) {
            // Get nodes for this followUp from the map
            Set<FollowUpNode> followUpNodes = nodesByFollowUpId.getOrDefault(
                    followUp.getFollowUpId(), Set.of());

            Set<FollowUpNodeResponseDTO> followUpNodeResponseDTOs = followUpNodes.stream()
                    .map(node -> new FollowUpNodeResponseDTO(
                            node.getFollowUpNodeId(),
                            node.getFollowUpDateTime(),
                            node.getBody(),
                            node.getTag(),
                            node.getUser().getFullName()))
                    .collect(Collectors.toSet());

            ClientUserInfo client = followUp.getEnquiry().getClient();

            FollowUpResponseDTO followUpResponseDTO = new FollowUpResponseDTO(
                    followUp.getFollowUpId(),
                    followUp.getEnquiry().getEnquiryId(),
                    followUp.getFollowUpNextDate(),
                    followUp.getDescription(),
                    followUpNodeResponseDTOs,
                    client.getClientName(),
                    client.getEmail(),
                    client.getMobileNumber());

            followUpResponseDTOs.add(followUpResponseDTO);
        }

        return ResponseEntity.ok(followUpResponseDTOs);
    }

    @Override
    @SuppressWarnings("null")
    public ResponseEntity<FollowUpResponseDTO> getFollowUpForEnquiry(
            UUID enquiryId,
            AppUserDetails appUserDetails) {

        log.info("Method: getFollowUpForEnquiry");

        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        if (enquiry.isDeleted()) {
            throw new NotFoundException("Enquiry not found");
        }

        // ✅ Resolve project ONCE (request-scoped)
        Project project = projectResolver.resolve(
                enquiry.getProject().getProjectId());

        // ✅ DB-free authorization
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        FollowUp followUp = followUpRepository.findByEnquiryIdWithFetch(enquiryId)
                .orElseThrow(() -> new NotFoundException("Follow-up not found"));

        Set<FollowUpNodeResponseDTO> followUpNodeResponseDTOs = new HashSet<>();

        Set<FollowUpNode> followUpNodes = followUpNodeService.getAllByFollowUpId(followUp.getFollowUpId());

        for (FollowUpNode followUpNode : followUpNodes) {
            followUpNodeResponseDTOs.add(
                    new FollowUpNodeResponseDTO(
                            followUpNode.getFollowUpNodeId(),
                            followUpNode.getFollowUpDateTime(),
                            followUpNode.getBody(),
                            followUpNode.getTag(),
                            followUpNode.getUser().getFullName()));
        }

        ClientUserInfo client = followUp.getEnquiry().getClient();

        FollowUpResponseDTO followUpResponseDTO = new FollowUpResponseDTO(
                followUp.getFollowUpId(),
                followUp.getEnquiry().getEnquiryId(),
                followUp.getFollowUpNextDate(),
                followUp.getDescription(),
                followUpNodeResponseDTOs,
                client.getClientName(),
                client.getEmail(),
                client.getMobileNumber());

        return ResponseEntity.ok(followUpResponseDTO);
    }

    @Override
    public ResponseEntity<FollowUpResponseDTO> getById(UUID followUpId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getById");

        FollowUp followUp = followUpRepository.findByIdWithFetch(followUpId)
                .orElseThrow(() -> new NotFoundException("FollowUp not found"));

        // ✅ Resolve project ONCE (request-scoped)
        Project project = projectResolver.resolve(
                followUp.getEnquiry().getProject().getProjectId());

        // ✅ DB-free authorization
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        Set<FollowUpNodeResponseDTO> followUpNodeResponseDTOs = new HashSet<>();

        Set<FollowUpNode> followUpNodes = followUpNodeService.getAllByFollowUpId(followUp.getFollowUpId());

        for (FollowUpNode followUpNode : followUpNodes) {
            FollowUpNodeResponseDTO followUpNodeResponseDTO = new FollowUpNodeResponseDTO(
                    followUpNode.getFollowUpNodeId(),
                    followUpNode.getFollowUpDateTime(),
                    followUpNode.getBody(),
                    followUpNode.getTag(),
                    followUpNode.getUser().getFullName());

            followUpNodeResponseDTOs.add(followUpNodeResponseDTO);
        }

        ClientUserInfo client = followUp.getEnquiry().getClient();

        // ~ Then Process FollowUp and add the FollowUpNodes
        FollowUpResponseDTO followUpResponseDTO = new FollowUpResponseDTO(
                followUp.getFollowUpId(),
                followUp.getEnquiry().getEnquiryId(),
                followUp.getFollowUpNextDate(),
                followUp.getDescription(),
                followUpNodeResponseDTOs,
                client.getClientName(),
                client.getEmail(),
                client.getMobileNumber());

        return ResponseEntity.ok(followUpResponseDTO);
    }

    @Override
    @SuppressWarnings("null")
    public ResponseEntity<String> addNodeToFollowUp(UUID followUpId, FollowUpNodeRequestDTO nodeRequestDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: addNodeToFollowUp");

        FollowUp followUp = followUpRepository.findById(followUpId)
                .orElseThrow(() -> new NotFoundException("FollowUp not found"));

        // ✅ Resolve project ONCE (request-scoped)
        Project project = projectResolver.resolve(
                followUp.getEnquiry().getProject().getProjectId());

        // ✅ DB-free authorization
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);
        followUp.setFollowUpNextDate(nodeRequestDTO.followUpNextDate());

        // * Create Task if FollowUpNextDate is Today
        if (followUp.getFollowUpNextDate().isEqual(LocalDate.now()) && !taskRepository.existsByFollowUp(followUp)) {
            Task task = new Task();
            task.setFollowUp(followUp);
            taskRepository.save(task);
        }

        followUpRepository.save(followUp);

        followUpNodeService.createNodeForFollowUp(followUp, nodeRequestDTO, appUserDetails);

        return ResponseEntity.ok("Node Added To FollowUp Successfully");
    }

    @Override
    public ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUps(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllFollowUps");

        Set<Project> projects;

        // ~ Admin
        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            projects = projectRepository.findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId());

            // ~ Employee
        } else {
            EmployeeUserInfo employee = employeeUserInfoRepository
                    .findByEmployeeIdAndIsDeletedFalse(UUID.fromString(appUserDetails.getUserId()));
            projects = employee.getProjects();
        }

        Set<FollowUpResponseDTO> followUpResponseDTOs = new HashSet<>();

        for (Project project : projects) {
            followUpResponseDTOs.addAll(getAllFollowUpsForProject(project.getProjectId(), null).getBody());
        }

        return ResponseEntity.ok(followUpResponseDTOs);

    }

    @Override
    public ResponseEntity<Set<FollowUpBasicInfoDTO>> getAllRemainingFollowUpsWithinRange(AppUserDetails appUserDetails,
            LocalDate from, LocalDate to) {

        log.info("\n");
        log.info("Method: getAllRemainingFollowUpsWithinRange");

        Set<FollowUpBasicInfoDTO> basicInfoDTOs = new HashSet<>();
        Set<Project> projects = null;

        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            projects = projectRepository.findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId());

        } else if (appUserDetails.getRole().equals(User.Role.EMPLOYEE)) {
            projects = employeeUserInfoRepository.findByUser_UserId(UUID.fromString(appUserDetails.getUserId())).get()
                    .getProjects().stream()
                    .filter(p -> !p.isDeleted())
                    .collect(Collectors.toSet());

        }

        Set<FollowUp> followUps = new HashSet<>();

        if (from == null && to != null) {
            followUps.addAll(followUpRepository.findAllByProjectsWithFetch(projects)
                    .stream()
                    .filter(f -> !f.getFollowUpNextDate().isAfter(to))
                    .toList());

        } else if (to == null && from != null) {
            followUps.addAll( followUpRepository.findAllByProjectsWithFetch(projects)
                    .stream()
                    .filter(f -> !f.getFollowUpNextDate().isBefore(from))
                    .toList());

        } else if (from == null && to == null) {
            followUps.addAll(taskRepository.findAllByProjectsWithFetch(projects).stream().map(Task::getFollowUp).toList());

        } else {
            followUps.addAll(followUpRepository.findAllByProjectsWithFetch(projects)
                    .stream()
                    .filter(f -> !f.getFollowUpNextDate().isAfter(to) && !f.getFollowUpNextDate().isBefore(from))
                    .toList());

        }

        // Batch fetch all followUpNodes for all followUps in one query
        Set<UUID> followUpIds = followUps.stream()
                .map(FollowUp::getFollowUpId)
                .collect(Collectors.toSet());
        Set<FollowUpNode> allFollowUpNodes = followUpNodeService.getAllByFollowUpIds(followUpIds);

        // Group followUpNodes by followUpId and find latest for each
        java.util.Map<UUID, FollowUpNode> latestNodeByFollowUpId = allFollowUpNodes.stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        node -> node.getFollowUp().getFollowUpId(),
                        java.util.stream.Collectors.collectingAndThen(
                                java.util.stream.Collectors.toList(),
                                nodes -> nodes.stream()
                                        .sorted(Comparator.comparing(FollowUpNode::getFollowUpDateTime).reversed())
                                        .findFirst()
                                        .orElse(null))));

        for (FollowUp followUp : followUps) {
            FollowUpNode latestNode = latestNodeByFollowUpId.get(followUp.getFollowUpId());
            ClientUserInfo client = followUp.getEnquiry().getClient();

            FollowUpBasicInfoDTO basicInfoDTO = new FollowUpBasicInfoDTO(
                    followUp.getFollowUpId(),
                    client.getClientName(),
                    client.getMobileNumber(),
                    followUp.getFollowUpNextDate(),
                    latestNode != null ? latestNode.getUser().getFullName() : null,
                    followUp.getDescription());

            basicInfoDTOs.add(basicInfoDTO);
        }

        return ResponseEntity.ok(basicInfoDTOs);

    }

}
