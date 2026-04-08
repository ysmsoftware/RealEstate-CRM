package com.ysminfosolution.realestate.model;

import java.time.Instant;

import com.ysminfosolution.realestate.model.enums.PaymentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "payment_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class PaymentOrder extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(name = "razorpay_order_id", nullable = false, unique = true)
    private String razorpayOrderId;

    // TODO: Enforce uniqueness in service layer
    @Column(name = "razorpay_payment_id", nullable = true)
    private String razorpayPaymentId;

    @Column(name = "razorpay_signature", nullable = true)
    private String razorpaySignature;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @Column(name = "amount_paise", nullable = false)
    private Long amountPaise; // in paise

    @Column(name = "currency", nullable = false)
    private String currency = "INR"; // e.g., INR

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status; // e.g., CREATED, PAID, FAILED

    @ManyToOne
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(name = "paid_at", nullable = true)
    private Instant paidAt;

    @Column(name = "payment_attempts", nullable = false)
    private Integer paymentAttempts = 0; // To track payment attempts

    @Column(name = "failure_code")
    private String failureCode;

    @Column(name = "failure_description")
    private String failureDescription;

    // Optimistic locking field to handle concurrent updates
    @Version
    private Long version;

}
