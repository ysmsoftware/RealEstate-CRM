package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.Organization;

public interface OrganizationRepository extends JpaRepository<Organization, UUID> {


    boolean existsByOrgEmail(String email);

    boolean existsByOrgIdAndIsDeletedFalse(UUID orgId);

    Optional<Organization> findByOrgIdAndIsDeletedFalse(UUID orgId);

}
