package com.kasper.chat;

import com.kasper.chat.dto.ChatRoomRequest;
import com.kasper.chat.dto.ChatRoomResponse;
import com.kasper.chat.dto.JoinChatRequest;
import com.kasper.common.dto.MessageResponse;
import com.kasper.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createChatRoom(
            @Valid @RequestBody ChatRoomRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UUID userId = UUID.fromString(userDetails.getId().toString());
        ChatRoomResponse response = chatService.createChatRoom(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UUID userId = UUID.fromString(userDetails.getId().toString());
        List<ChatRoomResponse> chatRooms = chatService.getChatRoomsByUserId(userId);
        return ResponseEntity.ok(chatRooms);
    }
    
    @GetMapping("/{chatRoomId}")
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public ResponseEntity<ChatRoomResponse> getChatRoomById(
            @PathVariable UUID chatRoomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        ChatRoomResponse chatRoom = chatService.getChatRoomById(chatRoomId);
        return ResponseEntity.ok(chatRoom);
    }
    
    @PostMapping("/{chatRoomId}/join")
    public ResponseEntity<MessageResponse> joinChatRoom(
            @PathVariable UUID chatRoomId,
            @Valid @RequestBody JoinChatRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Check permissions
        UUID requesterId = UUID.fromString(userDetails.getId().toString());
        UUID userId = request.getUserId();
        
        // Only allow self-joining or admin adding others
        if (!requesterId.equals(userId) && !chatService.isUserAdmin(requesterId, chatRoomId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MessageResponse("Only admins can add other users"));
        }
        
        chatService.addUserToChatRoom(chatRoomId, userId);
        return ResponseEntity.ok(new MessageResponse("User added to the chat successfully"));
    }
    
    @DeleteMapping("/{chatRoomId}/members/{userId}")
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public ResponseEntity<MessageResponse> removeMemberFromChat(
            @PathVariable UUID chatRoomId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        UUID requesterId = UUID.fromString(userDetails.getId().toString());
        chatService.removeMemberFromChatRoom(chatRoomId, userId, requesterId);
        return ResponseEntity.ok(new MessageResponse("User removed from the chat successfully"));
    }
}