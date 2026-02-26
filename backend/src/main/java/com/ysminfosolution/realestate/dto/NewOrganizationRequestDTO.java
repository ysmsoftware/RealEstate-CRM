package com.ysminfosolution.realestate.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record NewOrganizationRequestDTO(

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Organization name must not contain special characters")
    String orgName,

    @NotNull
    @Email
    String orgEmail,

    @NotBlank
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "User name contains invalid characters")
    String username,

    @NotBlank
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Password contains invalid characters")
    String password,

    @NotBlank
    @Email
    String email,

    @NotBlank
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Full name contains invalid characters")
    String fullName,

    @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Invalid mobile number format") 
    String mobileNo
) {

}
