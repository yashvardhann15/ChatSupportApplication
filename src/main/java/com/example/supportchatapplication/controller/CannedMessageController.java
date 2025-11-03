package com.example.supportchatapplication.controller;

import com.example.supportchatapplication.model.CannedMessage;
import com.example.supportchatapplication.service.CannedMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/canned-messages")
@RequiredArgsConstructor
public class CannedMessageController {

    private final CannedMessageService cannedMessageService;

    /**
     * GET /api/canned-messages
     * Fetches all available canned messages for the agent's UI.
     */
    @GetMapping
    public ResponseEntity<List<CannedMessage>> getAllCannedMessages() {
        return ResponseEntity.ok(cannedMessageService.getAllCannedMessages());
    }
}