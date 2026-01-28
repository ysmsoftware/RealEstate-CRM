package com.ysminfosolution.realestate.service.impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.NewAmenityResponse;
import com.ysminfosolution.realestate.dto.maincreationformdtos.AmenityCreationDTO;
import com.ysminfosolution.realestate.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Amenity;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.AmenityRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.AmenityService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class AmenityServiceImpl implements AmenityService {

    private final ProjectAuthorizationService projectAuthorizationService;

    // * Repositories
    private final AmenityRepository amenityRepository;

    private final ProjectResolver projectResolver;

    @Override
    public Boolean createAmenitiesForProject(Project savedProject, Set<AmenityCreationDTO> amenities) {

        log.info("\n");
        log.info("Method: createAmenitiesForProject");

        try {
            for (AmenityCreationDTO amenityDTO : amenities) {
                Amenity amenity = new Amenity();
                amenity.setAmenityName(amenityDTO.amenityName());
                amenity.setProject(savedProject);
                amenity.setDeleted(false);

                amenityRepository.save(amenity);
            }
            log.info("AMENITIES created successfully for Project : {}", savedProject.getProjectName());
            return true;
        } catch (Exception e) {
            log.error("Error occurred while creating Amenities for Project: {}\nRolling back...",
                    savedProject.getProjectName(), e);
            return false;
        }
    }

    @Override
    public void hardDeleteAllByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: hardDeleteAllByProjectId");

        amenityRepository.deleteAllByProject_ProjectId(projectId);
        log.info("Amenities deleted successfully for projectId : {}", projectId);
    }

    @Override
    public ResponseEntity<NewAmenityResponse> addAmenityToProject(UUID projectId, Amenity amenity) {

        log.info("\n");
        log.info("Method: addAmenityToProject");

        Project project = projectResolver.resolve(projectId);

        if (project == null) {
            throw new NotFoundException("Project not found");
        }

        amenity.setDeleted(false);
        amenity.setProject(project);
        Amenity savedAmenity = amenityRepository.save(amenity);

        NewAmenityResponse amenityResponse = new NewAmenityResponse(
            savedAmenity.getAmenityId(),
            savedAmenity.getAmenityName(),
            savedAmenity.getProject().getProjectId()
        );

        return ResponseEntity.ok(amenityResponse);
    }

    @Override
    @SuppressWarnings("null")
    public ResponseEntity<NewAmenityResponse> updateAmenity(UUID amenityId, Amenity amenityBody) {

        log.info("\n");
        log.info("Method: updateAmenity");

        Amenity amenity = amenityRepository.findById(amenityId).orElse(null);

        amenity.setAmenityName(amenityBody.getAmenityName());
        Amenity savedAmenity = amenityRepository.save(amenity);

        NewAmenityResponse amenityResponse = new NewAmenityResponse(
            savedAmenity.getAmenityId(),
            savedAmenity.getAmenityName(),
            savedAmenity.getProject().getProjectId()
        );

        return ResponseEntity.ok(amenityResponse);
    }

    @Override
    @SuppressWarnings("null")
    public ResponseEntity<String> deleteById(UUID amenityId) {

        log.info("\n");
        log.info("Method: deleteById");

        Amenity amenity = amenityRepository.findById(amenityId).orElse(null);
        if (amenity == null) {
            throw new NotFoundException("Amenity not found");
        }
        amenity.setDeleted(true);
        amenityRepository.save(amenity);
        return ResponseEntity.ok("Deleted Successfully");
    }

    @Override
    public ResponseEntity<Set<Amenity>> getAllAmenitiesForProject(UUID projectId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllAmenitiesForProject");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(amenityRepository.findAllByProject_ProjectId(projectId)
            .stream().filter(amenity -> !amenity.isDeleted()).collect(Collectors.toSet()));
    }

    @Override
    public ResponseEntity<Amenity> getAmenityById(UUID amenityId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAmenityById");

        Amenity amenity = amenityRepository.findByAmenityIdAndIsDeletedFalse(amenityId).orElseThrow(() -> new NotFoundException("Amenity not found"));

        Project project = projectResolver.resolve(amenity.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(amenity);
    }

    @Override
    public void deleteAmenitiesByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: deleteAmenitiesByProjectId");

        Set<Amenity> amenities = getAllAmenitiesForProject(projectId, null).getBody();

        if (amenities == null) {
            return;
        }

        for (Amenity amenity : amenities) {
            deleteById(amenity.getAmenityId());
        }
    }
    
}
