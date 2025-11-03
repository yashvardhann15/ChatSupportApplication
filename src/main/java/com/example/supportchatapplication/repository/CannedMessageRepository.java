package com.example.supportchatapplication.repository;

import com.example.supportchatapplication.model.CannedMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CannedMessageRepository extends JpaRepository<CannedMessage, UUID> {
    
    // We can just use the built-in findAll()
}