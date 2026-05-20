package com.stackly.common.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "Standup_Session")
public class StandupSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="Standup_Session_id")
    private Long StandupSessionId;

    private String userId;

    private LocalDate sessionDate;

    private Long currentQuestionOrder;

    private Boolean completed = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Teams teamId;
}
