package com.example.supportchatapplication.service;

import com.example.supportchatapplication.dto.SearchResponse;
import com.example.supportchatapplication.dto.SearchResultDTO;
import com.example.supportchatapplication.model.Customer;
import com.example.supportchatapplication.model.Message;
import com.example.supportchatapplication.repository.CustomerRepository;
import com.example.supportchatapplication.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final CustomerRepository customerRepository;
    private final MessageRepository messageRepository;

    /**
     * Searches both customers and messages for a query term.
     * @param query The search term.
     * @return A consolidated list of results, sorted by date.
     */
    @Transactional(readOnly = true) // This is a read-only operation
    public SearchResponse searchAll(String query) {
        
        // 1. Search Customers
        List<SearchResultDTO> customerResults = customerRepository
                .findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(query, query)
                .stream()
                .map(this::mapCustomerToResult)
                .toList();

        // 2. Search Messages
        List<SearchResultDTO> messageResults = messageRepository
                .findByBodyContainingIgnoreCase(query)
                .stream()
                .map(this::mapMessageToResult)
                .toList();

        // 3. Combine the two lists
        List<SearchResultDTO> combinedResults = Stream.concat(
                customerResults.stream(), 
                messageResults.stream()
            )
            .sorted((r1, r2) -> r2.createdAt().compareTo(r1.createdAt())) // Sort newest first
            .collect(Collectors.toList());

        return new SearchResponse(combinedResults);
    }

    /**
     * Helper method to convert a Customer entity to a standard SearchResultDTO.
     */
    private SearchResultDTO mapCustomerToResult(Customer customer) {
        return new SearchResultDTO(
            customer.getId(),
            "CUSTOMER",
            customer.getName() + " (" + customer.getEmail() + ")",
            customer.getCreatedAt()
        );
    }

    /**
     * Helper method to convert a Message entity to a standard SearchResultDTO.
     */
    private SearchResultDTO mapMessageToResult(Message message) {
        // Truncate long messages for the preview
        String text = message.getBody().length() > 100 
            ? message.getBody().substring(0, 100) + "..." 
            : message.getBody();
            
        return new SearchResultDTO(
            message.getId(),
            "MESSAGE",
            text,
            message.getCreatedAt()
        );
    }
}