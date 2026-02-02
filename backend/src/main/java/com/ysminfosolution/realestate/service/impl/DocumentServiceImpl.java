package com.ysminfosolution.realestate.service.impl;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ysminfosolution.realestate.dto.maincreationformdtos.DocumentCreationDTO;
import com.ysminfosolution.realestate.exception.NotFoundException;
import com.ysminfosolution.realestate.model.Document;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.repository.DocumentRepository;
import com.ysminfosolution.realestate.resolver.ProjectResolver;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.DocumentService;
import com.ysminfosolution.realestate.service.ProjectAuthorizationService;
import com.ysminfosolution.realestate.service.S3StorageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final ProjectAuthorizationService projectAuthorizationService;

    private final S3StorageService s3StorageService;

    // * Repository
    private final DocumentRepository documentRepository;

    private final ProjectResolver projectResolver;


    @Override
    public Boolean createDocumentsForProject(Project savedProject,
            Set<DocumentCreationDTO> documents,
            AppUserDetails appUserDetails) {

        log.info("\nMethod: createDocumentsForProject");

        try {
            for (DocumentCreationDTO documentDTO : documents) {

                Document document = new Document();
                document.setDocumentTitle(documentDTO.documentTitle());
                document.setDocumentType(documentDTO.documentType());
                document.setProject(savedProject);
                document.setDeleted(false);

                // NEW: Upload to S3 and store key
                String s3Key = saveDocumentToS3(savedProject, documentDTO, appUserDetails);
                document.setDocumentURL(s3Key);

                if (s3Key == null) {
                    log.error("Could not upload document to S3");
                    return false;
                }

                documentRepository.save(document);
            }

            log.info("DOCUMENTS uploaded successfully for Project : {}", savedProject.getProjectName());
            return true;

        } catch (Exception e) {

            log.error("Error occurred while creating documents for Project {}. Error: {} Rolling back...",
                    savedProject.getProjectName(), e.getMessage());

            return false;
        }
    }

    private String saveDocumentToS3(Project project,
            DocumentCreationDTO dto,
            AppUserDetails user) {

        String key = user.getOrgId() + "/" +
                project.getProjectName() + "/Documents/" +
                dto.document().getOriginalFilename();

        return s3StorageService.uploadFile(key, dto.document());
    }

    @Override
    public void hardDeleteAllByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: hardDeleteAllByProjectId");

        documentRepository.deleteAllByProject_ProjectId(projectId);
        log.info("Documents deleted successfully for projectId : " + projectId);
    }

    @Override
    public boolean addDocumentToProject(UUID projectId, DocumentCreationDTO documentDTO,
            AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: addDocumentToProject");

        Project project = projectResolver.resolve(projectId);
        if (project == null) {
            return false;
        }

        try {
            Document document = new Document();

            document.setDocumentTitle(documentDTO.documentTitle());
            document.setDocumentType(documentDTO.documentType());
            document.setProject(project);
            document.setDeleted(false);
            document.setDocumentURL(saveDocumentToS3(project, documentDTO, appUserDetails));
            if (document.getDocumentURL() == null) {
                log.error("Could not upload the Document to S3");
                return false;
            }

            documentRepository.save(document);

            log.info("DOCUMENT created successfully for Project : " + project.getProjectName());
            return true;
        } catch (Exception e) {
            log.info("Error occured while creating Document for Project: " + project.getProjectName());
            return false;
        }
    }

    @Override
    @SuppressWarnings("null")
    public ResponseEntity<String> deleteById(UUID documentId) {

        log.info("\n");
        log.info("Method: deleteById");

        Document document = documentRepository.findById(documentId).orElse(null);

        if (document == null) {
            throw new NotFoundException("Document not found with id: " + documentId);
        }

        document.setDeleted(true);
        documentRepository.save(document);
        return ResponseEntity.ok("Document Successfully Deleted");
    }

    @Override
    public ResponseEntity<Set<Document>> getAllDocumentsForProjectId(UUID projectId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getAllDocumentsForProjectId");

        Project project = projectResolver.resolve(projectId);

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(documentRepository.findAllByProject_ProjectId(projectId)
                .stream().filter(doc -> !doc.isDeleted()).collect(Collectors.toSet()));
    }

    @Override
    public ResponseEntity<Document> getDocumentById(@NonNull UUID documentId, AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Method: getDocumentById");

        Document document = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException("Document not found"));

        Project project = projectResolver.resolve(document.getProject().getProjectId());

        projectAuthorizationService.checkProjectAccess(appUserDetails, project);

        return ResponseEntity.ok(document);
    }

    @Override
    public void deleteDocumentsByProjectId(UUID projectId) {

        log.info("\n");
        log.info("Method: deleteDocumentsByProjectId");

        Set<Document> documents = getAllDocumentsForProjectId(projectId, null).getBody();

        if (documents == null) {
            return;
        }

        for (Document document : documents) {
            deleteById(document.getDocumentId());
        }
    }

}
