package com.ysminfosolution.realestate.repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.Enquiry;
import com.ysminfosolution.realestate.model.FollowUp;
import com.ysminfosolution.realestate.model.Project;

public interface FollowUpRepository extends JpaRepository<FollowUp, UUID> {

    Optional<FollowUp> findByEnquiry_EnquiryId(UUID enquiryId);

    @Query("""
                SELECT DISTINCT f FROM FollowUp f
                JOIN FETCH f.enquiry e
                JOIN FETCH e.project p
                WHERE p IN :projects AND f.isDeleted = false
            """)
    Set<FollowUp> findAllByProjectsWithFetch(@Param("projects") Set<Project> projects);

    Set<FollowUp> findByFollowUpNextDateAndIsDeletedFalse(LocalDate today);

    @Query("""
                SELECT DISTINCT f FROM FollowUp f
                JOIN FETCH f.enquiry e
                WHERE e IN :enquiries AND f.isDeleted = false
            """)
    Set<FollowUp> findAllByEnquiriesWithFetch(@Param("enquiries") Set<Enquiry> enquiries);

    @Query("""
                SELECT DISTINCT f FROM FollowUp f
                JOIN FETCH f.enquiry e
                JOIN FETCH e.project p
                WHERE p.projectId = :projectId 
                AND f.isDeleted = false
                AND e.isDeleted = false
            """)
    Set<FollowUp> findAllByProjectIdWithFetch(@Param("projectId") UUID projectId);

    @Query("""
                SELECT DISTINCT f FROM FollowUp f
                JOIN FETCH f.enquiry e
                WHERE e.enquiryId = :enquiryId 
                AND f.isDeleted = false
            """)
    Optional<FollowUp> findByEnquiryIdWithFetch(@Param("enquiryId") UUID enquiryId);

    @Query("""
                SELECT DISTINCT f FROM FollowUp f
                JOIN FETCH f.enquiry e
                JOIN FETCH e.project p
                WHERE f.followUpId = :followUpId 
                AND f.isDeleted = false
            """)
    Optional<FollowUp> findByIdWithFetch(@Param("followUpId") UUID followUpId);

    Optional<FollowUp> findByFollowUpIdAndIsDeletedFalse(UUID followUpId);

}
