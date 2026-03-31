package com.ysminfosolution.realestate.dto;

import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.model.Floor.PropertyType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record UpdateEnquiryDTO(


    String leadName,

    @Pattern(regexp = "^(\\+[1-9]\\d{1,3})?\\d{10}$", message = "Invalid mobile number format") 
    String leadMobileNumber,

    String leadLandlineNumber,

    @Email
    String leadEmail,
    String leadCity,
    String leadAddress,
    String leadOccupation,
    String leadCompany,
    String budget,
    Status status,
    String reference,
    String referenceName,
    String remark,
    PropertyType propertyType,
    String property,

    @Positive
    Double area
) {

}
