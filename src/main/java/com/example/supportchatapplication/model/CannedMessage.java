package com.example.supportchatapplication.model;

import com.example.supportchatapplication.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "canned_messages")
@SQLDelete(sql = "UPDATE canned_messages SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
public class CannedMessage extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String shortcut; // e.g., "/greet"

    @Column(columnDefinition = "TEXT", nullable = false)
    private String text; // The full message text
}