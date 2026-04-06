package com.ysminfosolution.realestate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "plan_entitlements", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"subscription_plan_id", "entitlement_code"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PlanEntitlement extends BaseEntity {

    // MAX_PROJECTS, MAX_EMPLOYEES, CAN_EXPORT_PDF, MAX_STORAGE_GB
    @Column(name = "entitlement_code", nullable = false)
    private String entitlementCode;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "limit_type", nullable = false)
    private String limitType;

    // -1 = unlimited, 0 = not allowed, >0 = specific limit, true/false for boolean
    @Column(name = "limit_value", nullable = false)
    private String limitValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;


    
}
