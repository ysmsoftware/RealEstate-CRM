package com.ysminfosolution.realestate.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "org_usage_counters", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"organization_id", "plan_entitlement_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class OrgUsageCounter extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "plan_entitlement_id", nullable = false)
    private PlanEntitlement planEntitlement;

    @Column(name = "usage_count", nullable = false)
    private int usageCount;
    
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    
}