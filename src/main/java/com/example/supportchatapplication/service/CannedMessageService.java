package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.CannedMessage;
import com.example.supportchatapplication.repository.CannedMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CannedMessageService {

    private final CannedMessageRepository cannedMessageRepository;

    public List<CannedMessage> getAllCannedMessages() {
        // We can add sorting here later if needed
        return cannedMessageRepository.findAll();
    }
}