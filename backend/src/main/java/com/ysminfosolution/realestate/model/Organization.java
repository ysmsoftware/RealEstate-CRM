package com.ysminfosolution.realestate.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "organization")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Organization extends BaseEntity {

    @Column(name = "org_email", nullable = false, unique = true)
    private String orgEmail;

    @Column(name = "org_name", nullable = false)
    private String orgName;

    @Column(name = "logo_url")
    private String logoUrl;

}
