package com.example.supportchatapplication.repository;

import com.example.supportchatapplication.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    // Spring Data JPA automatically understands this method name
    // It will generate a query like: "SELECT * FROM customers WHERE email = ? AND deleted = false"
    Optional<Customer> findByEmail(String email);
    List<Customer> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String nameQuery, String emailQuery);
}