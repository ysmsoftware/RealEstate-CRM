package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.dto.ClientBasicInfoDTO;
import com.ysminfosolution.realestate.model.Client;

public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findById(UUID clientId);

    @Query("""
                SELECT DISTINCT new com.ysminfosolution.realestate.dto.ClientBasicInfoDTO(
                    c.id,
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
                WHERE p.organization.id = :orgId
                  AND b.deleted = false
                  AND p.deleted = false
                  AND c.deleted = false
            """)
    Set<ClientBasicInfoDTO> findClientBasicInfoByOrganization(UUID orgId);


    
    @Query("""
                SELECT DISTINCT new com.ysminfosolution.realestate.dto.ClientBasicInfoDTO(
                    c.id,
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
                WHERE p.id IN :projectIds
                  AND c.deleted = false
                  AND b.deleted = false
                  AND p.deleted = false
            """)
    Set<ClientBasicInfoDTO> findClientBasicInfoByProjectIds(Set<UUID> projectIds);

}
