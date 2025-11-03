package com.example.supportchatapplication.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.hibernate.type.SqlTypes;

@Data
@EqualsAndHashCode(callSuper = true) // <-- Important for Lombok + inheritance
@Entity
@Table(name = "customers")
@SQLDelete(sql = "UPDATE customers SET deleted = true WHERE id = ?") // <-- Pro-move 1
@Where(clause = "deleted = false") // <-- Pro-move 2
public class Customer extends BaseEntity { // <-- Extend BaseEntity

    // The 'id', 'createdAt', 'updatedAt', and 'deleted' fields
    // are now inherited automatically. You can delete them from here.

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private String customerContext;
}