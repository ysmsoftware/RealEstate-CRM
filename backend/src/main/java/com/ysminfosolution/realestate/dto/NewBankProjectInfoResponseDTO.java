package com.ysminfosolution.realestate.dto;

import java.util.UUID;

import com.ysminfosolution.realestate.model.BankProjectInfo.AccountType;

public record NewBankProjectInfoResponseDTO(
    UUID bankProjectInfoId,
    String bankName,
    String branchName,
    String contactPerson,
    String contactNumber,
    String ifscCode,
    String accountNumber,
    AccountType accountType,
    UUID projectId
) {

}
