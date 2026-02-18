package com.ysminfosolution.realestate.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ProjectDetailPdfPolicyResponse(
        UUID projectDetailPdfPolicyId,
        String policyName,
        boolean includeProjectOverview,
        boolean includeWings,
        boolean includeFloors,
        boolean includeFlats,
        boolean includeBankProjectInfo,
        boolean includeAmenities,
        boolean includeDisbursements,
        boolean includeDocuments,
        List<UUID> documentIds,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
