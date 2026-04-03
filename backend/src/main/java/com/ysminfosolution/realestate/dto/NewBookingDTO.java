package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record NewBookingDTO(

    UUID enquiryId,

    @NotNull
    UUID propertyId,

    String clientName,

    @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Mobile number format is invalid")
    String mobileNumber,

    String landlineNumber,

    @Email
    String email,

    String city,

    String address,

    String occupation,

    String company,

    @NotNull
    @Past
    LocalDate dob,

    @NotNull
    @Pattern(regexp = "^[0-9]+$")
    String panNo,

    @Pattern(regexp = "^[0-9]+$")
    @NotNull    
    String aadharNo,

    @NotBlank
    String bookingAmount,

    @NotNull
    LocalDate bookingDate,

    @NotBlank
    String rate,

    @NotBlank
    String infraAmount,

    @NotBlank
    String agreementAmount,

    @NotNull
    @Positive
    Float gstPercentage,

    @NotBlank
    String gstAmount,

    @NotBlank
    String totalAmount,

    @NotBlank
    String chequeNo,

    @NotNull
    LocalDate chequeDate
) {

}
