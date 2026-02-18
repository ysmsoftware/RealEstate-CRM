package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.UUID;

import com.ysminfosolution.realestate.dto.ProjectDetailPdfPolicyResponse;
import com.ysminfosolution.realestate.dto.ProjectDetailPdfRequest;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface ProjectDetailPdfPolicyService {

    ProjectDetailPdfPolicyResponse createPolicy(UUID projectId, ProjectDetailPdfRequest request, AppUserDetails user);

    List<ProjectDetailPdfPolicyResponse> getPolicies(UUID projectId, AppUserDetails user);

    ProjectDetailPdfPolicyResponse getPolicy(UUID projectId, UUID policyId, AppUserDetails user);

    ProjectDetailPdfPolicyResponse updatePolicy(UUID projectId, UUID policyId, ProjectDetailPdfRequest request,
            AppUserDetails user);

    void deletePolicy(UUID projectId, UUID policyId, AppUserDetails user);
}
