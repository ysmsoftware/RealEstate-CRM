package com.ysminfosolution.realestate.model;

import java.time.Instant;

import com.ysminfosolution.realestate.model.enums.NotificationChannel;
import com.ysminfosolution.realestate.model.enums.NotificationStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "notification_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class NotificationLog extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Enumerated(EnumType.STRING)    
    private NotificationChannel channel;

    private String templateKey; // SUBSCRIPTION_EXPIRED, OTP_LOGIN, FOLLOWUP_REMINDER

    private String recipient; // email or phone number

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private Integer retryCount = 0;

    private String lastError; // Store last error message if sending fails

    private Instant lastSentAt;

}
