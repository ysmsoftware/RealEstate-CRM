package com.ysminfosolution.realestate.model;

import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
public class FollowUpNode extends BaseEntity {

    @Column(name = "follow_up_date_time", nullable = false)
    private LocalDateTime followUpDateTime;

    @Column(name = "body", nullable = false)
    private String body;

    @Column(name = "tag")
    private String tag;

    @ManyToOne
    @JoinColumn(name = "follow_up_id", referencedColumnName = "id", nullable = false)
    private FollowUp followUp;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

}
