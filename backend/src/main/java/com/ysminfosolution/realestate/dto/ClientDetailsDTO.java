package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record ClientDetailsDTO(
    UUID clientId,
    String clientName,
    String email,
    String mobileNumber,
    LocalDate dob,
    String landlineNumber,
    String city,
    String address,
    String occupation,
    String company,
    String panNo,
    String aadharNo,
    Set<UUID> enquiries,
    Set<UUID> followUps
) {

}
