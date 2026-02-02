package com.ysminfosolution.realestate.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.DashboardProjectDetailedResponseDTO;
import com.ysminfosolution.realestate.dto.DashboardResponseDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.DashboardService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@Validated
@RequestMapping("/dashboard")
@Slf4j
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class DashboardController {

    private final DashboardService dashboardService;
    
    @GetMapping("")
    public ResponseEntity<DashboardResponseDTO> getDashboardData(@AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(dashboardService.getDashboardData(appUserDetails));
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<DashboardProjectDetailedResponseDTO> getDashboardDataForProject(@PathVariable UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {
        return ResponseEntity.ok(dashboardService.getDashboardDataForProject(projectId, appUserDetails));
    }
    
    

}
