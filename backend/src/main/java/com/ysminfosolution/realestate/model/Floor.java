package com.ysminfosolution.realestate.model;

import java.util.HashSet;
import java.util.Set;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "floor")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Floor extends BaseEntity {

    public enum PropertyType {
        Residential,
        Commercial
    }
    
    @Column(name = "floor_no", nullable = false)
    private Short floorNo;
    
    @Column(name = "floor_name", nullable = false)
    private String floorName;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "property_type", nullable = false)
    private PropertyType propertyType;
    
    @Column(name = "property", nullable = false, length = 20)
    private String property;
    
    @Column(name = "area", nullable = false)
    private Double area;
    
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id", nullable = false)
    private Project project;

    @OneToMany(mappedBy = "floor")
    private Set<Flat> flats = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wing_id", referencedColumnName = "id", nullable = false)
    private Wing wing;

}
