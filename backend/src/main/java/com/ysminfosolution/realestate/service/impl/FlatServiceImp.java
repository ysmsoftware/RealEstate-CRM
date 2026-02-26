package com.ysminfosolution.realestate.service.impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Floor;
import com.ysminfosolution.realestate.repository.FlatRepository;
import com.ysminfosolution.realestate.repository.FloorRepository;
import com.ysminfosolution.realestate.service.FlatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FlatServiceImp implements FlatService {

    // * Repository
    private final FlatRepository flatRepository;
    private final FloorRepository floorRepository;

    @Override
    public Boolean createFlatsForFloor(Floor savedFloor) {

        log.info("\n");
        log.info("Method: createFlatsForFloor");

        try {
            for (int flatNo = 0; flatNo < savedFloor.getQuantity(); flatNo++) {
                Flat flat = new Flat();

                flat.setPropertyNumber("F" + Short.toString(savedFloor.getFloorNo()) + "-" + (flatNo + 1));
                flat.setArea(savedFloor.getArea()); // ! OR (savedFloor.getArea() / savedFloor.getQuantity())
                flat.setStatus(Flat.Status.Vacant);
                flat.setDeleted(false);
                flat.setFloor(savedFloor);
                flat.setWing(savedFloor.getWing());
                flat.setProject(savedFloor.getProject());

                flatRepository.save(flat);
            }
            log.info("FLATS created successfully for Project : " + savedFloor.getProject().getProjectName());
            return true;
        } catch (Exception e) {
            log.info("Error occured while creating Properties for Project: " + savedFloor.getProject().getProjectName()
                    + "\nRolling back...");
            return false;
        }

    }

    @Override
    public void hardDeleteFlatsRecursiveByFloorId(UUID floorId) {

        log.info("\n");
        log.info("Method: hardDeleteFlatsRecursiveByFloorId");

        flatRepository.deleteAllByFloor_FloorId(floorId);
        log.info("Flats deleted successfully for floorId : " + floorId);
    }

    @Override
    public Flat getFlatById(UUID id) {

        log.info("\n");
        log.info("Method: getFlatById");

        Flat flat = flatRepository.findById(id).orElse(null);
        if (flat == null || flat.isDeleted()) {
            return null;
        }
        return flat;
    }

    @Override
    public void deleteFlatsRecursiveByFloorId(UUID floorId) {

        log.info("\n");
        log.info("Method: deleteFlatsRecursiveByFloorId");

        Set<Flat> flats = flatRepository.findAllByFloor_FloorId(floorId);
        for (Flat flat : flats) {
            flat.setDeleted(true);
        }
        flatRepository.saveAll(flats);
    }

    @Override
    public Set<Flat> getAllFlatsForFloor(UUID floorId) {

        log.info("\n");
        log.info("Method: getAllFlatsForFloor");
        
        Floor floor = floorRepository.findById(floorId).orElse(null);
        if (floor == null || floor.isDeleted()) {
            return null;
        }

        return flatRepository.findAllByFloor_FloorId(floorId)
            .stream()
            .filter(f -> !f.isDeleted())
            .collect(Collectors.toSet());

    }

}
