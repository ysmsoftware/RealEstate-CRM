package com.ysminfosolution.realestate.service.impl;

import java.time.LocalDate;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.FollowUpBasicInfoDTO;
import com.ysminfosolution.realestate.dto.FollowUpNodeRequestDTO;
import com.ysminfosolution.realestate.dto.FollowUpNodeResponseDTO;
import com.ysminfosolution.realestate.dto.FollowUpResponseDTO;
import com.ysminfosolution.realestate.error.exception.ApiException;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Employee;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.FollowUpNode;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Task;
import com.ysminfosolution.realestate.model.User;
import com.ysminfosolution.realestate.repository.EmployeeRepository;
import com.ysminfosolution.realestate.repository.EnquiryRepository;
import com.ysminfosolution.realestate.repository.FollowUpRepository;
import com.ysminfosolution.realestate.repository.FollowUpNodeRepository;
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

    private final FollowUpRepository followUpRepository;
        private final FollowUpNodeRepository followUpNodeRepository;
    private final ProjectRepository projectRepository;
    private final EnquiryRepository enquiryRepository;
    private final EmployeeRepository employeeRepository;
    private final TaskRepository taskRepository;

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

        return ResponseEntity.ok(getFollowUpsForProject(projectId));
    }

    @Override
    public ResponseEntity<FollowUpResponseDTO> getFollowUpForEnquiry(
            UUID enquiryId,
            AppUserDetails appUserDetails) {

        log.info("Method: getFollowUpForEnquiry");

        Enquiry enquiry = enquiryRepository.findById(enquiryId)
                .orElseThrow(() -> new NotFoundException("Enquiry not found"));

        if (enquiry.isDeleted()) {
            throw new NotFoundException("Enquiry not found");
        }

        Project project = projectResolver.resolve(enquiry.getProject().getId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        FollowUp followUp = followUpRepository.findByEnquiryIdWithFetch(enquiryId)
                .orElseThrow(() -> new NotFoundException("Follow-up not found"));

        Set<FollowUpNodeResponseDTO> nodeDTOs = mapNodeResponseDTOs(
                followUpNodeService.getAllByFollowUpId(followUp.getId()));

        return ResponseEntity.ok(toResponseDTO(followUp, nodeDTOs));
    }

    @Override
    public ResponseEntity<FollowUpResponseDTO> getById(UUID followUpId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getById");

        FollowUp followUp = followUpRepository.findByIdWithFetch(followUpId)
                .orElseThrow(() -> new NotFoundException("FollowUp not found"));

        Project project = projectResolver.resolve(followUp.getEnquiry().getProject().getId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        Set<FollowUpNodeResponseDTO> nodeDTOs = mapNodeResponseDTOs(
                followUpNodeService.getAllByFollowUpId(followUp.getId()));

        return ResponseEntity.ok(toResponseDTO(followUp, nodeDTOs));
    }

    @Override
    public ResponseEntity<String> addNodeToFollowUp(UUID followUpId, FollowUpNodeRequestDTO nodeRequestDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: addNodeToFollowUp");

        FollowUp followUp = followUpRepository.findByFollowUpIdAndIsDeletedFalse(followUpId)
                .orElseThrow(() -> new NotFoundException("FollowUp not found"));

        Enquiry enquiry = followUp.getEnquiry();

        if (enquiry.getStatus().equals(Enquiry.Status.CANCELLED) || enquiry.getStatus().equals(Enquiry.Status.BOOKED)) {
            throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                    "Cannot add follow-up activity to a completed enquiry or cancelled enquiry");
        }

        Project project = projectResolver.resolve(followUp.getEnquiry().getProject().getId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);
        followUp.setFollowUpNextDate(nodeRequestDTO.followUpNextDate());

        if (followUp.getFollowUpNextDate().isBefore(LocalDate.now().plusDays(1))
                && !taskRepository.existsByFollowUp(followUp)) {
            Task task = new Task();
            task.setFollowUp(followUp);
            taskRepository.save(task);
        }

        followUpRepository.save(followUp);
        followUpNodeService.createNodeForFollowUp(followUp, nodeRequestDTO, appUserDetails);

        return ResponseEntity.ok("Node Added To FollowUp Successfully");
    }

    @Override
    public ResponseEntity<String> updateNodeToFollowUp(UUID followUpId, UUID nodeId, FollowUpNodeRequestDTO nodeRequestDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: updateNodeToFollowUp");

        FollowUp followUp = followUpRepository.findByFollowUpIdAndIsDeletedFalse(followUpId)
                .orElseThrow(() -> new NotFoundException("FollowUp not found"));

        Enquiry enquiry = followUp.getEnquiry();

        if (enquiry.getStatus().equals(Enquiry.Status.CANCELLED) || enquiry.getStatus().equals(Enquiry.Status.BOOKED)) {
            throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                    "Cannot update follow-up activity for a completed enquiry or cancelled enquiry");
        }

        Project project = projectResolver.resolve(followUp.getEnquiry().getProject().getId());
        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        FollowUpNode latestNode = followUpNodeRepository
                .findFirstByFollowUp_FollowUpIdAndIsDeletedFalseOrderByFollowUpDateTimeDesc(followUpId)
                .orElseThrow(() -> new NotFoundException("Follow-up node not found"));

        FollowUpNode nodeToUpdate = followUpNodeRepository.findByFollowUpIdAndNodeId(followUpId, nodeId)
                .orElseThrow(() -> new NotFoundException("Follow-up node not found"));

        if (!latestNode.getId().equals(nodeToUpdate.getId())) {
            throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                    "Only the latest follow-up node can be edited");
        }

        Instant createdAt = nodeToUpdate.getCreatedAt();
        Instant now = Instant.now();
        if (createdAt == null || !createdAt.plus(24, ChronoUnit.HOURS).isAfter(now)) {
            throw new ApiException(HttpStatus.METHOD_NOT_ALLOWED,
                    "Follow-up node can only be edited within 24 hours of creation");
        }

        followUp.setFollowUpNextDate(nodeRequestDTO.followUpNextDate());
        if (followUp.getFollowUpNextDate().isBefore(LocalDate.now().plusDays(1))
                && !taskRepository.existsByFollowUp(followUp)) {
            Task task = new Task();
            task.setFollowUp(followUp);
            taskRepository.save(task);
        }

        nodeToUpdate.setBody(nodeRequestDTO.body());
        nodeToUpdate.setTag(nodeRequestDTO.tag());

        followUpRepository.save(followUp);
        followUpNodeRepository.save(nodeToUpdate);

        return ResponseEntity.ok("Node Updated Successfully");
    }

    @Override
    public ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUps(AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllFollowUps");

        Set<Project> projects = resolveAccessibleProjects(appUserDetails);
        Set<FollowUpResponseDTO> followUpResponseDTOs = new HashSet<>();

        for (Project project : projects) {
            followUpResponseDTOs.addAll(getFollowUpsForProject(project.getId()));
        }

        return ResponseEntity.ok(followUpResponseDTOs);
    }

    @Override
    public ResponseEntity<Set<FollowUpBasicInfoDTO>> getAllRemainingFollowUpsWithinRange(AppUserDetails appUserDetails,
            LocalDate from, LocalDate to) {

        log.info("\n");
        log.info("Method: getAllRemainingFollowUpsWithinRange");

        Set<Project> projects = resolveAccessibleProjects(appUserDetails);
        Set<FollowUp> followUps = new HashSet<>();

        if (from == null && to != null) {
            followUps.addAll(followUpRepository.findAllByProjectsWithFetch(projects)
                    .stream()
                    .filter(f -> !f.getFollowUpNextDate().isAfter(to))
                    .toList());
        } else if (to == null && from != null) {
            followUps.addAll(followUpRepository.findAllByProjectsWithFetch(projects)
                    .stream()
                    .filter(f -> !f.getFollowUpNextDate().isBefore(from))
                    .toList());
        } else if (from == null) {
            followUps.addAll(taskRepository.findAllByProjectsWithFetch(projects).stream()
                    .map(Task::getFollowUp)
                    .toList());
        } else {
            followUps.addAll(followUpRepository.findAllByProjectsWithFetch(projects)
                    .stream()
                    .filter(f -> !f.getFollowUpNextDate().isAfter(to) && !f.getFollowUpNextDate().isBefore(from))
                    .toList());
        }

        Set<UUID> followUpIds = followUps.stream()
                .map(FollowUp::getId)
                .collect(Collectors.toSet());
        Set<FollowUpNode> allFollowUpNodes = followUpIds.isEmpty()
                ? Set.of()
                : followUpNodeService.getAllByFollowUpIds(followUpIds);

        Map<UUID, FollowUpNode> latestNodeByFollowUpId = allFollowUpNodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.getFollowUp().getId(),
                        Collectors.collectingAndThen(Collectors.toList(), nodes -> nodes.stream()
                                .sorted(Comparator.comparing(FollowUpNode::getFollowUpDateTime).reversed())
                                .findFirst()
                                .orElse(null))));

        Set<FollowUpBasicInfoDTO> basicInfoDTOs = new HashSet<>();
        for (FollowUp followUp : followUps) {
            FollowUpNode latestNode = latestNodeByFollowUpId.get(followUp.getId());
            Enquiry enquiry = followUp.getEnquiry();

            basicInfoDTOs.add(new FollowUpBasicInfoDTO(
                    followUp.getId(),
                    enquiry.getLeadName(),
                    enquiry.getLeadMobileNumber(),
                    followUp.getFollowUpNextDate(),
                    latestNode != null ? latestNode.getUser().getFullName() : null,
                    followUp.getDescription()));
        }

        return ResponseEntity.ok(basicInfoDTOs);
    }

    private Set<Project> resolveAccessibleProjects(AppUserDetails appUserDetails) {
        if (appUserDetails.getRole().equals(User.Role.ADMIN)) {
            return projectRepository.findAllByOrganization_OrgIdAndIsDeletedFalse(appUserDetails.getOrgId());
        }

        Employee employee = employeeRepository
                .findByUser_UserId(UUID.fromString(appUserDetails.getUserId()))
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        return employee.getProjects().stream()
                .filter(project -> !project.isDeleted())
                .collect(Collectors.toSet());
    }

    private Set<FollowUpResponseDTO> getFollowUpsForProject(UUID projectId) {
        Set<FollowUp> followUps = followUpRepository.findAllByProjectIdWithFetch(projectId);
        Set<UUID> followUpIds = followUps.stream()
                .map(FollowUp::getId)
                .collect(Collectors.toSet());

        Set<FollowUpNode> allFollowUpNodes = followUpIds.isEmpty()
                ? Set.of()
                : followUpNodeService.getAllByFollowUpIds(followUpIds);

        Map<UUID, Set<FollowUpNodeResponseDTO>> nodeDTOsByFollowUpId = allFollowUpNodes.stream()
                .collect(Collectors.groupingBy(
                        node -> node.getFollowUp().getId(),
                        Collectors.collectingAndThen(Collectors.toSet(), this::mapNodeResponseDTOs)));

        Set<FollowUpResponseDTO> followUpResponseDTOs = new HashSet<>();
        for (FollowUp followUp : followUps) {
            followUpResponseDTOs.add(toResponseDTO(
                    followUp,
                    nodeDTOsByFollowUpId.getOrDefault(followUp.getId(), Set.of())));
        }
        return followUpResponseDTOs;
    }

    private Set<FollowUpNodeResponseDTO> mapNodeResponseDTOs(Set<FollowUpNode> followUpNodes) {
        return followUpNodes.stream()
                .map(node -> new FollowUpNodeResponseDTO(
                        node.getId(),
                        node.getFollowUpDateTime(),
                        node.getCreatedAt(),
                        node.getBody(),
                        node.getTag(),
                        node.getUser().getFullName()))
                .collect(Collectors.toSet());
    }

    private FollowUpResponseDTO toResponseDTO(FollowUp followUp, Set<FollowUpNodeResponseDTO> nodeDTOs) {
        Enquiry enquiry = followUp.getEnquiry();
        return new FollowUpResponseDTO(
                followUp.getId(),
                enquiry.getId(),
                followUp.getFollowUpNextDate(),
                followUp.getDescription(),
                nodeDTOs,
                enquiry.getLeadName(),
                enquiry.getLeadEmail(),
                enquiry.getLeadMobileNumber());
    }
}
