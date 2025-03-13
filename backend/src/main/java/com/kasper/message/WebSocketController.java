package com.kasper.message;

import com.kasper.message.dto.WebSocketMessage;
import com.kasper.common.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

@Controller
@RequiredArgsConstructor
public class WebSocketController {

    @MessageMapping("/chat/{chatRoomId}")
    @SendTo("/topic/chat/{chatRoomId}")
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public WebSocketMessage processMessage(
            @DestinationVariable("chatRoomId") String chatRoomId,
            @Payload WebSocketMessage message,
            SimpMessageHeaderAccessor headerAccessor,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Set timestamp for the message if not already set
        if (message.getCreatedAt() == null) {
            message.setCreatedAt(LocalDateTime.now());
        }
        
        return message;
    }
    
    @MessageMapping("/chat/{chatRoomId}/typing")
    @SendTo("/topic/chat/{chatRoomId}/typing")
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public WebSocketMessage userTyping(
            @DestinationVariable("chatRoomId") String chatRoomId,
            @Payload WebSocketMessage message,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        message.setType(WebSocketMessage.MessageType.TYPING);
        return message;
    }
}