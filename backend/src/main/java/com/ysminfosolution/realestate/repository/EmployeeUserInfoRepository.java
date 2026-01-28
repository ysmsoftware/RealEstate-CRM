package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.EmployeeUserInfo;

public interface EmployeeUserInfoRepository extends JpaRepository<EmployeeUserInfo, UUID> {

    @EntityGraph(attributePaths = {"projects", "user"})
    Optional<EmployeeUserInfo> findByUser_UserId(UUID userId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Set<EmployeeUserInfo> findAllByUser_Organization_OrgIdAndIsDeletedFalse(UUID orgId);

    @EntityGraph(attributePaths = {"projects", "user"})
    EmployeeUserInfo findByEmployeeIdAndIsDeletedFalse(UUID employeeId);

    @EntityGraph(attributePaths = {"projects", "user"})
    Set<EmployeeUserInfo> findByProjects_ProjectIdAndIsDeletedFalse(UUID projectId);

    boolean existsByUser_UserId(UUID userId);

}
