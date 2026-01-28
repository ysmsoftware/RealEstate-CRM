package com.ysminfosolution.realestate.dto;

import java.util.UUID;

public record AdminResponseDTO(
    UUID adminId,
    UserResponseDTO user,
    String mobileNumber
) {

}
