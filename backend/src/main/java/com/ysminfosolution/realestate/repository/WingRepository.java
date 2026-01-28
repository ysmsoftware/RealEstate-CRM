package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.Wing;

public interface WingRepository extends JpaRepository<Wing, UUID> {

    // Existing methods (keep them)
    Set<Wing> findAllByProject_ProjectId(UUID projectId);

    Set<Wing> findAllByProject_ProjectIdAndIsDeletedFalse(UUID projectId);

    // ~ Full structure fetch (Wing → Floor → Flat)
    @Query("""
        select distinct w
        from Wing w
        left join fetch w.floors f
        left join fetch f.flats fl
        where w.project.projectId = :projectId
          and w.isDeleted = false
          and (f is null or f.isDeleted = false)
          and (fl is null or fl.isDeleted = false)
    """)
    Set<Wing> fetchFullStructureByProjectId(@Param("projectId") UUID projectId);
}
