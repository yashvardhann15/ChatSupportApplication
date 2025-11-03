package com.example.supportchatapplication.repository;

import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.enums.AgentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AgentRepository extends JpaRepository<Agent, UUID> {

    Optional<Agent> findByEmail(String email);

    // This is the custom query for your "Assignment Strategy"
    // It finds all agents who are ONLINE and are NOT in a current chat (currentConversation is NULL)
    // The "Top1" part just grabs the first one it finds.
    Optional<Agent> findTopByStatusAndCurrentConversationIsNull(AgentStatus status);
}