package com.ysminfosolution.realestate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "subscription_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class SubscriptionPlan extends BaseEntity {

    @Column(name = "plan_code", nullable = false, unique = true)
    private String planCode;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "plan_description")
    private String planDescription;

    @Column(name = "duration_months", nullable = false)
    private int durationMonths;

    @Column(name = "price", nullable = false)
    private double price;

    @Column(name = "active", nullable = false)
    private boolean active;


}
