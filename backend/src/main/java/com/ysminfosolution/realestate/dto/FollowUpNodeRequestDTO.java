package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record FollowUpNodeRequestDTO(

    @NotNull
    @Future
    LocalDate followUpNextDate,

    @NotBlank
    String body,

    @NotBlank
    String tag
) {

}
