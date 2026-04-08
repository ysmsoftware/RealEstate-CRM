package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.UserPermission;

public interface UserPermissionRepository extends JpaRepository<UserPermission, UUID> {

}
