package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.dto.EnquiryResponseDTO;
import com.ysminfosolution.realestate.model.ClientUserInfo;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Project;

public interface EnquiryRepository extends JpaRepository<Enquiry, UUID> {

    Set<Enquiry> findAllByProject_ProjectIdAndIsDeletedFalse(UUID projectId);

    Set<Enquiry> findAllByClientAndProject_Organization_OrgIdAndIsDeletedFalse(ClientUserInfo clientUserInfo,
            UUID orgId);

    Set<Enquiry> findAllByClient_ClientIdAndIsDeletedFalse(UUID clientId);

    @Query("""
                SELECT e FROM Enquiry e
                WHERE e.client = :client
                AND e.project IN :projects
                AND e.isDeleted = false
            """)
    Set<Enquiry> findAllByClientAndProjectsAndIsDeletedFalse(ClientUserInfo client, Set<Project> projects);

    @Query("""
                SELECT new com.ysminfosolution.realestate.dto.EnquiryResponseDTO(
                    e.enquiryId,
                    p.projectId,
                    p.projectName,
                    e.propertyType,
                    e.property,
                    e.area,
                    e.budget,
                    e.reference,
                    e.referenceName,
                    c.clientId,
                    c.clientName,
                    c.mobileNumber,
                    c.landlineNumber,
                    c.email,
                    c.city,
                    c.address,
                    c.occupation,
                    c.company,
                    e.status,
                    e.remark
                )
                FROM Enquiry e
                JOIN e.project p
                JOIN e.client c
                WHERE
                    e.isDeleted = false
                    AND p.isDeleted = false
                    AND p.organization.orgId = :orgId
            """)
    Set<EnquiryResponseDTO> findAllEnquiriesForOrg(UUID orgId);

    @Query("""
                SELECT new com.ysminfosolution.realestate.dto.EnquiryResponseDTO(
                    e.enquiryId,
                    p.projectId,
                    p.projectName,
                    e.propertyType,
                    e.property,
                    e.area,
                    e.budget,
                    e.reference,
                    e.referenceName,
                    c.clientId,
                    c.clientName,
                    c.mobileNumber,
                    c.landlineNumber,
                    c.email,
                    c.city,
                    c.address,
                    c.occupation,
                    c.company,
                    e.status,
                    e.remark
                )
                FROM Enquiry e
                JOIN e.project p
                JOIN e.client c
                WHERE
                    e.isDeleted = false
                    AND p.isDeleted = false
                    AND p IN :projects
            """)
    Set<EnquiryResponseDTO> findAllEnquiriesForProjects(Set<Project> projects);

}
