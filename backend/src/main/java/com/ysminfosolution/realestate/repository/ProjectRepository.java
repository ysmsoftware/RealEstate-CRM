package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.model.Project;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

    @EntityGraph(attributePaths = "organization")
    Optional<Project> findWithOrganizationByProjectId(UUID projectId);


    boolean existsByProjectNameAndOrganization_OrgId(String projectName, UUID organizationId);

    @Query("SELECT p FROM Project p WHERE p.organization.orgId = :orgId AND p.isDeleted = false")
    Set<Project> findAllByOrganization_OrgIdAndIsDeletedFalse(UUID orgId);

    boolean existsByMahareraNo(String mahareraNo);


    Set<Project> findAllByProjectIdInAndIsDeletedFalse(Set<UUID> projectIds);
    
}
