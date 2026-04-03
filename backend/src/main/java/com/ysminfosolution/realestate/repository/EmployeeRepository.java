package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @EntityGraph(attributePaths = {"projects", "user"})
    Optional<Employee> findByUser_UserId(UUID userId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Set<Employee> findAllByUser_Organization_OrgIdAndIsDeletedFalse(UUID orgId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Employee findByEmployeeIdAndIsDeletedFalse(UUID employeeId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Set<Employee> findByProjects_ProjectIdAndIsDeletedFalse(UUID projectId);

    boolean existsByUser_UserId(UUID userId);

}
