package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.OrganizationSubscription;

public interface OrganizationSubscriptionRepository extends JpaRepository<OrganizationSubscription, UUID> {

}
