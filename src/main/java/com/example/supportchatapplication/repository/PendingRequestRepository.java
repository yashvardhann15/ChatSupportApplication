package com.example.supportchatapplication.repository;

import com.example.supportchatapplication.model.PendingRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PendingRequestRepository extends JpaRepository<PendingRequest, UUID> {

    /**
     * CRITICAL: Finds the top pending requests, prioritizing by urgency, then time received.
     */
    List<PendingRequest> findTop10ByOrderByUrgencyScoreDescReceivedAtAsc();
}