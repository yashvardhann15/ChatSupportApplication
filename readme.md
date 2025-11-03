                          ┌──────────────────────────────────────────────┐
                          │                FRONTEND (UI)                 │
                          │                                              │
                          │     ┌────────────────────────────┐           │
                          │     │   CUSTOMER SIDE (Chat UI)  │           │
                          │     └────────────────────────────┘           │
                          │     ┌────────────────────────────┐           │
                          │     │    AGENT SIDE (Agent UI)   │           │
                          │     └────────────────────────────┘           │
                          └──────────────────────────────────────────────┘
                                             │
                                             │
                    ┌────────────────────────┴────────────────────────┐
                    │                SPRING BOOT APP                  │
                    │ (Chat Backend — Async + WS + Kafka + DB)        │
                    └─────────────────────────────────────────────────┘
                                             │
                                             ▼

───────────────────────────────────────────────────────────────────────────────
## 1️⃣ Agent Onboarding Flow
───────────────────────────────────────────────────────────────────────────────

[HTTP]  Agent Login/Register
----------------------------------------
Agent UI ──► POST /api/agent/register (email, role)
│
▼
AgentController
│
▼
AgentService  ──► DB.agents:
- Set status = ONLINE
- currentConversation = NULL
  │
  ▼
  Response 200 OK → AgentId returned

[WS]  Agent Connects to WebSocket
----------------------------------------
Agent UI ──► ws://.../ws-connect
│
▼
UserInterceptor sets agentId in session
│
▼
Agent subscribes to:
/topic/agent/{AgentId}
│
▼
Agent is now WAITING for assignment
───────────────────────────────────────────────────────────────────────────────


───────────────────────────────────────────────────────────────────────────────
## 2️⃣ Customer Request Submission + Prioritization Pipeline
───────────────────────────────────────────────────────────────────────────────

[HTTP]  Customer Registration
----------------------------------------
Customer UI ──► POST /api/customer/register
│
▼
CustomerController  →  DB.customers.add(customer)
│
▼
Response 200 OK → customerId returned

[WS]  Customer WebSocket Connection
----------------------------------------
Customer UI ──► ws://.../ws-connect
│
▼
Customer subscribes to:
/topic/customer/{CustomerId}

[HTTP]  Start Chat Request
----------------------------------------
Customer UI ──► POST /api/chat/start
Body: { customerId, issueType, message }
│
▼
ChatController
│
▼
KafkaProducerService.send("incoming-customer-requests", requestDTO)
│
▼
Response: HTTP 202 ACCEPTED
→ Customer sees “Waiting for an available agent...”

[KAFKA FLOW]
----------------------------------------
Topic: incoming-customer-requests
│
▼
KafkaConsumerService
│
▼
1. Save request to DB.pending_requests
   (with calculated urgencyScore)
   ───────────────────────────────────────────────────────────────────────────────


───────────────────────────────────────────────────────────────────────────────
## 3️⃣ Assignment + Conversation Creation (Scheduler Loop)
───────────────────────────────────────────────────────────────────────────────

[Scheduler Event]
----------------------------------------
⏲ Every 5 seconds:
PrioritizationScheduler.run()
│
▼
pending_requests = findTopByOrderByUrgencyScoreDesc()
│
▼
AgentAssignmentService.attemptAssignment(request)
│
▼
┌───────────────────────────────────────────────────────┐
│ Strategy Pattern: FindFirstAvailableStrategy           │
│   → SELECT * FROM agents WHERE status=ONLINE AND        │
│     currentConversation IS NULL LIMIT 1                │
└───────────────────────────────────────────────────────┘
│
▼
AssignmentCoreService.@Transactional:
1️⃣ Create Conversation
2️⃣ Link Agent.currentConversation = conversation.id
3️⃣ Save initial Message
4️⃣ Delete PendingRequest
│
▼
Dual WebSocket Notifications:

[WS Push 1] → /topic/agent/{AgentId}
{ conversationId, customerInfo }

[WS Push 2] → /topic/customer/{CustomerId}
{ conversationId, agentInfo }

        │
        ▼
✅ Agent and Customer both notified → Open Chat UI
───────────────────────────────────────────────────────────────────────────────


───────────────────────────────────────────────────────────────────────────────
## 4️⃣ Real-Time Communication Flow
───────────────────────────────────────────────────────────────────────────────

Both sides subscribe:
/topic/conversation/{ConversationId}

When user sends a message:

[WS] SEND /app/chat/send/{ConversationId}
----------------------------------------
Payload:
{
senderId,
messageText
}
│
▼
ChatController → ChatManagementService
│
▼
Save message to DB.messages
│
▼
Broadcast via WebSocket:
/topic/conversation/{ConversationId}
→ Sent to both Agent and Customer in real time

───────────────────────────────────────────────────────────────────────────────


───────────────────────────────────────────────────────────────────────────────
## 5️⃣ Additional APIs and Utilities
───────────────────────────────────────────────────────────────────────────────

[HTTP]  GET /api/search?q={term}
→ Searches across customers + messages

[HTTP]  GET /api/canned-messages
→ Returns list of predefined agent messages

[HTTP]  GET /api/chat/{conversationId}/context
→ Returns customer info + past messages

[WS]    /app/chat/close/{conversationId}
→ Closes chat, marks conversation CLOSED,
sets Agent.currentConversation = NULL,
updates Agent.status = ONLINE

───────────────────────────────────────────────────────────────────────────────


───────────────────────────────────────────────────────────────────────────────
## 6️⃣ End-to-End Summary Flow
───────────────────────────────────────────────────────────────────────────────

Customer → POST /api/chat/start
│
▼
KafkaProducerService
│
▼
KafkaConsumerService → DB.pending_requests
│
▼
[Scheduler every 5s]
│
▼
PrioritizationScheduler → AgentAssignmentService
│
▼
AssignmentCoreService → DB.conversations + agents
│
▼
WebSocket Push (Agent & Customer)
│
▼
Live Chat → /topic/conversation/{ConversationId}
│
▼
/app/chat/send → Save → Broadcast → Sync in real time
│
▼
/app/chat/close → End chat → Free agent

───────────────────────────────────────────────────────────────────────────────
