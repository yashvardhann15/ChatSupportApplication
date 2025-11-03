package com.example.supportchatapplication.service;

import com.example.supportchatapplication.dto.CustomerSupportRequest;
import com.example.supportchatapplication.model.PendingRequest; // <-- IMPORT THIS
import com.example.supportchatapplication.repository.PendingRequestRepository; // <-- IMPORT THIS
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- IMPORT THIS

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {

    // 1. REMOVE AgentAssignmentService
    
    // 2. INJECT the new repository
    private final PendingRequestRepository pendingRequestRepository;

    @KafkaListener(topics = "${kafka.topic.incoming-requests}", groupId = "${spring.kafka.consumer.group-id}")
    @Transactional // Ensure saving to the DB is atomic
    public void consumeNewRequest(CustomerSupportRequest request) {
        log.info("Received request from Kafka. Saving to Pending queue: {}", request.email());

        // 3. MAP DTO to Entity
        PendingRequest pendingRequest = new PendingRequest();
        pendingRequest.setCustomerEmail(request.email());
        pendingRequest.setCustomerName(request.name());
        pendingRequest.setIssueType(request.issueType());
        pendingRequest.setUrgencyScore(request.issueType().getUrgencyScore());
        pendingRequest.setMessageBody(request.messageBody());
        pendingRequest.setReceivedAt(request.timestamp());

        // 4. SAVE to holding table
        pendingRequestRepository.save(pendingRequest);
        
        // Processing stops here. The Scheduler takes over.
    }
}