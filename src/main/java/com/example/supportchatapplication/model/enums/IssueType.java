package com.example.supportchatapplication.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IssueType {
    
    // Urgency scores: 5 (Highest) -> 1 (Lowest)
    PAYMENT_DEDUCTED_NOT_RECEIVED(5, "Payment deducted but not received"), // Highest Urgency
    LOAN_APPROVAL_INQUIRY(4, "Inquiry about loan approval status"),
    PAYMENT_STALLED(3, "Payment stalled or delayed"),
    ACCOUNT_UPDATE_INQUIRY(2, "Inquiry about updating account information"),
    GENERAL_INQUIRY(1, "General information request");

    private final int urgencyScore;
    private final String description;
}