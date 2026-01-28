package com.ysminfosolution.realestate.dto;

import java.util.Set;
import java.util.UUID;

public record WingDTO(
    UUID wingId,
    String wingName,
    Integer noOfFloors,
    Integer noOfProperties,
    Set<FloorDTO> floors
) {

}
