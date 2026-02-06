package com.ysminfosolution.realestate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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

@Table(name = "follow_up")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FollowUp {
    
    @Id
    @Column(name = "follow_up_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID followUpId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enquiry_id", referencedColumnName = "enquiry_id", nullable = false)
    private Enquiry enquiry;

    @Column(name = "follow_up_next_date", nullable = false)
    private LocalDate followUpNextDate;

    @Column(name = "description", nullable = true, length = 500)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
