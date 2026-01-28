package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.model.Project.Status;

public record ProjectDTO(
    UUID projectId,
    String projectName,
    LocalDate startDate,
    LocalDate completionDate,
    String mahareraNo,
    Status status,
    Short progress,
    String projectAddress,
    Set<WingDTO> wings
) {

}
