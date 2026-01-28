package com.ysminfosolution.realestate.dto;

import java.util.UUID;

import com.ysminfosolution.realestate.model.Enquiry.Status;

public record EnquiryBasicInfoDTO(
    UUID enquiryId,
    String clientName,
    String projectName,
    String budget,
    Status status
) {

}
