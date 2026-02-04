package com.ysminfosolution.realestate.controller;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.maincreationformdtos.DisbursementCreationDTO;
import com.ysminfosolution.realestate.model.Disbursement;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.DisbursementService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;



@RestController
@Validated
@RequestMapping("/disbursements")
@Slf4j
@Transactional
@RequiredArgsConstructor
public class DisbursementController {

    private final DisbursementService disbursementService;

    @GetMapping("/{projectId}")
    public ResponseEntity<Set<Disbursement>> getAllDisbursementsByProjectId(@PathVariable @NotNull UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /disbursements/{projectId} | Method: getAllDisbursementsByProjectId");

        return disbursementService.getAllDisbursementsByProjectId(projectId, appUserDetails);
    }
    

    @PutMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<Disbursement>> changeDisbursementsForProject(@PathVariable @NotNull UUID projectId, @RequestBody @NotEmpty @Valid List<DisbursementCreationDTO> disbursements) {

        log.info("\n");
        log.info("Path: [PUT] /disbursements/{projectId} | Method: changeDisbursementsForProject");

        return disbursementService.changeDisbursementsForProject(projectId, disbursements);
    }
}