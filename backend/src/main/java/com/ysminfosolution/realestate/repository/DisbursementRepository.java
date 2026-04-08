package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Disbursement;

public interface DisbursementRepository extends JpaRepository<Disbursement, UUID> {

    void deleteAllByProject_Id(UUID projectId);

    @NonNull Set<Disbursement> findAllByProject_Id(UUID projectId);
    
}
