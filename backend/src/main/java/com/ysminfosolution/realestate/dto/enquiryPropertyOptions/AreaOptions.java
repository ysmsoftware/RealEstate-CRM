package com.ysminfosolution.realestate.dto.enquiryPropertyOptions;

import jakarta.validation.constraints.Positive;

public record AreaOptions(

    @Positive
    Double area,

    @Positive
    Long propertiesAvailable
) {

}
