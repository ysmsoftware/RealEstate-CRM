package com.ysminfosolution.realestate.model;

import java.time.Instant;
import java.util.UUID;

import com.ysminfosolution.realestate.model.OrganizationSubscription.SubscriptionStatus;
import com.ysminfosolution.realestate.model.enums.SubscriptionHistory_Reason;

import jakarta.persistence.Column;
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

@Table(name = "subscription_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubscriptionHistory extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "from_plan_id", nullable = true)
    private SubscriptionPlan fromPlan;

    @ManyToOne
    @JoinColumn(name = "to_plan_id", nullable = false)
    private SubscriptionPlan toPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", nullable = false)
    private SubscriptionStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false)
    private SubscriptionStatus toStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private SubscriptionHistory_Reason reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;
    
    @Column(name = "changed_by", nullable = true)
    private UUID changedBy; // Could be user ID or system ID for automated changes

}