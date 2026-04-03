package com.ysminfosolution.realestate.repository;

import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ysminfosolution.realestate.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
                SELECT DISTINCT b
                FROM Booking b
                JOIN FETCH b.client c
                JOIN FETCH b.flat f
                JOIN FETCH f.project p
                LEFT JOIN FETCH b.enquiry e
                WHERE c.id = :clientId
                  AND b.deleted = false
            """)
    Set<Booking> findAllByClientIdWithFetch(@Param("clientId") UUID clientId);
}
