package com.ysminfosolution.realestate.model;

import java.util.UUID;

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

@Table(name = "disbursement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Disbursement {
    
    @Id
    @Column(name = "disbursement_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID disbursementId;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @Column(name = "disbursement_title", nullable = false)
    private String disbursementTitle;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "percentage", nullable = false)
    private Float percentage;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
    
}
