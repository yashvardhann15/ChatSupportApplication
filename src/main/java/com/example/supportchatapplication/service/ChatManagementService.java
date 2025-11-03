package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.Conversation;
import com.example.supportchatapplication.model.Message;
import com.example.supportchatapplication.model.enums.ConversationStatus;
import com.example.supportchatapplication.model.enums.SenderType;
import com.example.supportchatapplication.repository.AgentRepository;
import com.example.supportchatapplication.repository.ConversationRepository;
import com.example.supportchatapplication.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import com.example.supportchatapplication.dto.MessageDTO;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatManagementService {

    private final ConversationRepository conversationRepository;
    private final AgentRepository agentRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageRepository messageRepository;

    public record ChatCloseEvent(UUID conversationId, String message) {}

    @Transactional
    public void closeConversation(UUID conversationId, UUID agentId) {

        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found."));

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new RuntimeException("Agent not found."));

        conversation.setStatus(ConversationStatus.CLOSED);
        conversationRepository.save(conversation);

        if (agent.getCurrentConversation() != null && agent.getCurrentConversation().getId().equals(conversationId)) {
            agent.setCurrentConversation(null);
            agentRepository.save(agent);
        }

        String topic = "/topic/conversation/" + conversationId;
        messagingTemplate.convertAndSend(topic, new ChatCloseEvent(conversationId, "Chat has been closed by the agent."));
    }

    @Transactional
    public void saveAndBroadcastMessage(UUID conversationId, UUID senderId, String messageBody) {

        // 1. Find the conversation (we need to fetch related objects eagerly)
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found."));

        if (conversation.getStatus() == ConversationStatus.CLOSED) {
            log.warn("Attempted to send message to a CLOSED conversation: {}", conversationId);
            // Optionally, we could send a "This chat is closed" message back
            // to the sender, but for now, we just stop.
            return; // Stop processing
        }

        // 2. Determine sender type (this logic is fine)
        SenderType senderType;
        if (conversation.getAgent().getId().equals(senderId)) {
            senderType = SenderType.AGENT;
        } else if (conversation.getCustomer().getId().equals(senderId)) {
            senderType = SenderType.CUSTOMER;
        } else {
            throw new SecurityException("Sender is not authorized for this conversation.");
        }

        // 3. Create and save the new message (this logic is fine)
        Message message = new Message();
        message.setConversation(conversation);
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setBody(messageBody);

        Message savedMessage = messageRepository.save(message);

        // 4. --- THIS IS THE FIX ---
        // We create a 'safe' DTO from the entity
        MessageDTO messageDTO = MessageDTO.fromEntity(savedMessage);

        // 5. Broadcast the DTO, not the entity
        String topic = "/topic/conversation/" + conversationId;
        messagingTemplate.convertAndSend(topic, messageDTO); // <-- Broadcast the DTO
    }
}