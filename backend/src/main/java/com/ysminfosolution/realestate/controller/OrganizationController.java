package com.ysminfosolution.realestate.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ysminfosolution.realestate.dto.NewOrganizationRequestDTO;
import com.ysminfosolution.realestate.dto.NewOrganizationResponseDTO;
import com.ysminfosolution.realestate.service.OrganizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@Validated
@RequestMapping("")
@Slf4j
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @PostMapping(value = "/register-organization", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<NewOrganizationResponseDTO> createNewOrganizationAndAdmin(
            @RequestPart("organization") String organizationJson,
            @RequestPart(value = "logo", required = false) MultipartFile logo) throws JsonMappingException, JsonProcessingException {

        log.info("\n");
        log.info("Path: [POST] /register-organization | Method: createNewOrganizationAndAdmin");

        ObjectMapper mapper = new ObjectMapper();
        NewOrganizationRequestDTO newOrganizationRequestDTO = mapper.readValue(organizationJson, NewOrganizationRequestDTO.class);

        return organizationService.createNewOrganizationAndAdmin(newOrganizationRequestDTO, logo);
    }

}
