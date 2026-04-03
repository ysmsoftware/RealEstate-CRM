package com.ysminfosolution.realestate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

}
