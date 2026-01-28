package com.ysminfosolution.realestate.dto;

import java.time.LocalDate;
import java.util.UUID;

public record BookingDTO(
    UUID bookingId,
    UUID clientId,
    UUID enquiryId,
    UUID propertyId,
    String agreementAmount,
    String bookingAmount,
    LocalDate bookingDate,
    LocalDate chequeDate,
    String chequeNo,
    Float gstPercentage,
    String gstAmount,
    String infraAmount,
    String totalAmount,
    String rate,
    Boolean isRegistered,
    String regNo,
    LocalDate regDate,
    Boolean isCancelled,
    String remark
) {

}
