package com.ysminfosolution.realestate.model;

import java.util.UUID;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(
    name = "users",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"org_id", "email"})
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    public enum Role {
        DEVELOPER,
        ADMIN,
        EMPLOYEE
        // CLIENT
    }

    @Id
    @Column(name = "user_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @ManyToOne
    @JoinColumn(name = "org_id", referencedColumnName = "org_id", nullable = false)
    private Organization organization;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "mobile_number", nullable = true, length = 15)
    private String mobileNumber;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "enabled", nullable = true)
    private boolean enabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
