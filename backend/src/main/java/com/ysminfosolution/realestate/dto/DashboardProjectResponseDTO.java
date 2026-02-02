package com.ysminfosolution.realestate.dto;

import java.util.UUID;

public record DashboardProjectResponseDTO(
    UUID id,
    String name,
    long totalProperties,
    long propertiesBooked,
    int propertiesAvailable,
    int totalEnquiries,
    int cancelledEnquiries
) {

}
