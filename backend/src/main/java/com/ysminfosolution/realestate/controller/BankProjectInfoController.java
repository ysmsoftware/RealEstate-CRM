package com.ysminfosolution.realestate.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.NewBankProjectInfoResponseDTO;
import com.ysminfosolution.realestate.model.BankProjectInfo;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.BankProjectInfoService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@Validated
@RequestMapping("/bankProjectInfo")
@Slf4j
@Transactional
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class BankProjectInfoController {

    private final BankProjectInfoService bankProjectInfoService;

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Set<BankProjectInfo>> getAllBankProjectInfoForProjectId(@NotNull @PathVariable UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /bankProjectInfo/project/{projectId} | Method: getAllBankProjectInfoForProjectId");

        return bankProjectInfoService.getAllBankProjectInfoForProjectId(projectId, appUserDetails);
    }
    
    @GetMapping("/{bankProjectInfoId}")
    public ResponseEntity<BankProjectInfo> getById(@NotNull @PathVariable UUID bankProjectInfoId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /bankProjectInfo/{bankProjectInfoId} | Method: getById");

        return bankProjectInfoService.getById(bankProjectInfoId, appUserDetails);
    }
    
    
    @PostMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewBankProjectInfoResponseDTO> createBankProjectInfoForProject(@NotNull @PathVariable UUID projectId, @RequestBody @NotNull BankProjectInfo bankProjectInfo) {

        log.info("\n");
        log.info("Path: [POST] /bankProjectInfo/{projectId} | Method: createBankProjectInfoForProject");

        return bankProjectInfoService.createBankProjectInfo(projectId, bankProjectInfo);
    }

    @PutMapping("/{bankProjectInfoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NewBankProjectInfoResponseDTO> updateBankProjectInfo(@NotNull @PathVariable UUID bankProjectInfoId, @RequestBody @NotNull BankProjectInfo bankProjectInfo) {

        log.info("\n");
        log.info("Path: [PUT] /bankProjectInfo/{bankProjectInfoId} | Method: updateBankProjectInfo");

        return bankProjectInfoService.updateBankProjectInfo(bankProjectInfoId, bankProjectInfo);
    }

    @DeleteMapping("/{bankProjectInfoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteBankProjectInfo(@NotNull @PathVariable UUID bankProjectInfoId) {

        log.info("\n");
        log.info("Path: [DELETE] /bankProjectInfo/{bankProjectInfoId} | Method: deleteBankProjectInfo");

        return bankProjectInfoService.deleteBankProjectInfo(bankProjectInfoId);
    }
    
}
