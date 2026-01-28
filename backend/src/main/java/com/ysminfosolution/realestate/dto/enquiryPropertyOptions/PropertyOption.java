package com.ysminfosolution.realestate.dto.enquiryPropertyOptions;

import java.util.Set;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PropertyOption(

    @NotBlank
    String property,

    @NotNull
    @NotEmpty
    @Valid
    Set<AreaOptions> areas,

    @Positive
    Long propertiesAvailable
) {

}
