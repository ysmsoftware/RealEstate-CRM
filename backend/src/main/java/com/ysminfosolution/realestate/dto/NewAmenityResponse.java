package com.ysminfosolution.realestate.dto;

import java.util.UUID;

public record NewAmenityResponse(
    UUID amenityId,
    String amenityName,
    UUID projectId
) {
    
}
