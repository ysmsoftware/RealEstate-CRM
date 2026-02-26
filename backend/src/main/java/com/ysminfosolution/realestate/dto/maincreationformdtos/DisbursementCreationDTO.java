package com.ysminfosolution.realestate.dto.maincreationformdtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record DisbursementCreationDTO(

    @NotBlank(message = "Disbursement title is required")
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Disbursement title contains invalid characters")
    String disbursementTitle,

    @NotBlank(message = "Description is required")
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Description contains invalid characters")
    String description,

    @NotNull(message = "Disbursement percentage is required")
    @Positive(message = "Disbursement percentage must be positive")
    Float percentage
) {}
