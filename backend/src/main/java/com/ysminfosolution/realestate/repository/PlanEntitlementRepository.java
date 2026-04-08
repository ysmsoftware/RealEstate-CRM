package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.PlanEntitlement;

public interface PlanEntitlementRepository extends JpaRepository<PlanEntitlement, UUID> {

}
