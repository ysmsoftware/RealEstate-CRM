package com.ysminfosolution.realestate.service.impl;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.maincreationformdtos.FloorCreationDTO;
import com.ysminfosolution.realestate.model.Floor;
import com.ysminfosolution.realestate.model.Wing;
import com.ysminfosolution.realestate.repository.FloorRepository;
import com.ysminfosolution.realestate.service.FlatService;
import com.ysminfosolution.realestate.service.FloorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FloorServiceImpl implements FloorService {

    // * Repository
    private final FloorRepository floorRepository;

    // * Other Services
    private final FlatService flatService;

    @Override
    @Transactional
    public Boolean createFloorsForWing(Wing savedWing, List<FloorCreationDTO> floorsDTOs) {

        log.info("\n");
        log.info("Method: createFloorsForWing");

        Boolean floorcreatedSuccessfully = false;
        try {
            for (FloorCreationDTO floorDTO : floorsDTOs) {

                Floor floor = new Floor();
                floor.setFloorNo(floorDTO.floorNo());
                floor.setFloorName(floorDTO.floorName());
                floor.setPropertyType(floorDTO.propertyType());
                floor.setProperty(floorDTO.property());
                floor.setArea(floorDTO.area());
                floor.setQuantity(floorDTO.quantity());
                floor.setProject(savedWing.getProject());
                floor.setWing(savedWing);
                floor.setDeleted(false);

                Floor savedFloor = floorRepository.save(floor);

                floorcreatedSuccessfully = flatService.createFlatsForFloor(savedFloor);
            }
            log.info("FLOORS created successfully for Wing : " + savedWing.getWingName());
            return floorcreatedSuccessfully;

        } catch (Exception e) {
            log.info("Error occured while creating Floors for Project: " + savedWing.getProject().getProjectName()
                    + "\nRolling back...");
            return false;
        }

    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void hardDeleteFloorsRecursiveByWingId(UUID wingId) {

        log.info("\n");
        log.info("Method: hardDeleteFloorsRecursiveByWingId");

        Set<Floor> floors = floorRepository.findAllByWing_WingIdAndIsDeletedFalse(wingId);
        for (Floor floor : floors) {
            flatService.hardDeleteFlatsRecursiveByFloorId(floor.getFloorId());
        }
        floorRepository.deleteAll(floors);
        log.info("Floors deleted successfully for wingId : " + wingId);
    }

    @Override
    @Transactional
    @SuppressWarnings("null")
    public void deleteFloorsRecursiveByWingId(UUID wingId) {

        log.info("\n");
        log.info("Method: deleteFloorsRecursiveByWingId");

        Set<Floor> floors = floorRepository.findAllByWing_WingIdAndIsDeletedFalse(wingId);
        for (Floor floor : floors) {
            flatService.deleteFlatsRecursiveByFloorId(floor.getFloorId());
            floor.setDeleted(true);
        }

        floorRepository.saveAll(floors);
    }

    @Override
    public Set<Floor> getAllFloorsForWing(UUID wingId) {

        log.info("\n");
        log.info("Method: getAllFloorsForWing");

        return floorRepository.findAllByWing_WingIdAndIsDeletedFalse(wingId)
            .stream()
            .filter(f -> !f.isDeleted())
            .collect(Collectors.toSet());
    }

}