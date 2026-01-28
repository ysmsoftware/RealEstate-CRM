package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Set<Document> findAllByProject_ProjectId(UUID projectId);

    void deleteAllByProject_ProjectId(UUID projectId);
    
}
