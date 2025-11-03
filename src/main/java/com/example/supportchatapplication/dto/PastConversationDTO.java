package com.example.supportchatapplication.dto;

import com.example.supportchatapplication.model.enums.ConversationStatus;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A lightweight summary of a single past conversation.
 */
public record PastConversationDTO(
    UUID conversationId,
    LocalDateTime createdAt,
    ConversationStatus status,
    int urgencyScore
) {}