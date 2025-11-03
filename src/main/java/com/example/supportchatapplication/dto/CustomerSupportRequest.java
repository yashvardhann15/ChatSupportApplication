package com.example.supportchatapplication.dto;

import com.example.supportchatapplication.model.enums.IssueType;

import java.time.LocalDateTime;

public record CustomerSupportRequest(
    String email,
    String name,
    IssueType issueType, // The selected urgency enum
    String messageBody,
    LocalDateTime timestamp // Time the request was made
) {}