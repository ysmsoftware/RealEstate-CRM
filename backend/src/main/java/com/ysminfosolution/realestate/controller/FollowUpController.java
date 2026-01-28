package com.ysminfosolution.realestate.controller;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.FollowUpBasicInfoDTO;
import com.ysminfosolution.realestate.dto.FollowUpNodeRequestDTO;
import com.ysminfosolution.realestate.dto.FollowUpResponseDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.FollowUpService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@Validated
@RequestMapping("/followUps")
@RequiredArgsConstructor
@Slf4j
@Transactional
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class FollowUpController {

    private final FollowUpService followUpService;

    @GetMapping("/tasks")
    public ResponseEntity<Set<FollowUpBasicInfoDTO>> getAllRemainingFollowUpsWithinRange(
            @AuthenticationPrincipal AppUserDetails appUserDetails, 
            @RequestParam(required = false) LocalDate fromDate,
            @RequestParam(required = false) LocalDate toDate) {

        log.info("\n");
        log.info("Path: [GET] /followUps/tasks | Method: getAllRemainingFollowUps");

        return followUpService.getAllRemainingFollowUpsWithinRange(appUserDetails, fromDate, toDate);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUpsForProject(@PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /followUps/project/{projectId} | Method: getAllFollowUpsForProject");

        return followUpService.getAllFollowUpsForProject(projectId, appUserDetails);
    }


    @GetMapping("/enquiry/{enquiryId}")
    public ResponseEntity<FollowUpResponseDTO> getFollowUpForEnquiry(@PathVariable @NotNull UUID enquiryId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /followUps/enquiry/{enquiryId} | Method: getFollowUpForEnquiry");

        return followUpService.getFollowUpForEnquiry(enquiryId, appUserDetails);
    }

    @GetMapping("/{followUpId}")
    public ResponseEntity<FollowUpResponseDTO> getById(@PathVariable @NotNull UUID followUpId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /followUps/{followUpId} | Method: getById");

        return followUpService.getById(followUpId, appUserDetails);
    }

    @PostMapping("/{followUpId}/node")
    public ResponseEntity<String> addNodeToFollowUp(@PathVariable @NotNull UUID followUpId,
            @RequestBody FollowUpNodeRequestDTO nodeRequestDTO,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /followUps/{followUpId}/node | Method: addNodeToFollowUp");

        return followUpService.addNodeToFollowUp(followUpId, nodeRequestDTO, appUserDetails);
    }

    @GetMapping("")
    public ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUps(
            @AuthenticationPrincipal AppUserDetails appUserDetails) {
        log.info("\n");
        log.info("Path: [POST] /followUps | Method: getAllFollowUps");

        return followUpService.getAllFollowUps(appUserDetails);
    }

}
