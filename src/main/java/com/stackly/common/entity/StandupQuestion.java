package com.stackly.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "standup_questions")
public class StandupQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "standup_question_id")
    private Long standupQuestionId;

    @Column(name = "question_text", nullable = false)
    private String questionText;
    
    @Column(name = "question_order_seq", nullable = false)
    private Long questionOrderSeq;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Teams team;

    @Column(name = "active", columnDefinition = "boolean default true")
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

	
}
