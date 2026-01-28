package com.ysminfosolution.realestate.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.EmployeeResponseDTO;
import com.ysminfosolution.realestate.service.AdminService;

import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@Validated
@RequestMapping("/admins")
@RequiredArgsConstructor
@Slf4j
@Transactional
@PreAuthorize("hasAnyRole('ADMIN', 'DEVELOPER')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/project-employees/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Set<EmployeeResponseDTO>> getAllEmployeesOfProject(@PathVariable UUID projectId) {

        log.info("\n");
        log.info("Path: [GET] /project-employees/{projectId} | Method: getAllEmployeesOfProject");
            
        return adminService.getAllEmployeesOfProject(projectId);
    }
    


    @PutMapping("/allocate/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> allocateEmployeesToProject(@PathVariable UUID projectId, @RequestBody @NotEmpty Set<String> list) {

        log.info("\n");
        log.info("Path: [POST] /admins/allocate/{projectId} | Method: allocateEmployeesToProject");
        
        return adminService.allocateEmployeesToProject(projectId, list);
    }
    
    
}
