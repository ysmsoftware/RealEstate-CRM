package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.dto.maincreationformdtos.FloorCreationDTO;
import com.ysminfosolution.realestate.model.Floor;
import com.ysminfosolution.realestate.model.Wing;

public interface FloorService {

    Boolean createFloorsForWing(Wing savedWing, List<FloorCreationDTO> floors);

    void hardDeleteFloorsRecursiveByWingId(UUID wingId);

    void deleteFloorsRecursiveByWingId(UUID wingId);

    Set<Floor> getAllFloorsForWing(UUID wingId);
    
}
