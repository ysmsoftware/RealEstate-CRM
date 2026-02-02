package com.ysminfosolution.realestate.dto;

import java.util.Set;

public record DashboardResponseDTO(
    int totalProjects,
    long totalProperties,
    long propertiesBooked,
    int propertiesAvailable,
    int totalEnquiries,
    int cancelledEnquiries,
    Set<DashboardProjectResponseDTO> projects
) {
    
}
