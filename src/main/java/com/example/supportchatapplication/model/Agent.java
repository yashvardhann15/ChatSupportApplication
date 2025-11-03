package com.example.supportchatapplication.model;

import com.example.supportchatapplication.model.enums.AgentStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@EqualsAndHashCode(callSuper = true) // Important for Lombok + inheritance
@Entity
@Table(name = "agents")
@SQLDelete(sql = "UPDATE agents SET deleted = true WHERE id = ?") // Intercepts deletes
@Where(clause = "deleted = false") // Auto-filters all queries
public class Agent extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgentStatus status = AgentStatus.OFFLINE;

    // This is the link for "one agent, one customer at a time"
    // It's nullable, so if it's NULL, the agent is FREE.
    // We add unique=true to enforce this rule at the DB level.
    @OneToOne
    @JoinColumn(name = "current_conversation_id", unique = true)
    private Conversation currentConversation;
}