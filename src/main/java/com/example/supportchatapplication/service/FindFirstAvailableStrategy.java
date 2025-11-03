package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.enums.AgentStatus;
import com.example.supportchatapplication.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindFirstAvailableStrategy implements AgentAssignmentStrategy {

    private final AgentRepository agentRepository;

    /**
     * Strategy: Query the database for the first agent who is ONLINE and FREE.
     */
    @Override
    public Optional<Agent> findAvailableAgent() {
        // Uses the custom method defined in AgentRepository
        return agentRepository.findTopByStatusAndCurrentConversationIsNull(AgentStatus.ONLINE);
    }
}