package com.ysminfosolution.realestate.dto.enquiryPropertyOptions;

import java.util.Set;

import com.ysminfosolution.realestate.model.Floor.PropertyType;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PropertyTypeOption(
    @NotNull
    PropertyType propertyType,

    @NotNull
    @NotEmpty
    @Valid
    Set<PropertyOption> properties,

    @Positive
    Long propertiesAvailable
) {
    
}
