package com.kasper.message.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebSocketMessage {
    
    private UUID chatRoomId;
    private UUID senderId;
    private String senderUsername;
    private String message;
    private String fileUrl;
    private LocalDateTime createdAt;
    private MessageType type;
    
    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE,
        TYPING
    }
}