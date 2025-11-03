package com.example.supportchatapplication.model;

import com.example.supportchatapplication.model.BaseEntity;
import com.example.supportchatapplication.model.enums.IssueType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "pending_requests")
@SQLDelete(sql = "UPDATE pending_requests SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class PendingRequest extends BaseEntity {

    @Column(nullable = false)
    private String customerEmail;

    @Column(nullable = false)
    private String customerName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueType issueType;

    @Column(nullable = false)
    private int urgencyScore; // Stored here for easy sorting

    @Column(columnDefinition = "TEXT", nullable = false)
    private String messageBody;

    @Column(nullable = false)
    private LocalDateTime receivedAt;
}