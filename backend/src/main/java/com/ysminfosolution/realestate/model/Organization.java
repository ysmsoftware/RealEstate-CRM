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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// * We are using shared schema architecture for multi-tenancy
// * The parent entities (project, user) will have the orgId and all the requests will be authenticated against the combination 
// *    of (orgId, username, password)

@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "org_id", nullable = false, updatable = false)
    private UUID orgId;

    @Column(name = "org_email", nullable = false, unique = true)
    private String orgEmail;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    // ! Later we can add Usage parameters like (active, createdDate, noOfRequests) if usage based subscription
}
