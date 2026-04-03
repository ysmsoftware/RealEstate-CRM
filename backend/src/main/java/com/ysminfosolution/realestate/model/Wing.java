package com.ysminfosolution.realestate.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "wing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Wing extends BaseEntity {

    @Column(name = "wing_name", nullable = false)
    private String wingName;

    @Column(name = "no_of_floors", nullable = false)
    private Integer noOfFloors;

    @Column(name = "no_of_properties", nullable = false)
    private Integer noOfProperties;

    @OneToMany(mappedBy = "wing")
    private Set<Floor> floors = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

}
