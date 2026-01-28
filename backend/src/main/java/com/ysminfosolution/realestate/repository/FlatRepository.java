package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Flat;


public interface FlatRepository extends JpaRepository<Flat, UUID> {

    void deleteAllByFloor_FloorId(UUID floorId);

    Set<Flat> findAllByFloor_FloorId(UUID floorId);

    // ~ Solves the lazy loading (Could Not Initialize Entity, No Active Session) problem by join fetching the specified columns
    @EntityGraph(attributePaths = {"wing", "floor", "project"})
    Set<Flat> findAllByProject_ProjectId(UUID projectId);

    Optional<Flat> findByPropertyIdAndIsDeletedFalse(UUID propertyId);
    
}
