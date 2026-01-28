package com.ysminfosolution.realestate.dto.maincreationformdtos;

import java.util.HashSet;
import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

public record WingCreationDTO(

    @NotBlank(message = "Wing name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Wing name must not contain special characters")
    String wingName,

    @NotNull(message = "Number of floors are required")
    @Positive(message = "Number of floors must be positive")
    Integer noOfFloors,

    @NotNull(message = "Number of properties are required")
    @Positive(message = "Number of properties must be positive")
    Integer noOfProperties,

    @Valid
    @NotEmpty(message = "Atleast one floor is required")
    Set<FloorCreationDTO> floors
) {
    public WingCreationDTO(
        String wingName,
        Integer noOfFloors,
        Integer noOfProperties,
        Set<FloorCreationDTO> floors
    ) {
        this.wingName = wingName;
        this.noOfFloors = noOfFloors;
        this.noOfProperties = noOfProperties;
        this.floors = floors != null ? floors : new HashSet<>();
    }
}
