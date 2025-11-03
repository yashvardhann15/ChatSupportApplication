package com.example.supportchatapplication.repository;

import com.example.supportchatapplication.model.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    
    // We'll add more complex queries here later if needed, but
    // JpaRepository gives us save(), findById(), and delete() for free.
    List<Conversation> findTop10ByCustomer_IdOrderByCreatedAtDesc(UUID customerId);
}