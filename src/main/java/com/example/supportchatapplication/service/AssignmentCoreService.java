package com.example.supportchatapplication.service;

import com.example.supportchatapplication.dto.CustomerSupportRequest;
import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.Conversation;
import com.example.supportchatapplication.model.Customer;
import com.example.supportchatapplication.model.Message;
import com.example.supportchatapplication.model.enums.ConversationStatus;
import com.example.supportchatapplication.model.enums.SenderType;
import com.example.supportchatapplication.repository.AgentRepository;
import com.example.supportchatapplication.repository.ConversationRepository;
import com.example.supportchatapplication.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.supportchatapplication.model.PendingRequest;
import com.example.supportchatapplication.repository.MessageRepository;

@Service
@RequiredArgsConstructor
public class AssignmentCoreService {

    private final CustomerRepository customerRepository;
    private final AgentRepository agentRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    /**
     * Handles the atomic creation of the customer, conversation, initial message, and agent lock.
     */
    @Transactional
    public Conversation createAndLockAssignment(PendingRequest request, Agent agent) {

        // 1. Find or Create Customer
        Customer customer = customerRepository.findByEmail(request.getCustomerEmail())
                .orElseGet(() -> {
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(request.getCustomerEmail());
                    newCustomer.setName(request.getCustomerName());
                    return customerRepository.save(newCustomer);
                });



        // 3. CREATE (but don't save yet) Conversation
        Conversation conversation = new Conversation();
        conversation.setCustomer(customer);
        conversation.setAgent(agent);
        conversation.setStatus(ConversationStatus.OPEN);
        conversation.setUrgencyScore(request.getUrgencyScore());

        // 4. CREATE the initial message
        Message initialMessage = new Message();
        initialMessage.setConversation(conversation); // <-- Link to parent
        initialMessage.setSenderType(SenderType.CUSTOMER);
        initialMessage.setSenderId(customer.getId());
        initialMessage.setBody(request.getMessageBody());

        // 5. THIS IS THE FIX: Add the message to the conversation's list
        // This tells JPA to save the message when the conversation is saved.
        conversation.getMessages().add(initialMessage);

        // 6. SAVE the conversation (and the message, via cascade)
        Conversation savedConversation = conversationRepository.save(conversation);

        // 7. Lock the Agent (CRITICAL)
        agent.setCurrentConversation(savedConversation);
        agentRepository.save(agent);

        return savedConversation;
    }
}