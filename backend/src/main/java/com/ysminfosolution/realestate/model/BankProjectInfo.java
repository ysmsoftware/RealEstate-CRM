package com.ysminfosolution.realestate.model;

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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "bank_project_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BankProjectInfo {
    
    @Id
    @Column(name = "bank_project_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID bankProjectId;

    @Column(name = "bank_name", nullable = false)
    private String bankName;


    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

}
