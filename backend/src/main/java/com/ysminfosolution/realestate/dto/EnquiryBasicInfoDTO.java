package com.ysminfosolution.realestate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ysminfosolution.realestate.model.Enquiry.Status;

public record EnquiryBasicInfoDTO(
    UUID enquiryId,
    LocalDateTime enquiryCreatedDateTime,
    String clientName,
    UUID projectId,
    String projectName,
    String budget,
    Status status
) {

}
