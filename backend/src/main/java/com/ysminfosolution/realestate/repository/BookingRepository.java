package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
    
}
