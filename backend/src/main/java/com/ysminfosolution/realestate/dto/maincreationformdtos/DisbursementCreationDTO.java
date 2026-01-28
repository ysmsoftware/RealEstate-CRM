package com.ysminfosolution.realestate.dto.maincreationformdtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record DisbursementCreationDTO(

    @NotBlank(message = "Disbursement title is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Disbursement title must not contain special characters")
    String disbursementTitle,

    @NotBlank(message = "Description is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Description must not contain special characters")
    String description,

    @NotNull(message = "Disbursement percentage is required")
    @Positive(message = "Disbursement percentage must be positive")
    Float percentage
) {}
