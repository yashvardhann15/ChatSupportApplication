package com.example.supportchatapplication.controller;

import com.example.supportchatapplication.dto.ChatMessageRequest;
import com.example.supportchatapplication.dto.CustomerContextResponse;
import com.example.supportchatapplication.dto.CustomerSupportRequest;
import com.example.supportchatapplication.model.enums.IssueType;
import com.example.supportchatapplication.service.CustomerContextService;
import com.example.supportchatapplication.service.ChatManagementService;
import com.example.supportchatapplication.service.KafkaProducerService;
import com.example.supportchatapplication.config.UserInterceptor; // <-- Make sure this is imported
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;


import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final KafkaProducerService kafkaProducerService;
    private final ChatManagementService chatManagementService;
    private final CustomerContextService customerContextService;

    public record CustomerStartChatRequest(
            String email,
            String name,
            String issueTypeString,
            String body
    ) {}

    // --- 1. CUSTOMER: Start Chat (HTTP) ---
    @PostMapping("/api/chat/start")
    @ResponseBody
    public ResponseEntity<String> startChat(@RequestBody CustomerStartChatRequest request) {

        IssueType issueType;
        try {
            issueType = IssueType.valueOf(request.issueTypeString().toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid issue type provided.");
        }

        CustomerSupportRequest payload = new CustomerSupportRequest(
                request.email(),
                request.name(),
                issueType,
                request.body(),
                LocalDateTime.now()
        );

        kafkaProducerService.send(payload);

        return ResponseEntity.accepted().body("Your request is being processed. Please wait for an agent to be assigned.");
    }

    // --- 2. AGENT: Close Chat (WebSocket) ---
    @MessageMapping("/chat/close/{conversationId}")
    public void closeConversation(
            @DestinationVariable UUID conversationId,
            StompHeaderAccessor accessor) {

        // --- FIX IS HERE ---
        // We retrieve the ID from the session attributes where our interceptor put it.
        String userIdString = (String) accessor.getSessionAttributes().get(UserInterceptor.USER_ID_SESSION_KEY);

        if (userIdString == null) {
            throw new SecurityException("User ID not found in session. Cannot close chat.");
        }

        UUID agentId = UUID.fromString(userIdString);
        chatManagementService.closeConversation(conversationId, agentId);
    }

    // --- 3. BOTH: Send Live Message (WebSocket) ---
    @MessageMapping("/chat/send/{conversationId}")
    public void sendChatMessage(
            @DestinationVariable UUID conversationId,
            @Payload ChatMessageRequest request,
            StompHeaderAccessor accessor) {

        // --- FIX IS HERE ---
        // We retrieve the ID from the session attributes here as well.
        String userIdString = (String) accessor.getSessionAttributes().get(UserInterceptor.USER_ID_SESSION_KEY);

        if (userIdString == null) {
            throw new SecurityException("User ID not found in session. Cannot send message.");
        }

        UUID senderId = UUID.fromString(userIdString);

        chatManagementService.saveAndBroadcastMessage(
                conversationId,
                senderId,
                request.body()
        );
    }

    // --- 4. AGENT: Get Customer Context (HTTP) ---
    @GetMapping("/api/chat/{conversationId}/context")
    @ResponseBody
    public ResponseEntity<CustomerContextResponse> getCustomerContext(
            @PathVariable UUID conversationId) {

        CustomerContextResponse context = customerContextService.getContextForConversation(conversationId);
        return ResponseEntity.ok(context);
    }
}