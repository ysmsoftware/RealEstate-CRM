package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Amenity;

public interface AmenityRepository extends JpaRepository<Amenity, UUID> {

    Set<Amenity> findAllByProject_Id(UUID projectId);

    Set<Amenity> findAllByProject_IdAndIsDeletedFalse(UUID projectId);

    void deleteAllByProject_Id(UUID projectId);

    Optional<Amenity> findByAmenityIdAndIsDeletedFalse(UUID amenityId);

    
}
