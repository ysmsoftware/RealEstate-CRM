package com.ysminfosolution.realestate.controller;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import com.ysminfosolution.realestate.dto.DocumentResponseDTO;
import com.ysminfosolution.realestate.dto.maincreationformdtos.DocumentCreationDTO;
import com.ysminfosolution.realestate.model.Document;
import com.ysminfosolution.realestate.security.AppUserDetails;
import com.ysminfosolution.realestate.service.DocumentService;
import com.ysminfosolution.realestate.service.S3PresignedUrlService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Validated
@RequestMapping("/documents")
@Slf4j
@RequiredArgsConstructor
@Transactional
@PreAuthorize("hasAnyRole('ADMIN', 'EMPLOYEE')")
public class DocumentController {

    
    // * Services
    private final S3PresignedUrlService presignedUrlService;
    private final DocumentService documentService;

    @SuppressWarnings("null")
    @GetMapping("/download/**")
    public ResponseEntity<String> getDocumentUrl(HttpServletRequest request) {

        String path = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        String pattern = (String) request.getAttribute(
                HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        AntPathMatcher matcher = new AntPathMatcher();
        String key = matcher.extractPathWithinPattern(pattern, path);

        // Explicit decode (Spring sometimes decodes automatically, sometimes not)
        key = URLDecoder.decode(key, StandardCharsets.UTF_8);

        log.info("FINAL S3 KEY = {}", key);

        String presignedUrl = presignedUrlService.generateDownloadUrl(key);

        return ResponseEntity.ok(presignedUrl);
    }

    @GetMapping("/project/{projectId}")
    public ResponseEntity<Set<DocumentResponseDTO>> GetAllDocumentsForProjectId(@PathVariable @NotNull UUID projectId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /documents/project/{projectId} | Method: GetAllDocumentsForProjectId");

        return documentService.getAllDocumentsForProjectId(projectId, appUserDetails);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Document> getDocumentById(@PathVariable @NonNull UUID documentId,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [GET] /documents/{documentId} | Method: getDocumentById");

        return documentService.getDocumentById(documentId, appUserDetails);
    }

    @PostMapping("/{projectId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addDocumentToProject(@PathVariable @NotNull UUID projectId,
            @RequestBody @NotNull @Valid DocumentCreationDTO documentCreationDTO,
            @AuthenticationPrincipal AppUserDetails appUserDetails) {

        log.info("\n");
        log.info("Path: [POST] /documents/{projectId} | Method: addDocumentToProject");

        if (!documentService.addDocumentToProject(projectId, documentCreationDTO, appUserDetails)) {
            log.info("Could Not Add document for projectId : " + projectId);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok("Document Uploaded and added to project successfully");
    }

    @DeleteMapping("/{documentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDocument(@PathVariable @NotNull UUID documentId) {

        log.info("\n");
        log.info("Path: [DELETE] /documents/{documentId} | Method: deleteDocument");

        return documentService.deleteById(documentId);
    }
}
