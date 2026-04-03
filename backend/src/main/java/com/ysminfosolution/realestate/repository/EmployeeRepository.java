package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    @EntityGraph(attributePaths = {"projects", "user"})
    Optional<Employee> findByUser_Id(UUID userId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Set<Employee> findAllByUser_Organization_Id(UUID orgId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Optional<Employee> findById(UUID employeeId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Set<Employee> findByProjects_Id(UUID projectId);

    boolean existsByUser_Id(UUID userId);

}
