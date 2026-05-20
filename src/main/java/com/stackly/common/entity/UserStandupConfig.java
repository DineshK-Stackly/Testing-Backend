package com.stackly.common.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_standup_config")
public class UserStandupConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_standup_config_id")
    private Long userStandupConfigId;

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Teams team;
    
    @Column(name = "user_name")
    private String userName;

    // Time to trigger the questions every day
    @Column(name = "scheduled_time")
    private LocalTime scheduledTime;

    // Active switch for scheduled features
    @Column(name = "active", columnDefinition = "boolean default true")
    private Boolean active = true;

    // Requirements for proactive message sending in Bot Framework
    @Column(name = "service_url", length = 1000)
    private String serviceUrl;

    @Column(name = "conversation_id", length = 500)
    private String conversationId;

    @Column(name = "bot_id", length = 255)
    private String botId;

    @Column(name = "bot_name", length = 255)
    private String botName;

    @Column(name = "tenant_id", length = 255)
    private String tenantId;

    @Column(name = "platform", length = 50)
    private String platform = "SLACK"; // SLACK or TEAMS

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
