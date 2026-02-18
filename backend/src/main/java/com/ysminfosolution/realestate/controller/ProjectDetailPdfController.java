package com.ysminfosolution.realestate.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.ProjectDetailPdfPolicyResponse;
import com.ysminfosolution.realestate.dto.ProjectDetailPdfRequest;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ProjectDetailPdfPolicyService;
import com.ysminfosolution.realestate.service.ProjectDetailPdfService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/projects")
@Slf4j
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ProjectDetailPdfController {

    private final ProjectDetailPdfService projectDetailPdfService;
    private final ProjectDetailPdfPolicyService projectDetailPdfPolicyService;

    @PostMapping("/{projectId}/generate-detail-pdf/{policyId}")
    public ResponseEntity<byte[]> generateProjectDetailPdf(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID policyId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /projects/{projectId}/generate-detail-pdf/{policyId} | Method: generateProjectDetailPdf");

        byte[] pdfBytes = projectDetailPdfService.generateProjectDetailPdf(projectId, policyId, appUserDetails);

        String filename = "project-detail-" + projectId + ".pdf";
        ContentDisposition contentDisposition = ContentDisposition.attachment().filename(filename).build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(pdfBytes);
    }

    @PostMapping("/{projectId}/detail-pdf-policies")
    public ResponseEntity<ProjectDetailPdfPolicyResponse> createPolicy(
            @PathVariable @NotNull UUID projectId,
            @RequestBody @NotNull @Valid ProjectDetailPdfRequest request,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /projects/{projectId}/detail-pdf-policies | Method: createPolicy");

        return ResponseEntity.ok(projectDetailPdfPolicyService.createPolicy(projectId, request, appUserDetails));
    }

    @GetMapping("/{projectId}/detail-pdf-policies")
    public ResponseEntity<List<ProjectDetailPdfPolicyResponse>> getPolicies(
            @PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /projects/{projectId}/detail-pdf-policies | Method: getPolicies");

        return ResponseEntity.ok(projectDetailPdfPolicyService.getPolicies(projectId, appUserDetails));
    }

    @GetMapping("/{projectId}/detail-pdf-policies/{policyId}")
    public ResponseEntity<ProjectDetailPdfPolicyResponse> getPolicy(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID policyId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /projects/{projectId}/detail-pdf-policies/{policyId} | Method: getPolicy");

        return ResponseEntity.ok(projectDetailPdfPolicyService.getPolicy(projectId, policyId, appUserDetails));
    }

    @PutMapping("/{projectId}/detail-pdf-policies/{policyId}")
    public ResponseEntity<ProjectDetailPdfPolicyResponse> updatePolicy(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID policyId,
            @RequestBody @NotNull @Valid ProjectDetailPdfRequest request,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [PUT] /projects/{projectId}/detail-pdf-policies/{policyId} | Method: updatePolicy");

        return ResponseEntity.ok(projectDetailPdfPolicyService.updatePolicy(projectId, policyId, request, appUserDetails));
    }

    @DeleteMapping("/{projectId}/detail-pdf-policies/{policyId}")
    public ResponseEntity<String> deletePolicy(
            @PathVariable @NotNull UUID projectId,
            @PathVariable @NotNull UUID policyId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [DELETE] /projects/{projectId}/detail-pdf-policies/{policyId} | Method: deletePolicy");

        projectDetailPdfPolicyService.deletePolicy(projectId, policyId, appUserDetails);
        return ResponseEntity.ok("Policy deleted successfully");
    }
}
