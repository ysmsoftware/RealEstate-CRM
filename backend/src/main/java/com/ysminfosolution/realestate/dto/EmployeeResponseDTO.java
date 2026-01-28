package com.ysminfosolution.realestate.dto;

import java.util.Set;
import java.util.UUID;

public record EmployeeResponseDTO(
    UUID userId,
    String employeeName,
    String email,
    String mobileNumber,
    Set<ProjectDTO> projects
) {

}
