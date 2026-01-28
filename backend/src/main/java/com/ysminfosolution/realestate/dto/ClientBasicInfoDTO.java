package com.ysminfosolution.realestate.dto;

import java.util.UUID;

public record ClientBasicInfoDTO(
    UUID clientId,
    String clientName,
    String mobileNumber,
    String email,
    String city,
    String occupation
) {

}
