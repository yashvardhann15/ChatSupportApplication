package com.example.supportchatapplication.service;

import com.example.supportchatapplication.dto.CustomerSupportRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("${kafka.topic.incoming-requests}") // Define this in application.yml
    private String topicName;
    
    private final KafkaTemplate<String, CustomerSupportRequest> kafkaTemplate;

    /**
     * Publishes a new customer request to the Kafka queue for assignment.
     */
    public void send(CustomerSupportRequest request) {
        // Use the customer's email as the message key for partitioning
        kafkaTemplate.send(topicName, request.email(), request); 
    }
}