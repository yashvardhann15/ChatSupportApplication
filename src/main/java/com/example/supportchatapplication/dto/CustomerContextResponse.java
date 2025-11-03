package com.example.supportchatapplication.dto;

import java.util.List;

/**
 * Main DTO for the entire customer context panel.
 */
public record CustomerContextResponse(
    String customerContextJson,
    List<PastConversationDTO> pastConversations
) {}