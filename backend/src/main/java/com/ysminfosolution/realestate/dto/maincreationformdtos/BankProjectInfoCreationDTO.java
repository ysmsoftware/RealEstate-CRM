package com.ysminfosolution.realestate.dto.maincreationformdtos;

import com.ysminfosolution.realestate.model.BankProjectInfo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
        String contactNumber,

        @NotBlank(message = "IFSC code is required")
        @Pattern(regexp = "^[A-Z]{4}0[A-Z0-9]{6}$", message = "Invalid IFSC code format")
        String ifscCode,

        @NotBlank(message = "Account number is required")
        @Pattern(regexp = "^[0-9]{9,18}$", message = "Account number must be between 9 and 18 digits")
        String accountNumber,

        @NotNull
        BankProjectInfo.AccountType accountType

) {

}
