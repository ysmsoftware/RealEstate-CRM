package com.ysminfosolution.realestate.dto;

import java.util.List;

public record DashboardResponseDTO(
    int totalProjects,
    long totalProperties,
    long propertiesBooked,
    int propertiesAvailable,
    int totalEnquiries,
    int cancelledEnquiries,
    List<DashboardProjectResponseDTO> projects
) {
    
}
