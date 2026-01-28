package com.ysminfosolution.realestate.controller;

import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.NewOrganizationRequestDTO;
import com.ysminfosolution.realestate.dto.NewOrganizationResponseDTO;
import com.ysminfosolution.realestate.service.OrganizationService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@Validated
@RequestMapping("")
@Slf4j
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping("/register-organization")
    public ResponseEntity<NewOrganizationResponseDTO> createNewOrganizationAndAdmin(@RequestBody @NotNull @Valid NewOrganizationRequestDTO newOrganizationRequestDTO) {

        log.info("\n");
        log.info("Path: [POST] /register-organization | Method: createNewOrganizationAndAdmin");

        return organizationService.createNewOrganizationAndAdmin(newOrganizationRequestDTO);
    }
    
}
