package com.ysminfosolution.realestate.dto;

import java.util.Set;
import java.util.UUID;

import com.ysminfosolution.realestate.model.User.Role;


public record UserResponseDTO(
    UUID userId,
    NewOrganizationResponseDTO organization,
    String username,
    String email,
    String fullName,
    String mobileNumber,
    Role role,
    boolean enabled,
    Set<ProjectLeastInfoDTO> allocatedProjects
) {

}
