package com.ysminfosolution.realestate.dto;

import java.time.Instant;
import java.util.UUID;

import com.ysminfosolution.realestate.model.Enquiry.Status;

public record EnquiryBasicInfoDTO(
    UUID enquiryId,
    Instant enquiryCreatedDateTime,
    String leadName,
    String leadMobileNumber,
    UUID projectId,
    String projectName,
    String budget,
    Status status
) {

}
