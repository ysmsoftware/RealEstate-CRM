package com.ysminfosolution.realestate.dto;

import java.util.UUID;

import com.ysminfosolution.realestate.model.Flat.Status;

public record FlatDTO(
    UUID propertyId,
    String propertyNumber,
    Status status,
    Double area
) {

}
