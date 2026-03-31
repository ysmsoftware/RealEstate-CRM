package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.ysminfosolution.realestate.dto.EnquiryResponseDTO;
import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.Project;

public interface EnquiryRepository extends JpaRepository<Enquiry, UUID> {

    Set<Enquiry> findAllByProject_ProjectIdAndIsDeletedFalse(UUID projectId);

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
                    e.leadName,
                    e.leadMobileNumber,
                    e.leadLandlineNumber,
                    e.leadEmail,
                    e.leadCity,
                    e.leadAddress,
                    e.leadOccupation,
                    e.leadCompany,
                    e.status,
                    e.remark
                )
                FROM Enquiry e
                JOIN e.project p
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
                    e.leadName,
                    e.leadMobileNumber,
                    e.leadLandlineNumber,
                    e.leadEmail,
                    e.leadCity,
                    e.leadAddress,
                    e.leadOccupation,
                    e.leadCompany,
                    e.status,
                    e.remark
                )
                FROM Enquiry e
                JOIN e.project p
                WHERE
                    e.isDeleted = false
                    AND p.isDeleted = false
                    AND p IN :projects
            """)
    Set<EnquiryResponseDTO> findAllEnquiriesForProjects(Set<Project> projects);

}
