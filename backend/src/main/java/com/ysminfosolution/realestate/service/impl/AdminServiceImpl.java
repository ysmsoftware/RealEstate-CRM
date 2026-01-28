package com.ysminfosolution.realestate.service.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.EmployeeResponseDTO;
import com.ysminfosolution.realestate.exception.NotFoundException;
import com.ysminfosolution.realestate.model.EmployeeUserInfo;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.EmployeeUserInfoRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.service.AdminService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmployeeUserInfoRepository employeeUserInfoRepository;
    private final ProjectResolver projectResolver;

    

    @Override
    public ResponseEntity<String> allocateEmployeesToProject(UUID projectId, Set<String> list) {

        log.info("\n");
        log.info("Method: allocateEmployeesToProject");

        Project project = projectResolver.resolve(projectId);
        if (project == null || project.isDeleted()) {
            throw new NotFoundException("Project not found");
        }

        boolean partialSuccess = false;

        for (String employeeId : list) {
            EmployeeUserInfo employeeUserInfo = employeeUserInfoRepository.findByUser_UserId(UUID.fromString(employeeId))
                    .orElse(null);

            if (employeeUserInfo == null) {
                partialSuccess = true;
                continue;
            }

            employeeUserInfo.getProjects().add(project);
            employeeUserInfoRepository.save(employeeUserInfo);
        }

        if (partialSuccess) {
            return new ResponseEntity<String>("Some employees were allocated, some were not found", HttpStatus.PARTIAL_CONTENT);
        }

        return ResponseEntity.ok("Employees Allocated to Project Successfully");

    }

    @Override
    public ResponseEntity<Set<EmployeeResponseDTO>> getAllEmployeesOfProject(UUID projectId) {
        
        log.info("\n");
        log.info("Method: getAllEmployeesOfProject");

        Project project = projectResolver.resolve(projectId);
        if (project == null || project.isDeleted()) {
            throw new NotFoundException("Project not found");
        }

        Set<EmployeeResponseDTO> employeeResponseDTOs = new HashSet<>();

        Set<EmployeeUserInfo> employees = employeeUserInfoRepository.findByProjects_ProjectIdAndIsDeletedFalse(projectId);

        for (EmployeeUserInfo employee : employees) {
            EmployeeResponseDTO employeeResponseDTO = new EmployeeResponseDTO(
                employee.getUser().getUserId(), 
                employee.getUser().getFullName(), 
                null, null, null);

            employeeResponseDTOs.add(employeeResponseDTO);
        }

        return ResponseEntity.ok(employeeResponseDTOs);

    }

}
