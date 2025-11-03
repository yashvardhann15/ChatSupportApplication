package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.Conversation;
import com.example.supportchatapplication.model.Customer;
import com.example.supportchatapplication.model.PendingRequest;
import com.example.supportchatapplication.repository.PendingRequestRepository;
import com.example.supportchatapplication.service.AgentAssignmentStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentAssignmentService {

    private final AgentAssignmentStrategy assignmentStrategy;
    private final AssignmentCoreService assignmentCoreService;
    private final SimpMessagingTemplate messagingTemplate;
    private final PendingRequestRepository pendingRequestRepository;

    /**
     * DTO for the WebSocket notification payload.
     * Used for both agent and customer, but the 'participantName' will be different.
     */
    public record AssignmentNotification(
            UUID conversationId,
            String participantName, // Will be Customer's name for Agent, or Agent's name for Customer
            int urgencyScore
    ) {}

    /**
     * Public method called by the PrioritizationScheduler.
     * Attempts to find a free agent and assign a single pending request.
     */
    @Transactional
    public boolean attemptAssignment(PendingRequest request) {

        // 1. EXECUTE STRATEGY: Find an available agent
        Optional<Agent> agentOptional = assignmentStrategy.findAvailableAgent();

        if (agentOptional.isEmpty()) {
            return false; // No free agents
        }

        Agent agent = agentOptional.get();

        // 2. EXECUTE CORE: Create Conversation, Lock Agent, Save Message
        Conversation conversation = assignmentCoreService.createAndLockAssignment(request, agent);

        // 3. CLEANUP: The request is handled, remove it from the pending table.
        pendingRequestRepository.delete(request);

        // --- 4. THE HANDSHAKE (Notify both parties) ---

        // 4a. Notify the Agent (Push 1)
        notifyAssignedAgent(agent, conversation);

        // 4b. Notify the Customer (Push 2)
        notifyCustomer(conversation.getCustomer(), conversation);

        return true; // Assignment was successful
    }

    /**
     * Pushes a notification to the agent's private WebSocket topic.
     */
    private void notifyAssignedAgent(Agent agent, Conversation conversation) {
        String topic = "/topic/agent/" + agent.getId();

        AssignmentNotification notification = new AssignmentNotification(
                conversation.getId(),
                conversation.getCustomer().getName(), // Agent sees Customer's name
                conversation.getUrgencyScore()
        );

        messagingTemplate.convertAndSend(topic, notification);

        log.info("Notified Agent {} of new chat (P={}): {}",
                agent.getId(),
                conversation.getUrgencyScore(),
                conversation.getId());
    }

    /**
     * Pushes a notification to the customer's private WebSocket topic.
     */
    private void notifyCustomer(Customer customer, Conversation conversation) {
        String topic = "/topic/customer/" + customer.getId();

        AssignmentNotification notification = new AssignmentNotification(
                conversation.getId(),
                conversation.getAgent().getName(), // Customer sees Agent's name
                conversation.getUrgencyScore()
        );

        messagingTemplate.convertAndSend(topic, notification);

        log.info("Notified Customer {} of new chat: {}",
                customer.getId(),
                conversation.getId());
    }
}