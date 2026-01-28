package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.EmployeeResponseDTO;

public interface AdminService {

    ResponseEntity<String> allocateEmployeesToProject(UUID projectId, Set<String> list);

    ResponseEntity<Set<EmployeeResponseDTO>> getAllEmployeesOfProject(UUID projectId);

}
