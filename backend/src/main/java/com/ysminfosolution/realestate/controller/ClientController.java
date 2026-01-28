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

import com.ysminfosolution.realestate.dto.ClientBasicInfoDTO;
import com.ysminfosolution.realestate.dto.ClientDetailsDTO;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.ClientService;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
@Validated
@RequestMapping("/clients")
@RequiredArgsConstructor
@Slf4j
@Transactional
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class ClientController {

    private final ClientService clientService;

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientBasicInfoDTO> getClientBasicInfo(@AuthenticationPrincipal AppUserDetails appUserDetails,
            @PathVariable @NotNull UUID clientId) {

        log.info("\n");
        log.info("Path: [GET] /clients/{} | Method: getClientBasicInfo", clientId);

        return clientService.getClientBasicInfo(appUserDetails, clientId);
    }

    @GetMapping("/{clientId}/details")
    public ResponseEntity<ClientDetailsDTO> getClientDetails(@AuthenticationPrincipal AppUserDetails appUserDetails,
            @PathVariable @NotNull UUID clientId) {

        log.info("\n");
        log.info("Path: [GET] /clients/{}/details | Method: getClientDetails", clientId);

        return clientService.getClientDetails(appUserDetails, clientId);
    }

    @GetMapping("/basicinfolist")
    public ResponseEntity<Set<ClientBasicInfoDTO>> getListOfClientBasicInfo(@AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /clients/basicinfolist | Method: getListOfClientBasicInfo");

        return clientService.getListOfClientBasicInfo(appUserDetails);


    }
    
}
