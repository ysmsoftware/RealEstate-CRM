package com.ysminfosolution.realestate.service.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.ProjectDetailPdfPolicyResponse;
import com.ysminfosolution.realestate.dto.ProjectDetailPdfRequest;
import com.ysminfosolution.realestate.error.exception.ConflictException;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Document;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.ProjectDetailPdfPolicy;
import com.ysminfosolution.realestate.repository.DocumentRepository;
import com.ysminfosolution.realestate.repository.ProjectDetailPdfPolicyRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;
import com.ysminfosolution.realestate.service.ProjectDetailPdfPolicyService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectDetailPdfPolicyServiceImpl implements ProjectDetailPdfPolicyService {

    private final ProjectResolver projectResolver;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final ProjectDetailPdfPolicyRepository projectDetailPdfPolicyRepository;
    private final DocumentRepository documentRepository;

    @Override
    @Transactional
    public ProjectDetailPdfPolicyResponse createPolicy(UUID projectId, ProjectDetailPdfRequest request,
            AppUserDetails user) {
        Project project = resolveAuthorizedProject(projectId, user);

        String policyName = normalizePolicyName(request.policyName());
        if (projectDetailPdfPolicyRepository.existsByProject_ProjectIdAndPolicyNameIgnoreCase(projectId, policyName)) {
            throw new ConflictException("Policy name already exists for this project");
        }

        ProjectDetailPdfPolicy policy = new ProjectDetailPdfPolicy();
        policy.setProject(project);
        policy.setPolicyName(policyName);
        applyRequest(policy, request, projectId);

        return toResponse(projectDetailPdfPolicyRepository.save(policy));
    }

    @Override
    public List<ProjectDetailPdfPolicyResponse> getPolicies(UUID projectId, AppUserDetails user) {
        resolveAuthorizedProject(projectId, user);

        return projectDetailPdfPolicyRepository.findByProject_ProjectId(projectId)
                .stream()
                .sorted(Comparator.comparing(
                        ProjectDetailPdfPolicy::getCreatedAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Override
    public ProjectDetailPdfPolicyResponse getPolicy(UUID projectId, UUID policyId, AppUserDetails user) {
        resolveAuthorizedProject(projectId, user);
        return toResponse(getPolicyEntity(projectId, policyId));
    }

    @Override
    @Transactional
    public ProjectDetailPdfPolicyResponse updatePolicy(
            UUID projectId,
            UUID policyId,
            ProjectDetailPdfRequest request,
            AppUserDetails user) {
        resolveAuthorizedProject(projectId, user);

        ProjectDetailPdfPolicy policy = getPolicyEntity(projectId, policyId);

        String policyName = normalizePolicyName(request.policyName());
        if (projectDetailPdfPolicyRepository
                .existsByProject_ProjectIdAndPolicyNameIgnoreCaseAndProjectDetailPdfPolicyIdNot(projectId, policyName,
                        policyId)) {
            throw new ConflictException("Policy name already exists for this project");
        }

        policy.setPolicyName(policyName);
        applyRequest(policy, request, projectId);

        return toResponse(projectDetailPdfPolicyRepository.save(policy));
    }

    @Override
    @Transactional
    public void deletePolicy(UUID projectId, UUID policyId, AppUserDetails user) {
        resolveAuthorizedProject(projectId, user);
        ProjectDetailPdfPolicy policy = getPolicyEntity(projectId, policyId);
        projectDetailPdfPolicyRepository.delete(policy);
    }

    private Project resolveAuthorizedProject(UUID projectId, AppUserDetails user) {
        Project project = projectResolver.resolve(projectId);
        projectAuthorizationService.checkProjectAccess(user, project);
        return project;
    }

    private ProjectDetailPdfPolicy getPolicyEntity(UUID projectId, UUID policyId) {
        return projectDetailPdfPolicyRepository
                .findByProjectDetailPdfPolicyIdAndProject_ProjectId(policyId, projectId)
                .orElseThrow(() -> new NotFoundException("Project detail PDF policy not found for id: " + policyId));
    }

    private String normalizePolicyName(String policyName) {
        if (policyName == null || policyName.isBlank()) {
            throw new IllegalArgumentException("Policy name is required");
        }
        return policyName.trim();
    }

    private void applyRequest(ProjectDetailPdfPolicy policy, ProjectDetailPdfRequest request, UUID projectId) {
        List<UUID> normalizedDocumentIds = normalizeDocumentIds(request.documentIds());
        List<UUID> validDocumentIds = sanitizeDocumentIdsForProject(
                projectId,
                request.includeDocuments(),
                normalizedDocumentIds);

        policy.setIncludeProjectOverview(request.includeProjectOverview());
        policy.setIncludeWings(request.includeWings());
        policy.setIncludeFloors(request.includeFloors());
        policy.setIncludeFlats(request.includeFlats());
        policy.setIncludeBankProjectInfo(request.includeBankProjectInfo());
        policy.setIncludeAmenities(request.includeAmenities());
        policy.setIncludeDisbursements(request.includeDisbursements());
        policy.setIncludeDocuments(request.includeDocuments());
        policy.setDocumentIds(validDocumentIds);
    }

    private List<UUID> sanitizeDocumentIdsForProject(
            UUID projectId,
            boolean includeDocuments,
            List<UUID> normalizedDocumentIds) {

        if (!includeDocuments) {
            return new ArrayList<>();

        }

        if (normalizedDocumentIds.isEmpty()) {
            throw new IllegalArgumentException("documentIds must be provided when includeDocuments is true");
        }

        List<Document> validDocuments = documentRepository
                .findAllByProject_ProjectIdAndDocumentIdInAndIsDeletedFalse(projectId, normalizedDocumentIds);

        Map<UUID, Document> validDocumentById = validDocuments.stream()
                .collect(Collectors.toMap(Document::getDocumentId, document -> document));

        List<UUID> orderedValidDocumentIds = normalizedDocumentIds.stream()
                .filter(validDocumentById::containsKey)
                .collect(Collectors.toCollection(ArrayList::new));

        if (orderedValidDocumentIds.isEmpty()) {
            throw new IllegalArgumentException("No valid project documents were found for the given documentIds");
        }

        return orderedValidDocumentIds;
    }

    private List<UUID> normalizeDocumentIds(List<UUID> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return new ArrayList<>();

        }

        return documentIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toCollection(ArrayList::new));

    }

    private ProjectDetailPdfPolicyResponse toResponse(ProjectDetailPdfPolicy policy) {
        return new ProjectDetailPdfPolicyResponse(
                policy.getProjectDetailPdfPolicyId(),
                policy.getPolicyName(),
                policy.isIncludeProjectOverview(),
                policy.isIncludeWings(),
                policy.isIncludeFloors(),
                policy.isIncludeFlats(),
                policy.isIncludeBankProjectInfo(),
                policy.isIncludeAmenities(),
                policy.isIncludeDisbursements(),
                policy.isIncludeDocuments(),
                new ArrayList<>(policy.getDocumentIds()),
                policy.getCreatedAt(),
                policy.getUpdatedAt());
    }
}
