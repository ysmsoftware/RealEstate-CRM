package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.FollowUpNode;

public interface FollowUpNodeRepository extends JpaRepository<FollowUpNode, UUID> {

    @Query("""
            SELECT fn FROM FollowUpNode fn
            JOIN FETCH fn.user
            WHERE fn.followUp.id = :followUpId
            AND fn.deleted = false
            ORDER BY fn.followUpDateTime
            """)
    Set<FollowUpNode> findAllByFollowUp_Id(@Param("followUpId") UUID followUpId);

    @Query("""
            SELECT fn FROM FollowUpNode fn
            JOIN FETCH fn.user
            WHERE fn.followUp.id IN :followUpIds
            AND fn.deleted = false
            ORDER BY fn.followUp.id, fn.followUpDateTime
            """)
    Set<FollowUpNode> findAllByFollowUpIdsWithUser(@Param("followUpIds") Set<UUID> followUpIds);

    Optional<FollowUpNode> findFirstByFollowUp_IdOrderByFollowUpDateTimeDesc(UUID followUpId);

    @Query("""
            SELECT fn FROM FollowUpNode fn
            JOIN FETCH fn.user
            WHERE fn.followUp.id = :followUpId
            AND fn.id = :nodeId
            AND fn.deleted = false
            """)
    Optional<FollowUpNode> findByFollowUpIdAndNodeId(@Param("followUpId") UUID followUpId,
            @Param("nodeId") UUID nodeId);
} 