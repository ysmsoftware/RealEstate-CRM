package com.ysminfosolution.realestate.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Employee extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToMany
    @JoinTable(name = "allocated_employee_project", // name of the join table
            joinColumns = @JoinColumn(name = "employee_id"), // FK to employee
            inverseJoinColumns = @JoinColumn(name = "project_id") // FK to Project
    )
    private Set<Project> projects = new HashSet<>();

}
