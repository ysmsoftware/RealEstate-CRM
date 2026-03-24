package com.ysminfosolution.realestate.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.Flat;


public interface FlatRepository extends JpaRepository<Flat, UUID> {

    interface ProjectFlatCount {
        UUID getProjectId();
        Long getTotalProperties();
    }

    void deleteAllByFloor_FloorId(UUID floorId);

    Set<Flat> findAllByFloor_FloorId(UUID floorId);

    // ~ Solves the lazy loading (Could Not Initialize Entity, No Active Session) problem by join fetching the specified columns
    @EntityGraph(attributePaths = {"wing", "floor", "project"})
    Set<Flat> findAllByProject_ProjectId(UUID projectId);

    Set<Flat> findAllByProject_ProjectIdInAndIsDeletedFalse(List<UUID> projectIds);
    
    long countByProject_ProjectIdInAndIsDeletedFalse(List<UUID> projectIds);

    @Query("""
            SELECT f.project.projectId AS projectId, COUNT(f) AS totalProperties
            FROM Flat f
            WHERE f.project.projectId IN :projectIds
              AND f.isDeleted = false
            GROUP BY f.project.projectId
            """)
    List<ProjectFlatCount> countActiveFlatsByProjectIds(@Param("projectIds") List<UUID> projectIds);

    Optional<Flat> findByPropertyIdAndIsDeletedFalse(UUID propertyId);

    long countByProject_ProjectIdAndIsDeletedFalse(UUID projectId);
    
}
