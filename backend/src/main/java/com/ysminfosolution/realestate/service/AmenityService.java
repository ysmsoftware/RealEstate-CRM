package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.NewAmenityResponse;
import com.ysminfosolution.realestate.dto.maincreationformdtos.AmenityCreationDTO;
import com.ysminfosolution.realestate.model.Amenity;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface AmenityService {

    Boolean createAmenitiesForProject(Project savedProject, Set<AmenityCreationDTO> amenities);

    void hardDeleteAllByProjectId(UUID projectId);
    
    ResponseEntity<NewAmenityResponse> addAmenityToProject(UUID projectId, Amenity amenity);

    ResponseEntity<NewAmenityResponse> updateAmenity(UUID amenityId, Amenity amenity);

    ResponseEntity<String> deleteById(UUID amenityId);

    ResponseEntity<Set<Amenity>> getAllAmenitiesForProject(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<Amenity> getAmenityById(UUID amenityId, AppUserDetails appUserDetails);

    void deleteAmenitiesByProjectId(UUID projectId);
    
}
