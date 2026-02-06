package com.ysminfosolution.realestate.model;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(
    name = "projects",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "project_name"})
    }
)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project {

    public enum Status {
        UPCOMING,
        IN_PROGRESS,
        COMPLETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @EqualsAndHashCode.Include
    @Column(name = "project_id", nullable = false)
    private UUID projectId;

    @ManyToOne
    @JoinColumn(name = "org_id", referencedColumnName = "org_id", nullable = false)
    private Organization organization;

    @Column(name = "project_name", nullable = false)
    private String projectName;

    // ^ Percentage
    @Column(name = "progress", nullable = false)
    private Short progress;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "completion_date", nullable = false)
    private LocalDate completionDate;

    @Column(name = "maharera_no", nullable = false, unique = true)
    private String mahareraNo;

    @Column(name = "project_address", nullable = false)
    private String projectAddress;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

}
