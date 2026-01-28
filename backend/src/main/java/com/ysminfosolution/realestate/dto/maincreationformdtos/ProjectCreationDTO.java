package com.ysminfosolution.realestate.dto.maincreationformdtos;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import com.ysminfosolution.realestate.model.Project.Status;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;


public record ProjectCreationDTO (
    
    @NotBlank(message = "Project name is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Project name must not contain special characters")
    String projectName,

    @Pattern(regexp = "^[a-zA-Z0-9\\s/,]*$", message = "Project address must not contain special characters")
    String projectAddress,

    @NotNull(message = "Project start date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate,

    @NotNull(message = "Project completion date is required")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate completionDate,

    @NotBlank(message = "Maharera Number is required")
    @Pattern(regexp = "^[a-zA-Z0-9\\s]+$", message = "Maharera number must not contain special characters")
    String mahareraNo,

    @NotNull(message = "Project status is required")
    Status status,

    @NotNull(message = "Letter head file is required")
    MultipartFile letterHeadFile,

    @Valid
    @NotEmpty(message = "Atleast one wing is required")
    Set<WingCreationDTO> wings,

    @Valid
    @NotEmpty(message = "Atleast one project approved bank detail is required")
    Set<BankProjectInfoCreationDTO> projectApprovedBanksInfo,

    @Valid
    Set<AmenityCreationDTO> amenities,
    
    @Valid
    Set<DocumentCreationDTO> documents,

    @Valid
    @NotEmpty(message = "Atleast one wing is required")
    Set<DisbursementCreationDTO> disbursements
) {
    
    public ProjectCreationDTO(
        String projectName,
        String projectAddress,
        LocalDate startDate,
        LocalDate completionDate,
        String mahareraNo,
        Status status,
        MultipartFile letterHeadFile,
        Set<WingCreationDTO> wings,
        Set<BankProjectInfoCreationDTO> projectApprovedBanksInfo,
        Set<AmenityCreationDTO> amenities,
        Set<DocumentCreationDTO> documents,
        Set<DisbursementCreationDTO> disbursements
    ) {
        this.projectName = projectName;
        this.projectAddress = projectAddress;
        this.startDate = startDate;
        this.completionDate = completionDate;
        this.mahareraNo = mahareraNo;
        this.status = status;
        this.letterHeadFile = letterHeadFile;
        this.wings = wings != null ? wings : new HashSet<>();
        this.projectApprovedBanksInfo = projectApprovedBanksInfo != null ? projectApprovedBanksInfo : new HashSet<>();
        this.amenities = amenities != null ? amenities : new HashSet<>();
        this.documents = documents != null ? documents : new HashSet<>();
        this.disbursements = disbursements != null ? disbursements : new HashSet<>();
    }
}
