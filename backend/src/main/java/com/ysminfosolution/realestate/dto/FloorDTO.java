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
) 
// * Required for sorting
implements Comparable<FloorDTO> {

    @Override
    public int compareTo(FloorDTO arg0) {
        return this.floorNo.compareTo(arg0.floorNo);
    }

}
