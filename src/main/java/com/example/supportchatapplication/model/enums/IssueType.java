package com.example.supportchatapplication.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum IssueType {

    // --- Payment Issues ---
    PAYMENT_DEDUCTED_NOT_RECEIVED(5, "Payment deducted but not received"),
    DOUBLE_PAYMENT_ISSUE(5, "Duplicate Payment Made"),
    PAYMENT_FAILED(4, "Payment Failed but Amount Debited"),
    PAYMENT_PENDING_CONFIRMATION(3, "Payment Pending Confirmation"),
    REFUND_DELAYED(3, "Refund Delayed"),
    PAYMENT_HISTORY_REQUEST(2, "Payment History Request"),
    BILLING_QUERY(2, "Billing Discrepancy or Clarification"),

    // --- Loan-Related Issues ---
    LOAN_DISBURSEMENT_DELAY(5, "Loan Disbursement Delay"),
    LOAN_APPROVAL_INQUIRY(4, "Loan Approval Inquiry"),
    LOAN_REPAYMENT_QUERY(3, "Loan Repayment or EMI Query"),
    LOAN_FORECLOSURE_REQUEST(2, "Loan Foreclosure / Prepayment Request"),

    // --- Financial / Account Issues ---
    ACCOUNT_VERIFICATION_ISSUE(4, "Account Verification Issue"),
    STATEMENT_REQUEST(3, "Account Statement or Summary Request"),
    ACCOUNT_UPDATE_INQUIRY(2, "Account Update Request"),
    LIMIT_INCREASE_REQUEST(2, "Credit / Transaction Limit Increase"),

    // --- General Inquiries ---
    GENERAL_INQUIRY(1, "General Information or Assistance"),
    FEEDBACK_OR_SUGGESTION(1, "Feedback or Suggestion");

    private final int urgencyScore;
    private final String description;
}