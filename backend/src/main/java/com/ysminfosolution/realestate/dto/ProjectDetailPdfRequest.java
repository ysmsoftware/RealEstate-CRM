package com.ysminfosolution.realestate.dto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public record ProjectDetailPdfRequest(
        @NotBlank(message = "Policy name is required")
        String policyName,
        boolean includeProjectOverview,
        boolean includeWings,
        boolean includeFloors,
        boolean includeFlats,
        boolean includeBankProjectInfo,
        boolean includeAmenities,
        boolean includeDisbursements,
        boolean includeDocuments,
        List<UUID> documentIds) {

    public ProjectDetailPdfRequest(
            String policyName,
            boolean includeProjectOverview,
            boolean includeWings,
            boolean includeFloors,
            boolean includeFlats,
            boolean includeBankProjectInfo,
            boolean includeAmenities,
            boolean includeDisbursements,
            boolean includeDocuments,
            List<UUID> documentIds) {
        this.policyName = policyName;
        this.includeProjectOverview = includeProjectOverview;
        this.includeWings = includeWings;
        this.includeFloors = includeFloors;
        this.includeFlats = includeFlats;
        this.includeBankProjectInfo = includeBankProjectInfo;
        this.includeAmenities = includeAmenities;
        this.includeDisbursements = includeDisbursements;
        this.includeDocuments = includeDocuments;
        this.documentIds = documentIds != null ? documentIds : new ArrayList<>();
    }
}
