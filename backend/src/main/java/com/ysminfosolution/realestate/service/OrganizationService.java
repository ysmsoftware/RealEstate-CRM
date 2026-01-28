package com.ysminfosolution.realestate.service;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.NewOrganizationRequestDTO;
import com.ysminfosolution.realestate.dto.NewOrganizationResponseDTO;

public interface OrganizationService {

    ResponseEntity<NewOrganizationResponseDTO> createNewOrganizationAndAdmin(NewOrganizationRequestDTO newOrganizationRequestDTO);

}
