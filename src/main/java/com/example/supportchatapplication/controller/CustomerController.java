package com.example.supportchatapplication.controller;

import com.example.supportchatapplication.model.Customer;
import com.example.supportchatapplication.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerRepository customerRepository;

    /**
     * DTO for the customer's initial registration.
     */
    public record CustomerRegistrationRequest(String email, String name) {}

    /**
     * POST /api/customer/register
     * Called by the customer UI on load to get a persistent Customer ID.
     * This will find an existing customer or create a new one.
     */
    @PostMapping("/register")
    public ResponseEntity<Customer> registerCustomer(@RequestBody CustomerRegistrationRequest request) {
        
        Customer customer = customerRepository.findByEmail(request.email())
                .orElseGet(() -> {
                    // This customer is new, create them
                    Customer newCustomer = new Customer();
                    newCustomer.setEmail(request.email());
                    newCustomer.setName(request.name());
                    // You could set default context JSON here, e.g.:
                    // newCustomer.setCustomerContext("{\"level\":\"new\"}");
                    return customerRepository.save(newCustomer);
                });
        
        // Return the full Customer object (including the ID)
        return ResponseEntity.ok(customer);
    }
}