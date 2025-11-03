package com.example.supportchatapplication.dto;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A single, unified search result.
 * Can represent either a Customer or a Message.
 */
public record SearchResultDTO(
    UUID id,                 // The ID of the customer or message
    String type,             // "CUSTOMER" or "MESSAGE"
    String text,             // The customer's name/email or the message body snippet
    LocalDateTime createdAt
) {}