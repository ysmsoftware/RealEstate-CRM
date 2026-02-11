package com.ysminfosolution.realestate.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.AdminUserInfo;

public interface AdminUserInfoRepository extends JpaRepository<AdminUserInfo, UUID> {

    boolean existsByUser_UserId(UUID userId);

    Optional<AdminUserInfo> findByUser_UserId(UUID userId);

}
