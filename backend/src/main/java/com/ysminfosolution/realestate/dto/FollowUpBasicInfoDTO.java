package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record FollowUpBasicInfoDTO(
    UUID followUpId,
    String leadName,
    String leadMobileNumber,
    LocalDate followUpNextDate,
    String agentName,
    String description
) {

}
