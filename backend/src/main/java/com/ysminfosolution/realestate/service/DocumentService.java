package com.ysminfosolution.realestate.service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;

import com.ysminfosolution.realestate.dto.maincreationformdtos.DocumentCreationDTO;
import com.ysminfosolution.realestate.model.Document;
import com.ysminfosolution.realestate.model.Project;
import com.ysminfosolution.realestate.security.AppUserDetails;

public interface DocumentService {

    Boolean createDocumentsForProject(Project savedProject, List<DocumentCreationDTO> documents, AppUserDetails appUserDetails);

    void hardDeleteAllByProjectId(UUID projectId);

    boolean addDocumentToProject(UUID projectId, DocumentCreationDTO documentCreationDTO, AppUserDetails appUserDetails);

    ResponseEntity<String> deleteById(UUID documentId);

    ResponseEntity<Set<Document>> getAllDocumentsForProjectId(UUID projectId, AppUserDetails appUserDetails);

    ResponseEntity<Document> getDocumentById(@NonNull UUID documentId, AppUserDetails appUserDetails);

    void deleteDocumentsByProjectId(UUID projectId);    
}