package com.example.supportchatapplication.repository;

import com.example.supportchatapplication.model.Message; // (or .Models)
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    // We don't need custom queries here for now,
    // as we'll get messages through the Conversation object.
    List<Message> findByBodyContainingIgnoreCase(String query);
}