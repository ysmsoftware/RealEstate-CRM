package com.ysminfosolution.realestate.service;

import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Floor;

public interface FlatService {

    Boolean createFlatsForFloor(Floor savedFloor);

    void hardDeleteFlatsRecursiveByFloorId(UUID floorId);

    Flat getFlatById(UUID uuid);

    void deleteFlatsRecursiveByFloorId(UUID floorId);

    Set<Flat> getAllFlatsForFloor(UUID floorId);
    
}
