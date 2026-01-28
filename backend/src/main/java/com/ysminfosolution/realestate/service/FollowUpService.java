package com.ysminfosolution.realestate.service;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.FollowUpBasicInfoDTO;
import com.ysminfosolution.realestate.dto.FollowUpNodeRequestDTO;
import com.ysminfosolution.realestate.dto.FollowUpResponseDTO;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface FollowUpService {

    boolean createFollowUpForEnquiry(Enquiry enquiry, AppUserDetails appUserDetails);

    ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUpsForProject(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<FollowUpResponseDTO> getFollowUpForEnquiry(UUID enquiryId, AppUserDetails appUserDetails);

    ResponseEntity<FollowUpResponseDTO> getById(UUID followUpId, AppUserDetails appUserDetails);

    ResponseEntity<String> addNodeToFollowUp(UUID followUpId, FollowUpNodeRequestDTO nodeRequestDTO, AppUserDetails appUserDetails);

    ResponseEntity<Set<FollowUpResponseDTO>> getAllFollowUps(AppUserDetails appUserDetails);

    ResponseEntity<Set<FollowUpBasicInfoDTO>> getAllRemainingFollowUpsWithinRange(AppUserDetails appUserDetails,
            LocalDate from, LocalDate to);

}
