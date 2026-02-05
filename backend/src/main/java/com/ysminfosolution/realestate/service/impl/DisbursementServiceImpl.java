package com.ysminfosolution.realestate.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.maincreationformdtos.DisbursementCreationDTO;
import com.ysminfosolution.realestate.model.Disbursement;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.DisbursementRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.DisbursementService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DisbursementServiceImpl implements DisbursementService {

    private final DisbursementRepository disbursementRepository;

    private final ProjectAuthorizationService projectAuthorizationService;
    private final ProjectResolver projectResolver;

    @Override
    public Boolean createDisbursementsForProject(Project savedProject, List<DisbursementCreationDTO> disbursements) {

        log.info("\n");
        log.info("Method: createDisbursementsForProject");

        try {
            for (DisbursementCreationDTO disbursementDTO : disbursements) {
                Disbursement disbursement = new Disbursement();

                disbursement.setDisbursementTitle(disbursementDTO.disbursementTitle());
                disbursement.setDescription(disbursementDTO.description());
                disbursement.setPercentage(disbursementDTO.percentage());
                disbursement.setDeleted(false);
                disbursement.setProject(savedProject);

                disbursementRepository.save(disbursement);
            }
            log.info("DISBURSEMENTS successfully created for Project : " + savedProject.getProjectName());
            return true;
        } catch (Exception e) {
            log.info("Error occured while creating Disbursement for Project: " + savedProject.getProjectName()
                    + "\nRolling back...");
            return false;
        }

    }

    @Override
    public void hardDeleteAllByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: hardDeleteAllByProjectId");

        disbursementRepository.deleteAllByProject_ProjectId(projectId);
        log.info("Disbursements deleted successfully for projectId : " + projectId);
    }

    @Override
    public ResponseEntity<Set<Disbursement>> changeDisbursementsForProject(UUID projectId,
            List<DisbursementCreationDTO> disbursements) {

        log.info("\n");
        log.info("Method: changeDisbursementsForProject");

        deleteAllByProjectId(projectId);
        Project project = projectResolver.resolve(projectId);
        createDisbursementsForProject(project, disbursements);

        Set<Disbursement> savedDisbursements = disbursementRepository.findAllByProject_ProjectId(projectId).stream()
                .filter(dis -> !dis.isDeleted()).collect(Collectors.toSet());
        return ResponseEntity.ok(savedDisbursements);
    }

    private void deleteAllByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: deleteAllByProjectId");

        Set<Disbursement> disbursements = disbursementRepository.findAllByProject_ProjectId(projectId);
        for (Disbursement disbursement : disbursements) {
            disbursement.setDeleted(true);
        }
        disbursementRepository.saveAll(disbursements);
    }

    @Override
    public ResponseEntity<Set<Disbursement>> getAllDisbursementsByProjectId(UUID projectId,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllDisbursementsByProjectId");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(disbursementRepository.findAllByProject_ProjectId(projectId)
                .stream()
                .filter(dis -> !dis.isDeleted())
                .collect(Collectors.toSet()));
    }

    @Override
    public void deleteDisbursementsByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: deleteDisbursementsByProjectId");

        deleteAllByProjectId(projectId);

    }

}
