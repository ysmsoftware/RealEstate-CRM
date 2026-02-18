package com.ysminfosolution.realestate.service;

import java.util.UUID;

import com.ysminfosolution.realestate.security.AppUserDetails;

public interface ProjectDetailPdfService {

    byte[] generateProjectDetailPdf(
            UUID projectId,
            UUID policyId,
            AppUserDetails user);
}
