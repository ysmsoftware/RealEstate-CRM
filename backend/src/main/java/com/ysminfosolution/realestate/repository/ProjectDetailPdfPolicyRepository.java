package com.ysminfosolution.realestate.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.ProjectDetailPdfPolicy;

public interface ProjectDetailPdfPolicyRepository extends JpaRepository<ProjectDetailPdfPolicy, UUID> {

    List<ProjectDetailPdfPolicy> findByProject_ProjectId(UUID projectId);

    Optional<ProjectDetailPdfPolicy> findByProjectDetailPdfPolicyIdAndProject_ProjectId(
            UUID projectDetailPdfPolicyId,
            UUID projectId);

    boolean existsByProject_ProjectIdAndPolicyNameIgnoreCase(UUID projectId, String policyName);

    boolean existsByProject_ProjectIdAndPolicyNameIgnoreCaseAndProjectDetailPdfPolicyIdNot(
            UUID projectId,
            String policyName,
            UUID projectDetailPdfPolicyId);
}
