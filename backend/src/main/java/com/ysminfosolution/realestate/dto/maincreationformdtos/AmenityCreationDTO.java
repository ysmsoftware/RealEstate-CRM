package com.ysminfosolution.realestate.dto.maincreationformdtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AmenityCreationDTO( 

    @NotBlank(message = "Amenity name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Amenity name must not contain special characters")
    String amenityName 
){}
