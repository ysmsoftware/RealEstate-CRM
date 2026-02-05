package com.ysminfosolution.realestate.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.maincreationformdtos.WingCreationDTO;
import com.ysminfosolution.realestate.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.Wing;
import com.ysminfosolution.realestate.repository.WingRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.service.FloorService;
import com.ysminfosolution.realestate.service.WingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WingServiceImpl implements WingService {

    // * Repository
    private final WingRepository wingRepository;

    // * Other Services used
    private final FloorService floorService;

    private final ProjectResolver projectResolver;

    @Override
    @Transactional
    public Boolean createWingsForProject(Project savedProject, List<WingCreationDTO> wings) {

        log.info("\n");
        log.info("Method: createWingsForProject");

        Boolean floorCreatedSuccessfully = false;
        try {
            for (WingCreationDTO wingDTO : wings) {
                Wing wing = new Wing();
                wing.setWingName(wingDTO.wingName());
                wing.setNoOfFloors(wingDTO.noOfFloors());
                wing.setNoOfProperties(wingDTO.noOfProperties());
                wing.setProject(savedProject);
                wing.setDeleted(false);
                Wing savedWing = wingRepository.save(wing);

                // * Creating all the floors of this wing
                floorCreatedSuccessfully = floorService.createFloorsForWing(savedWing, wingDTO.floors());
            }
            log.info("WINGS created successfully for Project : " + savedProject.getProjectName());
            return floorCreatedSuccessfully;
        } catch (Exception e) {
            log.info("Error occured while creating Wings for Project: " + savedProject.getProjectName()
                    + "\nRolling back...");
            return false;
        }

    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void hardDeleteWingsRecursiveByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: hardDeleteWingsRecursiveByProjectId");

        Set<Wing> wings = wingRepository.findAllByProject_ProjectId(projectId);
        for (Wing wing : wings) {
            floorService.hardDeleteFloorsRecursiveByWingId(wing.getWingId());
        }
        wingRepository.deleteAll(wings);
        log.info("Wings deleted successfully for projectId : " + projectId);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public ResponseEntity<Wing> updateWing(UUID wingId, WingCreationDTO wingCreationDTO) {

        log.info("\n");
        log.info("Method: updateWing");

        Wing wing = wingRepository.findById(wingId).orElse(null);
        if (wing == null) {
            throw new NotFoundException("Wing not found for id: " + wingId);
        }

        // * First we delete the existing wing and its childs
        deleteWingRecursiveByWingtId(wingId);

        // * Then we create the wing from scratch
        return createWingForProjectId(wing.getProject().getProjectId(), wingCreationDTO);

    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public Boolean deleteWingRecursiveByWingtId(UUID wingId) {

        log.info("\n");
        log.info("Method: deleteWingRecursiveByWingtId");

        Wing wing = wingRepository.findById(wingId).orElse(null);
        if (wing == null) {
            return false;
        }

        wing.setDeleted(true);
        wingRepository.save(wing);
        floorService.deleteFloorsRecursiveByWingId(wingId);
        return true;

    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void deleteWingsRecursiveByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: deleteWingsRecursiveByProjectId");

        Set<Wing> wings = wingRepository.findAllByProject_ProjectId(projectId);
        for (Wing wing : wings) {
            floorService.deleteFloorsRecursiveByWingId(wing.getWingId());
            wing.setDeleted(true);
        }
        wingRepository.saveAll(wings);
    }

    @Transactional
    @Override
    public ResponseEntity<Wing> createWingForProjectId(UUID projectId, WingCreationDTO wingDTO) {

        Project project = projectResolver.resolve(projectId);

        Wing wing = new Wing();
        wing.setWingName(wingDTO.wingName());
        wing.setNoOfFloors(wingDTO.noOfFloors());
        wing.setNoOfProperties(wingDTO.noOfProperties());
        wing.setProject(project);
        wing.setDeleted(false);

        Wing savedWing = wingRepository.save(wing);

        if (!floorService.createFloorsForWing(savedWing, wingDTO.floors())) {
            floorService.deleteFloorsRecursiveByWingId(savedWing.getWingId());
            wingRepository.delete(savedWing);
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.ok(savedWing);
    }

    @Override
    public Set<Wing> getAllWingsForProject(UUID projectId) {

        log.info("Method: getAllWingsForProject");

        Project project = projectResolver.resolve(projectId);
        if (project.isDeleted()) {
            return Set.of();
        }

        return wingRepository.findAllByProject_ProjectIdAndIsDeletedFalse(projectId);
    }

    @Override
    public Set<Wing> getFullStructureByProjectId(UUID projectId) {
        projectResolver.resolve(projectId);
        return wingRepository.fetchFullStructureByProjectId(projectId);
    }

}
