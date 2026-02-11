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
    String clientName,

    @NotBlank
    @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Mobile number number format is invalid") 
    String mobileNumber,

    String landlineNumber,
    
    @NotBlank
    @Email
    String email,

    @NotBlank
    String city,

    @NotBlank
    String address,

    @NotBlank
    String occupation,

    @NotBlank
    String company,

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