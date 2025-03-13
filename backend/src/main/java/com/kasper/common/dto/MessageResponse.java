package com.kasper.common.dto;

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
public class MessageResponse {
    
    private UUID id;
    private UUID chatRoomId;
    private UserInfo sender;
    private String message;
    private String fileUrl;
    private LocalDateTime createdAt;
    private boolean encrypted;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private UUID id;
        private String username;
    }
    
    // Constructor for simple message response
    public MessageResponse(String message) {
        this.message = message;
    }
}