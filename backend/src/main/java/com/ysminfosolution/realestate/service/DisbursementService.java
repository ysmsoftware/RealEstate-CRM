package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.maincreationformdtos.DisbursementCreationDTO;
import com.ysminfosolution.realestate.model.Disbursement;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface DisbursementService {

    Boolean createDisbursementsForProject(Project savedProject, List<DisbursementCreationDTO> disbursements);

    void hardDeleteAllByProjectId(UUID projectId);

    ResponseEntity<Set<Disbursement>> changeDisbursementsForProject(UUID projectId, List<DisbursementCreationDTO> disbursements);

    ResponseEntity<Set<Disbursement>> getAllDisbursementsByProjectId(UUID projectId, AppUserDetails appUserDetails);

    void deleteDisbursementsByProjectId(UUID projectId);
    
}