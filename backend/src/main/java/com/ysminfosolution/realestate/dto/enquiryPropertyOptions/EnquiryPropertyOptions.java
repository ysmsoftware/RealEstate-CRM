package com.ysminfosolution.realestate.dto.enquiryPropertyOptions;

import java.util.Set;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record EnquiryPropertyOptions(
    @NotNull
    @NotEmpty
    Set<PropertyTypeOption> propertyTypes,

    @Positive
    Long propertiesAvailable
) {}
