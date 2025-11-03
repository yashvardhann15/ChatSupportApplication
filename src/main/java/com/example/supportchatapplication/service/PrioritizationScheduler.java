package com.example.supportchatapplication.service;

import com.example.supportchatapplication.model.PendingRequest;
import com.example.supportchatapplication.repository.PendingRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrioritizationScheduler {

    private final PendingRequestRepository pendingRequestRepository;
    private final AgentAssignmentService agentAssignmentService;

    /**
     * Runs every 5 seconds to attempt assignment for high-priority requests.
     */
    @Scheduled(fixedDelay = 10000)
    public void runPrioritizedAssignment() {
        log.debug("SCHEDULER: Running assignment check for pending requests...");

        // 1. Fetch the highest priority requests (P5 appears first)
        List<PendingRequest> pendingRequests = pendingRequestRepository.findTop10ByOrderByUrgencyScoreDescReceivedAtAsc();

        if (pendingRequests.isEmpty()) {
            return; // Nothing to do
        }

        // 2. Attempt to process the top requests in order
        for (PendingRequest request : pendingRequests) {
            
            // 3. Ask the assignment service to ATTEMPT the assignment
            // This service will return true if successful, false if no agents are free
            boolean assigned = agentAssignmentService.attemptAssignment(request);
            
            if (!assigned) {
                // If this assignment failed (no free agents), stop trying.
                // We'll try again in 5 seconds.
                log.info("SCHEDULER: No free agents. Pausing assignment cycle.");
                break;
            }
            // If assigned, loop continues to the next highest priority request
        }
    }
}