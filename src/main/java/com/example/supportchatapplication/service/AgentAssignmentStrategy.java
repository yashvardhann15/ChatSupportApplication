package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.enums.AgentStatus;

import java.util.Optional;

// This interface is the heart of the Strategy Pattern
public interface AgentAssignmentStrategy {

    /**
     * Finds a suitable available agent based on the implemented strategy.
     * @return An Optional containing the Agent if one is found, otherwise empty.
     */
    Optional<Agent> findAvailableAgent();
}