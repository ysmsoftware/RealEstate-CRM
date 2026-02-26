package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.UUID;

import com.ysminfosolution.realestate.model.Project.Status;

public record ProjectBasicInfoDTO(
    UUID projectId,
    String projectName,
    String projectAddress,
    String pincode,
    Status status,
    Short progress,
    LocalDate startDate,
    LocalDate completionDate
) {

}
