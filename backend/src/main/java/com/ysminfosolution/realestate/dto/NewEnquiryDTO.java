package com.ysminfosolution.realestate.dto;

import java.util.UUID;

import com.ysminfosolution.realestate.model.Floor.PropertyType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record NewEnquiryDTO(

    @NotBlank
    String leadName,

    @NotBlank
    @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Mobile number number format is invalid") 
    String leadMobileNumber,

    String leadLandlineNumber,
    
    @NotBlank
    @Email
    String leadEmail,

    @NotBlank
    String leadCity,

    @NotBlank
    String leadAddress,

    @NotBlank
    String leadOccupation,

    @NotBlank
    String leadCompany,

    @NotNull
    UUID projectId,

    @NotNull
    PropertyType propertyType,

    @NotBlank
    String property,

    @NotNull
    @Positive
    Double area,

    @NotBlank
    String budget,

    @NotBlank
    String reference,

    @NotBlank
    String referenceName

) {
}
