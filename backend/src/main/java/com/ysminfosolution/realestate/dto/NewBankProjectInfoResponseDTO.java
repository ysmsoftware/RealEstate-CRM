package com.ysminfosolution.realestate.dto;

import java.util.UUID;

public record NewBankProjectInfoResponseDTO(
    UUID bankProjectInfoId,
    String bankName,
    String branchName,
    String contactPerson,
    String contactNumber,
    UUID projectId
) {

}
