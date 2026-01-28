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
    UUID clientId,
    String clientName,
    String mobileNumber,
    String landlineNumber,
    String email,
    String city,
    String address,
    String occupation,
    String company,
    Status status,
    String remark
) {

}
