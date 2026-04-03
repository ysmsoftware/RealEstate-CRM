package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, UUID> {

    boolean existsByUser_Id(UUID userId);

    Optional<Admin> findByUser_Id(UUID userId);

}
