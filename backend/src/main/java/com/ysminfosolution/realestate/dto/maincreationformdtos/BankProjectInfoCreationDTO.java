package com.ysminfosolution.realestate.dto.maincreationformdtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record BankProjectInfoCreationDTO(

        @NotBlank(message = "Bank name is required") 
        @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Bank name contains invalid characters") 
        String bankName,

        @NotBlank(message = "Branch name is required") 
        @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Branch name contains invalid characters") 
        String branchName,

        @NotBlank(message = "Contact person is required") 
        @Pattern(regexp = "^[\\p{L}0-9\\s\\-&,./()']+$", message = "Contact person contains invalid characters") 
        String contactPerson,

        @NotBlank(message = "Contact number is required") 
        @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Invalid contact number format") 
        String contactNumber) {

}
