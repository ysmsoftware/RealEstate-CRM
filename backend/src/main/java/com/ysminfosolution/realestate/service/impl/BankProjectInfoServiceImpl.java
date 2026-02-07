package com.ysminfosolution.realestate.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.NewBankProjectInfoResponseDTO;
import com.ysminfosolution.realestate.dto.maincreationformdtos.BankProjectInfoCreationDTO;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.BankProjectInfo;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.BankProjectInfoRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.BankProjectInfoService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class BankProjectInfoServiceImpl implements BankProjectInfoService {

    private final ProjectResolver projectResolver;
    private final ProjectAuthorizationService projectAuthorizationService;

    // * Repository
    private final BankProjectInfoRepository bankProjectInfoRepository;

    @Override
    public Boolean createBankProjectInfosForProject(Project savedProject,
            List<BankProjectInfoCreationDTO> projectApprovedBanksInfo) {


        log.info("\n");
        log.info("Method: createBankProjectInfosForProject");

        try {
            for (BankProjectInfoCreationDTO bankProjectInfoDTO : projectApprovedBanksInfo) {
                BankProjectInfo bankProjectInfo = new BankProjectInfo();

                bankProjectInfo.setBankName(bankProjectInfoDTO.bankName());
                bankProjectInfo.setBranchName(bankProjectInfoDTO.branchName());
                bankProjectInfo.setContactPerson(bankProjectInfoDTO.contactPerson());
                bankProjectInfo.setContactNumber(bankProjectInfoDTO.contactNumber());
                bankProjectInfo.setProject(savedProject);
                bankProjectInfo.setDeleted(false);

                bankProjectInfoRepository.save(bankProjectInfo);
            }
            log.info("BANK PROJECT INFOS created successfully for Project : " + savedProject.getProjectName());
            return true;
        } catch (Exception e) {
            log.info("Error occured while creating BankProjectInfo for Project: " + savedProject.getProjectName()
                    + "\nRolling back...");
            return false;
        }

    }

    @Override
    public void hardDeleteAllByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: hardDeleteAllByProjectId");

        bankProjectInfoRepository.deleteAllByProject_ProjectId(projectId);
        
        log.info("BankProjectInfo deleted successfully for projectId : " + projectId);
    }

    @Override
    public ResponseEntity<NewBankProjectInfoResponseDTO> createBankProjectInfo(UUID projectId,
            BankProjectInfo bankProjectInfo) {

        log.info("\n");
        log.info("Method: createBankProjectInfo");
        
        Project project = projectResolver.resolve(projectId);
        if (project == null) {
            throw new NotFoundException("Project not found");
        }

        bankProjectInfo.setProject(project);
        bankProjectInfo.setDeleted(false);
        BankProjectInfo savedBankProjectInfo = bankProjectInfoRepository.save(bankProjectInfo);

        NewBankProjectInfoResponseDTO bankProjectInfoResponseDTO = new NewBankProjectInfoResponseDTO(
            savedBankProjectInfo.getBankProjectId(),
            savedBankProjectInfo.getBankName(),
            savedBankProjectInfo.getBranchName(),
            savedBankProjectInfo.getContactPerson(),
            savedBankProjectInfo.getContactNumber(),
            savedBankProjectInfo.getProject().getProjectId()
        );

        return ResponseEntity.ok(bankProjectInfoResponseDTO);
    }

    @Override
    public ResponseEntity<NewBankProjectInfoResponseDTO> updateBankProjectInfo(UUID bankProjectInfoId,
            BankProjectInfo bankProjectInfo) {

        log.info("\n");
        log.info("Method: updateBankProjectInfo");
        
        BankProjectInfo bankProjectInfoDB = bankProjectInfoRepository.findByBankProjectIdAndIsDeletedFalse(bankProjectInfoId).orElseThrow(() -> new NotFoundException("Bank Project Info not found"));
        
        bankProjectInfoDB.setBankName(bankProjectInfo.getBankName());
        bankProjectInfoDB.setBranchName(bankProjectInfo.getBranchName());
        bankProjectInfoDB.setContactPerson(bankProjectInfo.getContactPerson());
        bankProjectInfoDB.setContactNumber(bankProjectInfo.getContactNumber());
        
        BankProjectInfo savedBankProjectInfo = bankProjectInfoRepository.save(bankProjectInfoDB);

        NewBankProjectInfoResponseDTO bankProjectInfoResponseDTO = new NewBankProjectInfoResponseDTO(
            savedBankProjectInfo.getBankProjectId(), 
            savedBankProjectInfo.getBankName(), 
            savedBankProjectInfo.getBranchName(),
            savedBankProjectInfo.getContactPerson(),
            savedBankProjectInfo.getContactNumber(),
            savedBankProjectInfo.getProject().getProjectId()
        );
        return ResponseEntity.ok(bankProjectInfoResponseDTO);
    }

    @Override
    public ResponseEntity<String> deleteBankProjectInfo(UUID bankProjectInfoId) {

        log.info("\n");
        log.info("Method: deleteBankProjectInfo");

        BankProjectInfo bankProjectInfo = bankProjectInfoRepository.findByBankProjectIdAndIsDeletedFalse(bankProjectInfoId).orElseThrow(() -> new NotFoundException("Bank Project Info not found"));

        bankProjectInfo.setDeleted(true);
        bankProjectInfoRepository.save(bankProjectInfo);
        return ResponseEntity.ok("BankProjectInfo Deleted Successfully");
    }

    @Override
    public ResponseEntity<Set<BankProjectInfo>> getAllBankProjectInfoForProjectId(UUID projectId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllBankProjectInfoForProjectId");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(bankProjectInfoRepository.findAllByProject_ProjectId(projectId)
            .stream().filter(bpi -> !bpi.isDeleted()).collect(Collectors.toSet()));
    }

    @Override
    public ResponseEntity<BankProjectInfo> getById(UUID bankProjectInfoId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getById");

        BankProjectInfo bankProjectInfo = bankProjectInfoRepository.findByBankProjectIdAndIsDeletedFalse(bankProjectInfoId).orElseThrow(() -> new NotFoundException("Bank Project Info not found"));

        Project project = projectResolver.resolve(bankProjectInfo.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(bankProjectInfo);
    }

    @Override
    public void deleteBankProjectInfosByProjectId(UUID projectId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: deleteBankProjectInfosByProjectId");


        Set<BankProjectInfo> bankProjectInfos = getAllBankProjectInfoForProjectId(projectId, appUserDetails).getBody();

        if (bankProjectInfos == null) {
            return;
        }

        for (BankProjectInfo bankProjectInfo : bankProjectInfos) {
            deleteBankProjectInfo(bankProjectInfo.getBankProjectId());
        }
    }

}
