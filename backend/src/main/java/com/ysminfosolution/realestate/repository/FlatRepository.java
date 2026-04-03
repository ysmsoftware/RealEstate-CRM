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

    void deleteAllByFloor_Id(UUID floorId);

    Set<Flat> findAllByFloor_Id(UUID floorId);

    // ~ Solves the lazy loading (Could Not Initialize Entity, No Active Session) problem by join fetching the specified columns
    @EntityGraph(attributePaths = {"wing", "floor", "project"})
    Set<Flat> findAllByProject_Id(UUID projectId);

    Set<Flat> findAllByProject_IdIn(List<UUID> projectIds);
    
    long countByProject_IdIn(List<UUID> projectIds);

    @Query("""
            SELECT f.project.id AS projectId, COUNT(f) AS totalProperties
            FROM Flat f
            WHERE f.project.id IN :projectIds
              AND f.deleted = false
            GROUP BY f.project.id
            """)
    List<ProjectFlatCount> countActiveFlatsByProjectIds(@Param("projectIds") List<UUID> projectIds);

    Optional<Flat> findById(UUID propertyId);

    long countByProject_Id(UUID projectId);
    
}
