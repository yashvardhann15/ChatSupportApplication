package com.example.supportchatapplication.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass // <-- The most important annotation
@EntityListeners(AuditingEntityListener.class) // <-- This enables automatic auditing
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @CreatedDate // <-- Tells Spring to manage this field on create
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // <-- Tells Spring to manage this field on update
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private boolean deleted = false; // Your "isDeleted" field
}