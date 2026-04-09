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

@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document extends BaseEntity {

    public enum DocumentType {
        FloorPlan,
        BasementPlan,
        LetterHead,
        Other
    }
    
    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type", nullable = false, length = 100)
    private DocumentType documentType;

    @Column(name = "document_title", nullable = false)
    private String documentTitle;

    @Column(name = "document_url", nullable = false)
    private String documentURL;

}
