package com.ysminfosolution.realestate.dto;

import java.util.UUID;

import com.ysminfosolution.realestate.model.Enquiry.Status;
import com.ysminfosolution.realestate.model.Floor.PropertyType;

public record EnquiryResponseDTO(
    UUID enquiryId,
    UUID projectId,
    String projectName,
    PropertyType propertyType,
    String property,
    Double area,
    String budget,
    String reference,
    String referenceName,
    String leadName,
    String leadMobileNumber,
    String leadLandlineNumber,
    String leadEmail,
    String leadCity,
    String leadAddress,
    String leadOccupation,
    String leadCompany,
    Status status,
    String remark
) {

}
