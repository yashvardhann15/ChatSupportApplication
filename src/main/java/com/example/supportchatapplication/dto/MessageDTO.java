package com.example.supportchatapplication.dto;

import com.example.supportchatapplication.model.Message;
import com.example.supportchatapplication.model.enums.SenderType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MessageDTO {

    private UUID id;
    private UUID conversationId;
    private SenderType senderType;
    private UUID senderId;
    private String body;
    private LocalDateTime createdAt;

    /**
     * Helper method to convert a 'live' Message entity into a 'safe' DTO.
     */
    public static MessageDTO fromEntity(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setId(message.getId());
        dto.setConversationId(message.getConversation().getId()); // Flatten the object
        dto.setSenderType(message.getSenderType());
        dto.setSenderId(message.getSenderId());
        dto.setBody(message.getBody());
        dto.setCreatedAt(message.getCreatedAt());
        return dto;
    }
}