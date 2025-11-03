package com.example.supportchatapplication.model;

import com.example.supportchatapplication.model.enums.SenderType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "messages")
@SQLDelete(sql = "UPDATE messages SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class Message extends BaseEntity {

    // This links the message back to its one conversation
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private Conversation conversation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderType senderType;

    // We use a generic UUID here so it can be either a
    // customerId or an agentId from your 'Customer' or 'Agent' table
    @Column(nullable = false)
    private UUID senderId;

    @Column(columnDefinition = "TEXT", nullable = false) // Use TEXT for long messages
    private String body;

    // The 'sentAt' timestamp is handled by the 'createdAt'
    // field from your BaseEntity, so no need to add it!
}