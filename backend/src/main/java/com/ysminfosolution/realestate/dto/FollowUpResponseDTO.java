package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record FollowUpResponseDTO(
    UUID followUpId,
    UUID enquiryId,
    LocalDate followUpNextDate,
    String description,
    Set<FollowUpNodeResponseDTO> followUpNodes,
    String clientName,
    String email,
    String mobileNumber
) {

}
