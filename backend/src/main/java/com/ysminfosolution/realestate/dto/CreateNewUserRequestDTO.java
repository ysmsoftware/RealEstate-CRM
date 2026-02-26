package com.ysminfosolution.realestate.dto;

import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.model.User.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


public record CreateNewUserRequestDTO(

    @NotBlank
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "username contains invalid characters")
    String username,

    @NotBlank
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "password contains invalid characters")
    String password,

    @NotBlank
    @Email
    String email,

    @NotNull
    Role userType,

    @NotBlank
    @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Full name contains invalid characters")
    String fullName,

    @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Invalid mobile number format") 
    String mobileNumber,

    Set<UUID> projectIds
) {

}
