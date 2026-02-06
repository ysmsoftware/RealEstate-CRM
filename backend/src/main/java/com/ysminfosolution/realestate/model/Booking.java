package com.ysminfosolution.realestate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(name = "booking")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Booking {
    
    @Id
    @Column(name = "booking_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bookingId;

    @OneToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false)
    private ClientUserInfo client;

    @Column(name = "agreement_amount", nullable = false, length = 20)
    private String agreementAmount;

    @Column(name = "booking_amount", nullable = false, length = 20)
    private String bookingAmount;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "cheque_date", nullable = false)
    private LocalDate chequeDate;

    @Column(name = "cheque_no", nullable = false, length = 20)
    private String chequeNo;

    @Column(name = "gst_percentage", nullable = false)
    private Float gstPercentage;

    @Column(name = "gst_amount", nullable = false, length = 20)
    private String gstAmount;

    @Column(name = "infra_amount", nullable = false, length = 20)
    private String infraAmount;

    @Column(name = "total_amount", nullable = false, length = 20)
    private String totalAmount;

    @Column(name = "rate", nullable = false, length = 20)
    private String rate;

    @OneToOne
    @JoinColumn(name = "property_id", referencedColumnName = "property_id", nullable = false)
    private Flat flat;

    @Column(name = "is_registered", nullable = false)
    private boolean isRegistered;

    @Column(name = "reg_no", nullable = true, length = 20)
    private String regNo;

    @Column(name = "reg_date", nullable = true)
    private LocalDate regDate;

    // ! This field can be omitted
    @OneToOne
    @JoinColumn(name = "enquiry_id", referencedColumnName = "enquiry_id", nullable = true)
    private Enquiry enquiry;

    @Column(name = "is_cancelled", nullable = false)
    private boolean isCancelled;

    @Column(name = "remark", nullable = true)
    private String remark;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
