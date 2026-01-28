package com.ysminfosolution.realestate.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "employee_user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EmployeeUserInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_id", nullable = false)
    private UUID employeeId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "allocated_employee_project", // name of the join table
            joinColumns = @JoinColumn(name = "employee_id"), // FK to EmployeeUserInfo
            inverseJoinColumns = @JoinColumn(name = "project_id") // FK to Project
    )
    private Set<Project> projects = new HashSet<>();
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
