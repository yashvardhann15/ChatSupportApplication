package com.example.supportchatapplication.service;

import com.example.supportchatapplication.dto.CustomerContextResponse;
import com.example.supportchatapplication.dto.PastConversationDTO;
import com.example.supportchatapplication.model.Conversation;
import com.example.supportchatapplication.model.Customer;
import com.example.supportchatapplication.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerContextService {

    private final ConversationRepository conversationRepository;

    @Transactional(readOnly = true) // This is a read-only operation
    public CustomerContextResponse getContextForConversation(UUID currentConversationId) {
        
        // 1. Get the current conversation to find the customer
        Conversation currentConversation = conversationRepository.findById(currentConversationId)
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        Customer customer = currentConversation.getCustomer();

        // 2. Get the customer's static profile info
        String contextJson = customer.getCustomerContext();

        // 3. Get their 10 most recent conversations
        List<Conversation> pastConversations = conversationRepository
                .findTop10ByCustomer_IdOrderByCreatedAtDesc(customer.getId());

        // 4. Map the full Conversation objects to lightweight DTOs
        List<PastConversationDTO> pastDtos = pastConversations.stream()
                .map(convo -> new PastConversationDTO(
                        convo.getId(),
                        convo.getCreatedAt(),
                        convo.getStatus(),
                        convo.getUrgencyScore()
                ))
                .collect(Collectors.toList());

        // 5. Return the complete context object
        return new CustomerContextResponse(contextJson, pastDtos);
    }
}