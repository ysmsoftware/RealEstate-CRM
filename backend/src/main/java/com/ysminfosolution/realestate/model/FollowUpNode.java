package com.ysminfosolution.realestate.model;

import java.time.LocalDateTime;
import java.util.UUID;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "follow_up_node")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class FollowUpNode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "follow_up_node_id", nullable = false)
    private UUID followUpNodeId;

    @Column(name = "follow_up_date_time", nullable = false)
    private LocalDateTime followUpDateTime;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "tag")
    private String tag;

    @ManyToOne
    @JoinColumn(name = "follow_up_id", referencedColumnName = "follow_up_id", nullable = false)
    private FollowUp followUp;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;
}
