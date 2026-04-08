package com.ysminfosolution.realestate.model;


import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Table(
    name = "projects",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "project_name"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Project extends BaseEntity {

    public enum Status {
        UPCOMING,
        IN_PROGRESS,
        COMPLETED
    }

    @ManyToOne
    @JoinColumn(name = "org_id", referencedColumnName = "id", nullable = false)
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

    @Column(name = "pincode", nullable = true)
    private String pincode;

    @Column(name = "letterhead_url", nullable = false)
    private String letterheadUrl;

    @Column(name = "locked", nullable = false)
    private Boolean locked = false; // If true, only read-only access to project details, no edits allowed

    @Column(name = "lock_reason", nullable = true)
    private String lockReason; // Reason for locking the project, e.g., "Downgraded subscription - only basic details visible"

}
