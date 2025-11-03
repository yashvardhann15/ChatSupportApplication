package com.example.supportchatapplication.model;

import com.example.supportchatapplication.model.enums.ConversationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "conversations")
@SQLDelete(sql = "UPDATE conversations SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Conversation extends BaseEntity {

    // This links the conversation to one customer
    // A customer can have many conversations
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // This links the conversation to one agent
    // An agent can have many conversations (over time)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_agent_id")
    private Agent agent;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversationStatus status;

    @Column(nullable = false)
    private int urgencyScore = 0; // For your priority requirement

    // This creates the "WhatsApp" style chat log
    // A conversation has many messages
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC") // Use the 'createdAt' from BaseEntity to order messages
    private List<Message> messages = new ArrayList<>();
}