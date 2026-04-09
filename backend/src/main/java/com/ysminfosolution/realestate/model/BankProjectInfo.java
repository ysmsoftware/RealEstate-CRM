package com.ysminfosolution.realestate.model;

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

@Table(name = "bank_project_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class BankProjectInfo extends BaseEntity {

    @Column(name = "bank_name", nullable = false)
    private String bankName;


    @Column(name = "branch_name", nullable = false)
    private String branchName;

    @Column(name = "contact_person", nullable = false)
    private String contactPerson;

    @Column(name = "contact_number", nullable = false, length = 15)
    private String contactNumber;

    @Column(name = "ifsc_code", nullable = false)
    private String ifscCode;

    @Column(name = "account_number", nullable = false)
    private String accountNumber;

    @Column(name = "account_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AccountType accountType;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
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
