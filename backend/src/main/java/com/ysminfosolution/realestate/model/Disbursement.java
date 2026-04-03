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

@Table(name = "disbursement")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Disbursement extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @Column(name = "disbursement_title", nullable = false)
    private String disbursementTitle;

    @Column(name = "description", nullable = true)
    private String description;

    @Column(name = "percentage", nullable = false)
    private Float percentage;

}
