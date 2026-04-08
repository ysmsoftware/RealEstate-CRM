package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.EnquiryBasicInfoDTO;
import com.ysminfosolution.realestate.dto.EnquiryResponseDTO;
import com.ysminfosolution.realestate.dto.NewEnquiryDTO;
import com.ysminfosolution.realestate.dto.UpdateEnquiryDTO;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.EnquiryPropertyOptions;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.security.AppUserDetails;

import jakarta.validation.constraints.NotNull;


public interface EnquiryService {

    ResponseEntity<EnquiryResponseDTO> createNewEnquiry(NewEnquiryDTO newEnquiryDTO, AppUserDetails appUserDetails);

    ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiries(AppUserDetails appUserDetails);

    ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiriesForProject(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<EnquiryResponseDTO> getById(@NotNull UUID enquiryId, AppUserDetails appUserDetails);

    ResponseEntity<String> cancelEnquiryWithRemark(@NotNull UUID enquiryId, String remark, AppUserDetails appUserDetails);

    ResponseEntity<Set<EnquiryBasicInfoDTO>> getListOfEnquiryBasicInfo(AppUserDetails appUserDetails);

    ResponseEntity<String> updateEnquiry(@NotNull UUID enquiryId, UpdateEnquiryDTO updateEnquiryDTO,
            AppUserDetails appUserDetails);

    ResponseEntity<EnquiryPropertyOptions> getAllPropertyOptionsForProject(UUID projectId,
            AppUserDetails appUserDetails);

    ResponseEntity<String> changeEnquiryStatus(@NotNull UUID enquiryId, Status status, AppUserDetails appUserDetails);
    
}
