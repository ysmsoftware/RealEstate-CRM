package com.ysminfosolution.realestate.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.ProjectDetailPdfView;
import com.ysminfosolution.realestate.error.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Amenity;
import com.ysminfosolution.realestate.model.BankProjectInfo;
import com.ysminfosolution.realestate.model.Disbursement;
import com.ysminfosolution.realestate.model.Document;
import com.ysminfosolution.realestate.model.Flat;
import com.ysminfosolution.realestate.model.Floor;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.model.ProjectDetailPdfPolicy;
import com.ysminfosolution.realestate.model.Wing;
import com.ysminfosolution.realestate.repository.AmenityRepository;
import com.ysminfosolution.realestate.repository.BankProjectInfoRepository;
import com.ysminfosolution.realestate.repository.DisbursementRepository;
import com.ysminfosolution.realestate.repository.DocumentRepository;
import com.ysminfosolution.realestate.repository.ProjectDetailPdfPolicyRepository;
import com.ysminfosolution.realestate.repository.WingRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.HtmlProjectPdfRenderer;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;
import com.ysminfosolution.realestate.service.ProjectDetailPdfService;
import com.ysminfosolution.realestate.service.ProjectPdfRenderer;
import com.ysminfosolution.realestate.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProjectDetailPdfServiceImpl implements ProjectDetailPdfService {

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final ProjectResolver projectResolver;
    private final ProjectAuthorizationService projectAuthorizationService;

    private final WingRepository wingRepository;
    private final AmenityRepository amenityRepository;
    private final BankProjectInfoRepository bankProjectInfoRepository;
    private final DisbursementRepository disbursementRepository;
    private final DocumentRepository documentRepository;
    private final ProjectDetailPdfPolicyRepository projectDetailPdfPolicyRepository;

    private final HtmlProjectPdfRenderer htmlProjectPdfRenderer;
    private final ProjectPdfRenderer projectPdfRenderer;
    private final S3StorageService s3StorageService;

    @Override
    @Transactional
    public byte[] generateProjectDetailPdf(UUID projectId, UUID policyId, AppUserDetails user) {
        if (user == null) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        Project project = projectResolver.resolve(projectId);
        projectAuthorizationService.checkProjectAccess(user, project);

        ProjectDetailPdfPolicy policy = projectDetailPdfPolicyRepository
                .findByProjectDetailPdfPolicyIdAndProject_ProjectId(policyId, projectId)
                .orElseThrow(() -> new NotFoundException("Project detail PDF policy not found for id: " + policyId));

        List<UUID> requestedDocumentIds = policy.isIncludeDocuments()
                ? normalizeDocumentIds(policy.getDocumentIds())
                : List.of();

        if (policy.isIncludeDocuments() && requestedDocumentIds.isEmpty()) {
            throw new IllegalArgumentException("Selected policy has includeDocuments=true but no documentIds configured");
        }

        log.info(
                "Starting project detail PDF generation. projectId={}, policyId={}, includeDocs={}, requestedDocuments={}, userId={}",
                projectId,
                policyId,
                policy.isIncludeDocuments(),
                requestedDocumentIds.size(),
                user.getUserId());

        Set<Wing> wings = requiresStructure(policy)
                ? wingRepository.fetchFullStructureByProjectId(projectId)
                : Set.of();

        Set<Amenity> amenities = policy.isIncludeAmenities()
                ? amenityRepository.findAllByProject_ProjectIdAndIsDeletedFalse(projectId)
                : Set.of();

        Set<BankProjectInfo> bankProjectInfos = policy.isIncludeBankProjectInfo()
                ? bankProjectInfoRepository.findAllByProject_ProjectIdAndIsDeletedFalse(projectId)
                : Set.of();

        Set<Disbursement> disbursements = policy.isIncludeDisbursements()
                ? disbursementRepository.findAllByProject_ProjectIdAndIsDeletedFalse(projectId)
                : Set.of();

        List<Document> selectedDocuments = policy.isIncludeDocuments()
                ? getValidSelectedDocuments(projectId, requestedDocumentIds)
                : List.of();

        S3StorageService.S3ObjectData letterheadObject = policy.isIncludeDocuments()
                ? loadLetterheadObject(project.getLetterheadUrl())
                : null;

        ProjectDetailPdfView view = buildView(
                project,
                policy,
                wings,
                amenities,
                bankProjectInfos,
                disbursements,
                selectedDocuments,
                policy.isIncludeDocuments() && letterheadObject != null);

        byte[] mainPdfBytes = htmlProjectPdfRenderer.renderPdf(view);

        if (!policy.isIncludeDocuments()) {
            return mainPdfBytes;
        }

        List<ProjectPdfRenderer.ExternalDocument> attachments = new ArrayList<>();
        addProjectLetterheadAttachment(attachments, project, letterheadObject);
        attachments.addAll(loadSelectedDocumentBytes(selectedDocuments));

        if (attachments.isEmpty()) {
            log.info(
                    "Document embedding requested but no mergeable project files were found. projectId={}, policyId={}",
                    projectId,
                    policyId);
            return mainPdfBytes;
        }

        ProjectPdfRenderer.MergedPdfResult mergeResult = projectPdfRenderer
                .mergeWithExternalDocuments(mainPdfBytes, attachments);

        log.info(
                "Documents merged into project detail PDF. projectId={}, policyId={}, mergedPdf={}, mergedImages={}, skipped={}",
                projectId,
                policyId,
                mergeResult.mergedPdfCount(),
                mergeResult.mergedImageCount(),
                mergeResult.skippedCount());

        return mergeResult.pdfBytes();
    }

    private boolean requiresStructure(ProjectDetailPdfPolicy policy) {
        return policy.isIncludeWings() || policy.isIncludeFloors() || policy.isIncludeFlats();
    }

    private List<UUID> normalizeDocumentIds(List<UUID> documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return List.of();
        }

        return documentIds.stream()
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<Document> getValidSelectedDocuments(UUID projectId, List<UUID> requestedDocumentIds) {
        if (requestedDocumentIds.isEmpty()) {
            return List.of();
        }

        List<Document> fetched = documentRepository
                .findAllByProject_ProjectIdAndDocumentIdInAndIsDeletedFalse(projectId, requestedDocumentIds);

        if (fetched.isEmpty()) {
            return List.of();
        }

        Map<UUID, Document> documentById = new HashMap<>();
        for (Document document : fetched) {
            documentById.put(document.getDocumentId(), document);
        }

        List<Document> ordered = new ArrayList<>();
        for (UUID requestedDocumentId : requestedDocumentIds) {
            Document document = documentById.get(requestedDocumentId);
            if (document != null) {
                ordered.add(document);
            }
        }

        return ordered;
    }

    private S3StorageService.S3ObjectData loadLetterheadObject(String letterheadKey) {
        if (letterheadKey == null || letterheadKey.isBlank()) {
            return null;
        }

        try {
            return s3StorageService.downloadFile(letterheadKey);
        } catch (Exception ex) {
            log.warn("Letterhead could not be loaded from S3 key {}: {}", letterheadKey, ex.getMessage());
            return null;
        }
    }

    private void addProjectLetterheadAttachment(
            List<ProjectPdfRenderer.ExternalDocument> attachments,
            Project project,
            S3StorageService.S3ObjectData letterheadObject) {
        if (project == null || letterheadObject == null || project.getLetterheadUrl() == null
                || project.getLetterheadUrl().isBlank()) {
            return;
        }

        attachments.add(new ProjectPdfRenderer.ExternalDocument(
                "Project LetterHead",
                "LetterHead",
                project.getLetterheadUrl(),
                letterheadObject.contentType(),
                letterheadObject.bytes()));
    }

    private List<ProjectPdfRenderer.ExternalDocument> loadSelectedDocumentBytes(List<Document> selectedDocuments) {
        List<ProjectPdfRenderer.ExternalDocument> externalDocuments = new ArrayList<>();

        for (Document selectedDocument : selectedDocuments) {
            if (selectedDocument.getDocumentURL() == null || selectedDocument.getDocumentURL().isBlank()) {
                continue;
            }

            try {
                S3StorageService.S3ObjectData objectData = s3StorageService.downloadFile(selectedDocument.getDocumentURL());

                externalDocuments.add(new ProjectPdfRenderer.ExternalDocument(
                        defaultString(selectedDocument.getDocumentTitle()),
                        toDisplayEnum(selectedDocument.getDocumentType()),
                        selectedDocument.getDocumentURL(),
                        objectData.contentType(),
                        objectData.bytes()));

            } catch (Exception ex) {
                log.warn(
                        "Skipping selected document {} because it could not be downloaded: {}",
                        selectedDocument.getDocumentId(),
                        ex.getMessage());
            }
        }

        return externalDocuments;
    }

    private ProjectDetailPdfView buildView(
            Project project,
            ProjectDetailPdfPolicy policy,
            Set<Wing> wings,
            Set<Amenity> amenities,
            Set<BankProjectInfo> bankProjectInfos,
            Set<Disbursement> disbursements,
            List<Document> selectedDocuments,
            boolean includeProjectLetterheadDocument) {

        return new ProjectDetailPdfView(
                defaultString(project.getOrganization() == null ? null : project.getOrganization().getOrgName()),
                project.getOrganization() == null ? null : project.getOrganization().getLogoUrl(),
                TIMESTAMP_FORMATTER.format(LocalDateTime.now()),
                policy.isIncludeProjectOverview(),
                requiresStructure(policy),
                policy.isIncludeBankProjectInfo(),
                policy.isIncludeAmenities(),
                policy.isIncludeDisbursements(),
                policy.isIncludeDocuments(),
                buildOverview(project),
                toWingViews(wings),
                toBankProjectInfoViews(bankProjectInfos),
                toAmenityViews(amenities),
                toDisbursementViews(disbursements),
                toDocumentViews(selectedDocuments, includeProjectLetterheadDocument));
    }

    private ProjectDetailPdfView.ProjectOverviewView buildOverview(Project project) {
        return new ProjectDetailPdfView.ProjectOverviewView(
                defaultString(project.getProjectName()),
                defaultString(project.getProjectAddress()),
                defaultString(project.getMahareraNo()),
                toDisplayEnum(project.getStatus()),
                project.getProgress() == null ? "-" : project.getProgress() + "%",
                formatDate(project.getStartDate()),
                formatDate(project.getCompletionDate()));
    }

    private List<ProjectDetailPdfView.WingView> toWingViews(Set<Wing> wings) {
        return wings.stream()
                .filter(wing -> !wing.isDeleted())
                .sorted(Comparator.comparing(Wing::getWingName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(this::toWingView)
                .toList();
    }

    private ProjectDetailPdfView.WingView toWingView(Wing wing) {
        List<Floor> sortedFloors = wing.getFloors().stream()
                .filter(floor -> !floor.isDeleted())
                .sorted(Comparator
                        .comparing(Floor::getFloorNo, Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Floor::getFloorName, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();

        List<ProjectDetailPdfView.FloorRowView> floorRows = new ArrayList<>();

        int totalVacant = 0;
        int totalBooked = 0;
        int totalRegistered = 0;
        int totalFlats = 0;

        for (Floor floor : sortedFloors) {
            int vacantCount = 0;
            int bookedCount = 0;
            int registeredCount = 0;
            int floorTotal = 0;

            for (Flat flat : floor.getFlats()) {
                if (flat == null || flat.isDeleted()) {
                    continue;
                }

                floorTotal++;
                if (flat.getStatus() == Flat.Status.Vacant) {
                    vacantCount++;
                } else if (flat.getStatus() == Flat.Status.Booked) {
                    bookedCount++;
                } else if (flat.getStatus() == Flat.Status.Registered) {
                    registeredCount++;
                }
            }

            floorRows.add(new ProjectDetailPdfView.FloorRowView(
                    floorDisplayName(floor),
                    toDisplayEnum(floor.getPropertyType()),
                    defaultString(floor.getProperty()),
                    formatArea(floor.getArea()),
                    vacantCount,
                    bookedCount,
                    registeredCount,
                    floorTotal));

            totalVacant += vacantCount;
            totalBooked += bookedCount;
            totalRegistered += registeredCount;
            totalFlats += floorTotal;
        }

        return new ProjectDetailPdfView.WingView(
                defaultString(wing.getWingName()),
                floorRows,
                totalVacant,
                totalBooked,
                totalRegistered,
                totalFlats);
    }

    private String floorDisplayName(Floor floor) {
        String floorNo = floor.getFloorNo() == null ? "-" : floor.getFloorNo().toString();
        if (floor.getFloorName() == null || floor.getFloorName().isBlank()) {
            return "Floor " + floorNo;
        }
        return "Floor " + floorNo + " - " + floor.getFloorName();
    }

    private List<ProjectDetailPdfView.BankProjectInfoView> toBankProjectInfoViews(Set<BankProjectInfo> bankProjectInfos) {
        return bankProjectInfos.stream()
                .filter(bankProjectInfo -> !bankProjectInfo.isDeleted())
                .sorted(Comparator.comparing(
                        BankProjectInfo::getBankName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(bankProjectInfo -> new ProjectDetailPdfView.BankProjectInfoView(
                        defaultString(bankProjectInfo.getBankName()),
                        defaultString(bankProjectInfo.getBranchName()),
                        defaultString(bankProjectInfo.getContactPerson()),
                        defaultString(bankProjectInfo.getContactNumber())))
                .toList();
    }

    private List<String> toAmenityViews(Set<Amenity> amenities) {
        return amenities.stream()
                .filter(amenity -> !amenity.isDeleted())
                .map(amenity -> defaultString(amenity.getAmenityName()))
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    private List<ProjectDetailPdfView.DisbursementView> toDisbursementViews(Set<Disbursement> disbursements) {
        return disbursements.stream()
                .filter(disbursement -> !disbursement.isDeleted())
                .sorted(Comparator.comparing(
                        Disbursement::getDisbursementTitle,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .map(disbursement -> new ProjectDetailPdfView.DisbursementView(
                        defaultString(disbursement.getDisbursementTitle()),
                        formatPercentage(disbursement.getPercentage())))
                .toList();
    }

    private List<ProjectDetailPdfView.DocumentView> toDocumentViews(
            List<Document> selectedDocuments,
            boolean includeProjectLetterheadDocument) {
        List<ProjectDetailPdfView.DocumentView> documentViews = new ArrayList<>();

        if (includeProjectLetterheadDocument) {
            documentViews.add(new ProjectDetailPdfView.DocumentView("Project LetterHead", "LetterHead"));
        }

        documentViews.addAll(selectedDocuments.stream()
                .map(document -> new ProjectDetailPdfView.DocumentView(
                        defaultString(document.getDocumentTitle()),
                        toDisplayEnum(document.getDocumentType())))
                .toList());

        return documentViews;
    }

    private String formatPercentage(Float value) {
        if (value == null) {
            return "-";
        }
        return String.format(Locale.ROOT, "%.2f%%", value);
    }

    private String formatArea(Double value) {
        if (value == null) {
            return "-";
        }
        return String.format(Locale.ROOT, "%.2f", value);
    }

    private String formatDate(LocalDate date) {
        if (date == null) {
            return "-";
        }
        return DATE_FORMATTER.format(date);
    }

    private String toDisplayEnum(Enum<?> enumValue) {
        if (enumValue == null) {
            return "-";
        }

        String normalized = enumValue.name().replace('_', ' ').toLowerCase(Locale.ROOT);
        String[] parts = normalized.split(" ");
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isBlank()) {
                continue;
            }
            if (i > 0) {
                builder.append(' ');
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
        }

        return builder.isEmpty() ? "-" : builder.toString();
    }

    private String defaultString(String value) {
        if (value == null || value.isBlank()) {
            return "-";
        }
        return value;
    }
}
