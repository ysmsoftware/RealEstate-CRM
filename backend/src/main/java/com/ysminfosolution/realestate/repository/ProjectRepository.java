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
    Optional<Project> findWithOrganizationById(UUID projectId);


    boolean existsByProjectNameAndOrganization_Id(String projectName, UUID organizationId);

    @Query("SELECT p FROM Project p WHERE p.organization.id = :orgId AND p.deleted = false")
    Set<Project> findAllByOrganization_Id(UUID orgId);

    boolean existsByMahareraNo(String mahareraNo);


    Set<Project> findAllByIdIn(Set<UUID> projectIds);
    
}
