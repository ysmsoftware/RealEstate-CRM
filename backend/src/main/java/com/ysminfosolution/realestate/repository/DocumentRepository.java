package com.ysminfosolution.realestate.repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Document;

public interface DocumentRepository extends JpaRepository<Document, UUID> {

    Set<Document> findAllByProject_Id(UUID projectId);

    List<Document> findAllByProject_IdAndIdIn(UUID projectId, Collection<UUID> documentIds);

    void deleteAllByProject_Id(UUID projectId);
    
}
