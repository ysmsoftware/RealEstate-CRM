package com.ysminfosolution.realestate.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicUpdate;
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

@Table(name = "client_user_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@DynamicUpdate
public class ClientUserInfo {

    @Id
    @Column(name = "client_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID clientId;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    // ~ Specifies the unique email of the client, if found across any other client then the clients are considered same
    @Column(name = "email", nullable = true, unique = false)
    private String email;

    @Column(name = "mobile_number", nullable = true, length = 15)
    private String mobileNumber;

    @Column(name = "dob", nullable = true)
    private LocalDate dob;

    @Column(name = "landline_number", nullable = true, length = 15)
    private String landlineNumber;

    @Column(name = "city", nullable = true)
    private String city;

    @Column(name = "address", nullable = true, length = 500)
    private String address;

    @Column(name = "occupation", nullable = true)
    private String occupation;

    @Column(name = "company", nullable = true)
    private String company;

    @Column(name = "pan_no", nullable = true, length = 20)
    private String panNo;

    @Column(name = "aadhar_no", nullable = true, length = 20)
    private String aadharNo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;


    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
    
}
