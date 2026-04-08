package com.ysminfosolution.realestate.model;

import com.ysminfosolution.realestate.model.enums.PermissionKey;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "user_permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class UserPermission extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "permission", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionKey permissionKey;

    @ManyToOne
    @JoinColumn(name = "granted_by", nullable = false)
    private User grantedBy;


}
