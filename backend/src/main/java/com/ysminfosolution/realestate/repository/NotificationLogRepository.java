package com.ysminfosolution.realestate.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ysminfosolution.realestate.model.NotificationLog;

public interface NotificationLogRepository extends JpaRepository<NotificationLog, UUID> {

}
