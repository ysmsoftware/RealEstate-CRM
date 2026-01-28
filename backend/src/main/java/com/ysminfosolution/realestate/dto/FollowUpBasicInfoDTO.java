package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record FollowUpBasicInfoDTO(
    UUID followUpId,
    String clientName,
    String mobileNumber,
    LocalDate followUpNextDate,
    String agentName,
    String description
) {

}
