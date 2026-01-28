package com.ysminfosolution.realestate.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record FollowUpNodeResponseDTO(
    UUID followUpNodeId,
    LocalDateTime followUpDateTime,
    String body,
    String tag,
    String agentName
) {

}
