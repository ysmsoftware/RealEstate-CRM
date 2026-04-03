package com.ysminfosolution.realestate.model;

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
public class Task extends BaseEntity {

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "follow_up_id", referencedColumnName = "id", nullable = false)
    private FollowUp followUp;

}
