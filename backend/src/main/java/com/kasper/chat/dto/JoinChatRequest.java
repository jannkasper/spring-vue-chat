package com.kasper.chat.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class JoinChatRequest {
    
    @NotNull(message = "User ID is required")
    private UUID userId;
}