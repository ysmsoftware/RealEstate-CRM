package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.BankProjectInfo;

public interface BankProjectInfoRepository extends JpaRepository<BankProjectInfo, UUID> {

    Set<BankProjectInfo> findAllByProject_ProjectId(UUID projectId);

    Set<BankProjectInfo> findAllByProject_ProjectIdAndIsDeletedFalse(UUID projectId);

    void deleteAllByProject_ProjectId(UUID projectId);


    Optional<BankProjectInfo> findByBankProjectIdAndIsDeletedFalse(UUID bankProjectInfoId);
    
}
