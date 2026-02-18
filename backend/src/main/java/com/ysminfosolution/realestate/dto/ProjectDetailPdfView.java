package com.ysminfosolution.realestate.dto;

import java.util.List;

public record ProjectDetailPdfView(
        String organizationName,
        String organizationLogoS3Key,
        String generatedAt,
        boolean includeProjectOverview,
        boolean includeWingStructure,
        boolean includeBankProjectInfo,
        boolean includeAmenities,
        boolean includeDisbursements,
        boolean includeDocuments,
        ProjectOverviewView projectOverview,
        List<WingView> wings,
        List<BankProjectInfoView> bankProjectInfos,
        List<String> amenities,
        List<DisbursementView> disbursements,
        List<DocumentView> documents) {

    public record ProjectOverviewView(
            String projectName,
            String projectAddress,
            String mahareraNo,
            String status,
            String progress,
            String startDate,
            String completionDate) {
    }

    public record WingView(
            String wingName,
            List<FloorRowView> floors,
            int totalVacant,
            int totalBooked,
            int totalRegistered,
            int totalFlats) {
    }

    public record FloorRowView(
            String floorName,
            String propertyType,
            String property,
            String area,
            int vacantCount,
            int bookedCount,
            int registeredCount,
            int totalCount) {
    }

    public record BankProjectInfoView(
            String bankName,
            String branchName,
            String contactPerson,
            String contactNumber) {
    }

    public record DisbursementView(
            String title,
            String percentage) {
    }

    public record DocumentView(
            String title,
            String type) {
    }
}
