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

@Table(name = "documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Document {

    public enum DocumentType {
        FloorPlan,
        BasementPlan,
        LetterHead
    }
    
    @Id
    @Column(name = "document_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID documentId;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "project_id", nullable = false)
    private Project project;

    @Column(name = "document_type", nullable = false, length = 100)
    private DocumentType documentType;

    @Column(name = "document_title", nullable = false)
    private String documentTitle;

    @Column(name = "document_url", nullable = false)
    private String documentURL;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
