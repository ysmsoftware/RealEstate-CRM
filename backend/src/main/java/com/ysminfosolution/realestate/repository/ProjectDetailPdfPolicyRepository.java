package com.ysminfosolution.realestate.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.ProjectDetailPdfPolicy;

public interface ProjectDetailPdfPolicyRepository extends JpaRepository<ProjectDetailPdfPolicy, UUID> {

    List<ProjectDetailPdfPolicy> findByProject_Id(UUID projectId);

    Optional<ProjectDetailPdfPolicy> findByIdAndProject_Id(
            UUID projectDetailPdfPolicyId,
            UUID projectId);

    boolean existsByProject_IdAndPolicyNameIgnoreCase(UUID projectId, String policyName);

    boolean existsByProject_IdAndPolicyNameIgnoreCaseAndIdNot(
            UUID projectId,
            String policyName,
            UUID projectDetailPdfPolicyId);
}
