package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.LockedResource;

public interface LockedResourceRepository extends JpaRepository<LockedResource, UUID> {

}
