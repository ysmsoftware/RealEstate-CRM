package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import com.ysminfosolution.realestate.model.Disbursement;

public interface DisbursementRepository extends JpaRepository<Disbursement, UUID> {

    void deleteAllByProject_ProjectId(UUID projectId);

    @NonNull Set<Disbursement> findAllByProject_ProjectId(UUID projectId);

    @NonNull Set<Disbursement> findAllByProject_ProjectIdAndIsDeletedFalse(UUID projectId);
    
}
