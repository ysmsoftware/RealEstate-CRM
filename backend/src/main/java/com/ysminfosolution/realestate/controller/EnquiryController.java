package com.ysminfosolution.realestate.controller;

import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ysminfosolution.realestate.dto.EnquiryBasicInfoDTO;
import com.ysminfosolution.realestate.dto.EnquiryResponseDTO;
import com.ysminfosolution.realestate.dto.NewEnquiryDTO;
import com.ysminfosolution.realestate.dto.UpdateEnquiryDTO;
import com.ysminfosolution.realestate.dto.enquiryPropertyOptions.EnquiryPropertyOptions;
import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.EnquiryService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;



@RestController
@Validated
@RequestMapping("/enquiries")
@Slf4j
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class EnquiryController {

    private final EnquiryService enquiryService;

    @GetMapping("")
    public ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiries(@AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /enquiries | Method: getAllEnquiries");

        return enquiryService.getAllEnquiries(appUserDetails);
    }

    @GetMapping("/basicinfolist")
    public ResponseEntity<Set<EnquiryBasicInfoDTO>> getListOfEnquiryBasicInfo(@AuthenticationPrincipal AppUserDetails appUserDetails) {
     
        log.info("\n");
        log.info("Path: [GET] /enquiries/basicinfolist | Method: getListOfEnquiryBasicInfo");

        return enquiryService.getListOfEnquiryBasicInfo(appUserDetails);
        
    }
    

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Set<EnquiryResponseDTO>> getAllEnquiriesForProject(@PathVariable @NotNull UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /enquiries/project/{projectId} | Method: getAllEnquiriesForProject");

        return enquiryService.getAllEnquiriesForProject(projectId, appUserDetails);
    }
    
    @GetMapping("/{enquiryId}")
    public ResponseEntity<EnquiryResponseDTO> getById(@PathVariable @NonNull UUID enquiryId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /enquiries/{enquiryId} | Method: getById");

        return enquiryService.getById(enquiryId, appUserDetails);
    }
  
    @GetMapping("/basicinfolist/client/{clientId}")
    public ResponseEntity<Set<EnquiryBasicInfoDTO>> getListOfEnquiryBasicInfoForClient(@AuthenticationPrincipal AppUserDetails appUserDetails, @PathVariable @NotNull UUID clientId) {
     
        log.info("\n");
        log.info("Path: [GET] /enquiries/basicinfolist/client/{clientId} | Method: getListOfEnquiryBasicInfoForClient");

        return enquiryService.getListOfEnquiryBasicInfoForClient(appUserDetails, clientId);
        
    }
    
    @GetMapping("/propertyOptions/forProject/{projectId}")
    public ResponseEntity<EnquiryPropertyOptions> getAllPropertyOptionsForProject(@PathVariable @NotNull UUID projectId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /enquiries//propertyOptions/forProject/{projectId} | Method: getAllPropertyOptionsForProject");

        return enquiryService.getAllPropertyOptionsForProject(projectId, appUserDetails);
    }
    

    @PostMapping("")
    public ResponseEntity<EnquiryResponseDTO> createEnquiry(@RequestBody @NotNull @Valid NewEnquiryDTO newEnquiryDTO, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /enquiries | Method: createEnquiry");

        return enquiryService.createNewEnquiry(newEnquiryDTO, appUserDetails);
    }

    @PostMapping("/client/{clientId}")
    public ResponseEntity<EnquiryResponseDTO> createEnquiryWithClientId(@RequestBody @NonNull @Valid NewEnquiryDTO newEnquiryDTO, @PathVariable @NonNull UUID clientId, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /enquiries/client/{clientId} | Method: createEnquiryWithClientId");

        return enquiryService.createNewEnquiryForClient(newEnquiryDTO, clientId, appUserDetails);
    }

    @PutMapping("/{enquiryId}")
    public ResponseEntity<String> updateEnquiry(@PathVariable @NonNull UUID enquiryId, @RequestBody @NotNull @Valid UpdateEnquiryDTO updateEnquiryDTO, @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [PUT] /enquiries/{enquiryId} | Method: updateEnquiry");

        return enquiryService.updateEnquiry(enquiryId, updateEnquiryDTO, appUserDetails);
    }

    @DeleteMapping("/cancel/{enquiryId}")
    public ResponseEntity<String> cancelEnquiryWithRemark(@PathVariable @NonNull UUID enquiryId, @RequestParam @NotBlank String remark, @AuthenticationPrincipal AppUserDetails appUserDetails){

        log.info("\n");
        log.info("Path: [DELETE] /enquiries/cancel/{enquiryId} | Method: cancelEnquiryWithRemark");

        return enquiryService.cancelEnquiryWithRemark(enquiryId, remark, appUserDetails);
    }

    @PutMapping("/status/{enquiryId}")
    public ResponseEntity<String> changeEnquiryStatus(@PathVariable @NonNull UUID enquiryId, @RequestBody @NotNull Status status, @AuthenticationPrincipal AppUserDetails appUserDetails) {
        
        log.info("\n");
        log.info("Path: [DELETE] /enquiries/status/{enquiryId} | Method: changeEnquiryStatus");
        
        return enquiryService.changeEnquiryStatus(enquiryId, status, appUserDetails);
    }

}
