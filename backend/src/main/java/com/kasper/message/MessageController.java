package com.kasper.message;

import com.kasper.common.dto.MessageResponse;
import com.kasper.message.dto.MessageRequest;
import com.kasper.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chats/{chatRoomId}/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    
    @PostMapping
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public ResponseEntity<MessageResponse> createMessage(
            @PathVariable UUID chatRoomId,
            @Valid @RequestBody MessageRequest request,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        // Ensure the sender ID in the request matches the authenticated user
        UUID userId = UUID.fromString(userDetails.getId().toString());
        if (!userId.equals(request.getSenderId())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Sender ID doesn't match authenticated user"));
        }
        
        MessageResponse response = messageService.saveMessage(chatRoomId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public ResponseEntity<List<MessageResponse>> getChatMessages(
            @PathVariable UUID chatRoomId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        List<MessageResponse> messages = messageService.getChatMessages(chatRoomId);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/paginated")
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public ResponseEntity<Page<MessageResponse>> getChatMessagesPaginated(
            @PathVariable UUID chatRoomId,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Page<MessageResponse> messages = messageService.getChatMessagesPaginated(chatRoomId, pageable);
        return ResponseEntity.ok(messages);
    }
    
    @GetMapping("/search")
    @PreAuthorize("@chatService.isUserInChatRoom(#userDetails.id, #chatRoomId)")
    public ResponseEntity<Page<MessageResponse>> searchMessages(
            @PathVariable UUID chatRoomId,
            @RequestParam String query,
            @PageableDefault(size = 20) Pageable pageable,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        
        Page<MessageResponse> messages = messageService.searchMessages(chatRoomId, query, pageable);
        return ResponseEntity.ok(messages);
    }
}