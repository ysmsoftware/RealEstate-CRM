package com.ysminfosolution.realestate.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.NewAmenityResponse;
import com.ysminfosolution.realestate.model.Amenity;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.AmenityService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;





@RestController
@Validated
@RequestMapping("/amenities")
@Slf4j
@Transactional
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class AmenityController {

    private final AmenityService amenityService;

    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Set<Amenity>> getAllAmenitiesForProject(@NotNull @PathVariable UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /amenities/project/{projectId} | Method: getAllAmenitiesForProject");

        return amenityService.getAllAmenitiesForProject(projectId, appUserDetails);
    }
    
    @GetMapping("/{amenityId}")
    public ResponseEntity<Amenity> getAmenityById(@NotNull @PathVariable UUID amenityId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /amenities/{amenityId} | Method: getAmenityById");

        return amenityService.getAmenityById(amenityId, appUserDetails);
    }
    

    @PreAuthorize("hasAnyRole('ADMIN')")
    @PostMapping("/{projectId}")
    public ResponseEntity<NewAmenityResponse> addAmenityToProject(@NotNull @PathVariable UUID projectId, @RequestBody Amenity amenity) {

        log.info("\n");
        log.info("Path: [POST] /amenities/{projectId} | Method: addAmenityToProject");

        return amenityService.addAmenityToProject(projectId, amenity);
    }
    
    @PreAuthorize("hasAnyRole('ADMIN')")
    @PutMapping("/{amenityId}")
    public ResponseEntity<NewAmenityResponse> updateAmenity(@NotNull @PathVariable UUID amenityId, @RequestBody Amenity amenity) {

        log.info("\n");
        log.info("Path: [PUT] /amenities/{amenityId} | Method: updateAmenity");

        return amenityService.updateAmenity(amenityId, amenity);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{amenityId}")
    public ResponseEntity<String> deleteAmenity(@NotNull @PathVariable UUID amenityId) {
        amenityService.deleteById(amenityId);

        log.info("\n");
        log.info("Path: [DELETE] /amenities/{amenityId} | Method: deleteAmenity");

        return ResponseEntity.ok("Deleted Successfully");
    }
    
}
