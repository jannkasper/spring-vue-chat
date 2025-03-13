package com.kasper.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChatRoomRequest {
    
    @NotBlank(message = "Chat room name is required")
    @Size(min = 3, max = 100, message = "Chat room name must be between 3 and 100 characters")
    private String name;
    
    private boolean isPrivate;
}