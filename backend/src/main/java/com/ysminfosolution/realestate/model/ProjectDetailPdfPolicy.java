package com.ysminfosolution.realestate.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "project_detail_pdf_policies",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = { "project_id", "policy_name" })
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDetailPdfPolicy extends BaseEntity {


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @Column(name = "policy_name", nullable = false)
    private String policyName;

    @Column(name = "include_project_overview", nullable = false)
    private boolean includeProjectOverview;

    @Column(name = "include_wings", nullable = false)
    private boolean includeWings;

    @Column(name = "include_floors", nullable = false)
    private boolean includeFloors;

    @Column(name = "include_flats", nullable = false)
    private boolean includeFlats;

    @Column(name = "include_bank_project_info", nullable = false)
    private boolean includeBankProjectInfo;

    @Column(name = "include_amenities", nullable = false)
    private boolean includeAmenities;

    @Column(name = "include_disbursements", nullable = false)
    private boolean includeDisbursements;

    @Column(name = "include_documents", nullable = false)
    private boolean includeDocuments;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "project_detail_pdf_policy_documents",
            joinColumns = @JoinColumn(name = "project_detail_pdf_policy_id"))
    @OrderColumn(name = "position")
    @Column(name = "document_id", nullable = false)
    private List<UUID> documentIds = new ArrayList<>();

}
