package com.ysminfosolution.realestate.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Task {

    @Id
    @Column(name = "task_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID taskId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "follow_up_id", referencedColumnName = "follow_up_id", nullable = false)
    private FollowUp followUp;

}
