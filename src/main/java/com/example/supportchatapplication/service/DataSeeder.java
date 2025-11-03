package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.CannedMessage;
import com.example.supportchatapplication.repository.CannedMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CannedMessageRepository cannedMessageRepository;

    @Override
    public void run(String... args) throws Exception {
        // Check if the database is already seeded
        if (cannedMessageRepository.count() == 0) {
            // If it's empty, create and save the default messages
            CannedMessage greet = new CannedMessage();
            greet.setShortcut("/greet");
            greet.setText("Hello! Thank you for contacting support. How can I help you today?");

            CannedMessage auth = new CannedMessage();
            auth.setShortcut("/auth");
            auth.setText("I can help with that. To look up your account, I just need your full name and email address.");

            CannedMessage close = new CannedMessage();
            close.setShortcut("/close");
            close.setText("Is there anything else I can help you with today?");

            cannedMessageRepository.save(greet);
            cannedMessageRepository.save(auth);
            cannedMessageRepository.save(close);
        }
    }
}