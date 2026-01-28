package com.ysminfosolution.realestate.dto;

import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.model.Floor.PropertyType;

public record FloorDTO(
    UUID floorId,
    Short floorNo,
    String floorName,
    PropertyType propertyType,
    String property,
    Double area,
    Integer quantity,
    Set<FlatDTO> flats
) {

}
