package com.ysminfosolution.realestate.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ysminfosolution.realestate.model.Floor.PropertyType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "enquiries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Enquiry {

    public enum Status {
        ONGOING,
        CANCELLED,
        BOOKED,
        COLD_LEAD,
        WARM_LEAD,
        HOT_LEAD
        
    }

    @Id
    @Column(name = "enquiry_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID enquiryId;
    
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    //--------------------------------------------------------------------------------------
    // ^ Prefrences for the enquiry (more filters can be added like specifc floorNo)

    @Column(name = "property_type", nullable = true)
    private PropertyType propertyType;
    
    @Column(name = "property", nullable = true, length = 20)
    private String property;

    @Column(name = "area", nullable = true)
    private Double area;

    //--------------------------------------------------------------------------------------

    @Column(name = "budget", nullable = false)
    private String budget;

    // & (Newspaper, ADS)
    @Column(name = "reference", nullable = false)
    private String reference;

    @Column(name = "reference_name", nullable = false)
    private String referenceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(referencedColumnName = "client_id", name = "client_id", nullable = false)
    private ClientUserInfo client;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

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
