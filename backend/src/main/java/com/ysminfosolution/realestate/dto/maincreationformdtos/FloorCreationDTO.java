package com.ysminfosolution.realestate.dto.maincreationformdtos;

import com.ysminfosolution.realestate.model.Floor.PropertyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record FloorCreationDTO(

    @NotNull(message = "Floor number is required")
    @PositiveOrZero
    Short floorNo,

    @NotBlank(message = "Floor name is required")
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Floor name contains invalid characters")
    String floorName,

    @NotNull(message = "Property type is required")
    PropertyType propertyType,

    @NotBlank(message = "Property category is required")
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Property category contains invalid characters")
    String property,

    @NotNull(message = "Floor area is required")
    @Positive(message = "Floor area must be positive")
    Double area,

    @NotNull(message = "Property quantity in floor is required")
    @Positive(message = "Property quantity must be positive")
    Integer quantity
) {}
