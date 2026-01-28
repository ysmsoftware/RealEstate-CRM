package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.FollowUpNode;

public interface FollowUpNodeRepository extends JpaRepository<FollowUpNode, UUID> {

    @Query("""
            SELECT fn FROM FollowUpNode fn
            JOIN FETCH fn.user
            WHERE fn.followUp.followUpId = :followUpId
            AND fn.isDeleted = false
            ORDER BY fn.followUpDateTime
            """)
    Set<FollowUpNode> findAllByFollowUp_FollowUpId(@Param("followUpId") UUID followUpId);

    @Query("""
            SELECT fn FROM FollowUpNode fn
            JOIN FETCH fn.user
            WHERE fn.followUp.followUpId IN :followUpIds
            AND fn.isDeleted = false
            ORDER BY fn.followUp.followUpId, fn.followUpDateTime
            """)
    Set<FollowUpNode> findAllByFollowUpIdsWithUser(@Param("followUpIds") Set<UUID> followUpIds);
} 