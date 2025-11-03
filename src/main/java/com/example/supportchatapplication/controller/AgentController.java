package com.example.supportchatapplication.controller;

import com.example.supportchatapplication.dto.AgentRegistrationRequest;
import com.example.supportchatapplication.model.Agent;
import com.example.supportchatapplication.model.enums.AgentStatus;
import com.example.supportchatapplication.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private static final String REQUIRED_ROLE = "AGENT";
    private final AgentRepository agentRepository;
    
    // We removed ConversationRepository and SimpMessagingTemplate—they don't belong here.

    // -----------------------------------------------------
    // 1. REGISTER (Login)
    // -----------------------------------------------------
    @PostMapping("/register")
    public ResponseEntity<Agent> registerAgent(@RequestBody AgentRegistrationRequest request) {

        if (!REQUIRED_ROLE.equalsIgnoreCase(request.role())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Agent agent = agentRepository.findByEmail(request.email()).orElseGet(() -> {
            Agent newAgent = new Agent();
            newAgent.setEmail(request.email());
            newAgent.setName(request.email().split("@")[0]);
            return newAgent;
        });

        agent.setStatus(AgentStatus.ONLINE);
        agent.setCurrentConversation(null); 

        Agent savedAgent = agentRepository.save(agent);
        
        return ResponseEntity.ok(savedAgent);
    }
    
    // -----------------------------------------------------
    // 2. LOGOUT (Full Disconnect/Mark Unavailable)
    // -----------------------------------------------------
    @PostMapping("/logout/{agentId}")
    public ResponseEntity<Agent> logoutAgent(@PathVariable UUID agentId) {
        return agentRepository.findById(agentId)
                .map(agent -> {
                    agent.setStatus(AgentStatus.OFFLINE);
                    agent.setCurrentConversation(null);

                    Agent savedAgent = agentRepository.save(agent);
                    return ResponseEntity.ok(savedAgent);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}