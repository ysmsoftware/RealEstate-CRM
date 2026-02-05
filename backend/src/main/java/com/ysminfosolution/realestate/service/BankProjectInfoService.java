package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;

import com.ysminfosolution.realestate.dto.NewBankProjectInfoResponseDTO;
import com.ysminfosolution.realestate.dto.maincreationformdtos.BankProjectInfoCreationDTO;
import com.ysminfosolution.realestate.model.BankProjectInfo;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface BankProjectInfoService {

    Boolean createBankProjectInfosForProject(Project savedProject, List<BankProjectInfoCreationDTO> projectApprovedBanksInfo);

    void hardDeleteAllByProjectId(UUID projectId);

    ResponseEntity<NewBankProjectInfoResponseDTO> createBankProjectInfo(UUID projectId,
            BankProjectInfo bankProjectInfo);

    ResponseEntity<NewBankProjectInfoResponseDTO> updateBankProjectInfo(UUID bankProjectInfoId,
            BankProjectInfo bankProjectInfo);

    ResponseEntity<String> deleteBankProjectInfo(UUID bankProjectInfoId);

    ResponseEntity<Set<BankProjectInfo>> getAllBankProjectInfoForProjectId(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<BankProjectInfo> getById(UUID bankProjectInfoId, AppUserDetails appUserDetails);

    void deleteBankProjectInfosByProjectId(UUID projectId, AppUserDetails appUserDetails);
    
}
