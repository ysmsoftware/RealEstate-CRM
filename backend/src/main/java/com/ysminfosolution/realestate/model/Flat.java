package com.ysminfosolution.realestate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "flat")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Flat extends BaseEntity {

    public enum Status {
        Vacant,
        Booked,
        Registered
    }

    @Column(name = "property_number", nullable = false)
    private String propertyNumber;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;
    
    @Column(name = "area", nullable = false)
    private Double area;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wing_id", referencedColumnName = "id", nullable = false)
    private Wing wing;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", referencedColumnName = "id", nullable = false)
    private Floor floor;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

}
