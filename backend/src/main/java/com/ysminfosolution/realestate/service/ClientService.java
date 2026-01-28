package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.ClientBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ClientDetailsDTO;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface ClientService {

    ClientUserInfo createClientForEnquiry(ClientUserInfo client);

    ResponseEntity<Set<ClientBasicInfoDTO>> getListOfClientBasicInfo(AppUserDetails appUserDetails);

    ResponseEntity<ClientBasicInfoDTO> getClientBasicInfo(AppUserDetails appUserDetails, UUID clientId);

    ResponseEntity<ClientDetailsDTO> getClientDetails(AppUserDetails appUserDetails, UUID clientId);
    
}
