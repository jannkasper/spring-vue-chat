package com.kasper.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class MessageRequest {
    
    @NotNull(message = "Sender ID is required")
    private UUID senderId;
    
    @NotBlank(message = "Message content is required")
    private String message;
    
    private String fileUrl;
    
    private boolean encrypted;
}