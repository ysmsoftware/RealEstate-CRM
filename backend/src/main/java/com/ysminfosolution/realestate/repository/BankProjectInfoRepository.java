package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.BankProjectInfo;

public interface BankProjectInfoRepository extends JpaRepository<BankProjectInfo, UUID> {

    Set<BankProjectInfo> findAllByProject_Id(UUID projectId);

    Set<BankProjectInfo> findAllByProject_IdAndIsDeletedFalse(UUID projectId);

    void deleteAllByProject_Id(UUID projectId);


    Optional<BankProjectInfo> findByBankProjectIdAndIsDeletedFalse(UUID bankProjectInfoId);
    
}
