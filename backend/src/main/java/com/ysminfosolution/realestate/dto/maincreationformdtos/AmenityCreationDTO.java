package com.ysminfosolution.realestate.dto.maincreationformdtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AmenityCreationDTO( 

    @NotBlank(message = "Amenity name is required")
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Amenity name contains invalid characters")
    String amenityName 
){}
