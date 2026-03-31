package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.dto.ClientBasicInfoDTO;
import com.ysminfosolution.realestate.model.ClientUserInfo;

public interface ClientUserInfoRepository extends JpaRepository<ClientUserInfo, UUID> {

    Optional<ClientUserInfo> findByClientIdAndIsDeletedFalse(UUID clientId);

    @Query("""
                SELECT DISTINCT new com.ysminfosolution.realestate.dto.ClientBasicInfoDTO(
                    c.clientId,
                    c.clientName,
                    c.mobileNumber,
                    c.email,
                    c.city,
                    c.occupation
                )
                FROM Booking b
                JOIN b.client c
                JOIN b.flat f
                JOIN f.project p
                WHERE p.organization.orgId = :orgId
                  AND b.isDeleted = false
                  AND p.isDeleted = false
                  AND c.isDeleted = false
            """)
    Set<ClientBasicInfoDTO> findClientBasicInfoByOrganization(UUID orgId);


    
    @Query("""
                SELECT DISTINCT new com.ysminfosolution.realestate.dto.ClientBasicInfoDTO(
                    c.clientId,
                    c.clientName,
                    c.mobileNumber,
                    c.email,
                    c.city,
                    c.occupation
                )
                FROM Booking b
                JOIN b.client c
                JOIN b.flat f
                JOIN f.project p
                WHERE p.projectId IN :projectIds
                  AND c.isDeleted = false
                  AND b.isDeleted = false
                  AND p.isDeleted = false
            """)
    Set<ClientBasicInfoDTO> findClientBasicInfoByProjectIds(Set<UUID> projectIds);

}
