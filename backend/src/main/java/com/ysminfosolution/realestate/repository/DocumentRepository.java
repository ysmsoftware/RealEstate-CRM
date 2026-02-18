package com.ysminfosolution.realestate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Set<Document> findAllByProject_ProjectId(UUID projectId);

    List<Document> findAllByProject_ProjectIdAndDocumentIdInAndIsDeletedFalse(UUID projectId, Collection<UUID> documentIds);

    void deleteAllByProject_ProjectId(UUID projectId);
    
}
