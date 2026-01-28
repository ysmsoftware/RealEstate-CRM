package com.ysminfosolution.realestate.dto;

import java.util.UUID;

public record NewOrganizationResponseDTO(
    UUID orgId,
    String orgName,
    String email
) {

}
