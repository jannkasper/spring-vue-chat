package com.kasper.message;

import com.kasper.chat.ChatService;
import com.kasper.chat.model.ChatRoom;
import com.kasper.common.dto.MessageResponse;
import com.kasper.common.security.EncryptionService;
import com.kasper.message.dto.MessageRequest;
import com.kasper.message.dto.WebSocketMessage;
import com.kasper.message.model.Message;
import com.kasper.chat.repository.ChatRoomRepository;
import com.kasper.message.repository.MessageRepository;
import com.kasper.user.model.User;
import com.kasper.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final ChatService chatService;
    private final EncryptionService encryptionService;
    private final SimpMessagingTemplate messagingTemplate;
    
    @Transactional
    public MessageResponse saveMessage(UUID chatRoomId, MessageRequest messageRequest) {
        // Validate chat room and sender
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        User sender = userRepository.findById(messageRequest.getSenderId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + messageRequest.getSenderId()));
        
        // Check if user is a member of the chat room
        if (!chatService.isUserInChatRoom(sender.getId(), chatRoomId)) {
            throw new IllegalStateException("User is not a member of this chat room");
        }
        
        // Process message content (encrypt if needed)
        String processedContent = messageRequest.getMessage();
        if (messageRequest.isEncrypted()) {
            processedContent = encryptionService.encrypt(processedContent);
        }
        
        // Create and save the message
        Message message = Message.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(processedContent)
                .fileUrl(messageRequest.getFileUrl())
                .isEncrypted(messageRequest.isEncrypted())
                .build();
        
        message = messageRepository.save(message);
        
        // Create response
        MessageResponse response = mapToMessageResponse(message);
        
        // Send WebSocket message to subscribers
        sendWebSocketMessage(chatRoomId, sender, messageRequest.getMessage(), messageRequest.getFileUrl(), message.getCreatedAt());
        
        return response;
    }
    
    public List<MessageResponse> getChatMessages(UUID chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        List<Message> messages = messageRepository.findByChatRoomOrderByCreatedAtAsc(chatRoom);
        
        return messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
    }
    
    public Page<MessageResponse> getChatMessagesPaginated(UUID chatRoomId, Pageable pageable) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        Page<Message> messages = messageRepository.findByChatRoomOrderByCreatedAtDesc(chatRoom, pageable);
        
        return messages.map(this::mapToMessageResponse);
    }
    
    public Page<MessageResponse> searchMessages(UUID chatRoomId, String query, Pageable pageable) {
        Page<Message> messages = messageRepository.searchMessages(chatRoomId, query, pageable);
        return messages.map(this::mapToMessageResponse);
    }
    
    private MessageResponse mapToMessageResponse(Message message) {
        String content = message.getContent();
        
        // Decrypt if needed
        if (message.isEncrypted()) {
            content = encryptionService.decrypt(content);
        }
        
        return MessageResponse.builder()
                .id(message.getId())
                .chatRoomId(message.getChatRoom().getId())
                .sender(MessageResponse.UserInfo.builder()
                        .id(message.getSender().getId())
                        .username(message.getSender().getUsername())
                        .build())
                .message(content)
                .fileUrl(message.getFileUrl())
                .createdAt(message.getCreatedAt())
                .encrypted(message.isEncrypted())
                .build();
    }
    
    private void sendWebSocketMessage(UUID chatRoomId, User sender, String message, String fileUrl, LocalDateTime timestamp) {
        WebSocketMessage webSocketMessage = WebSocketMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(sender.getId())
                .senderUsername(sender.getUsername())
                .message(message)
                .fileUrl(fileUrl)
                .createdAt(timestamp)
                .type(WebSocketMessage.MessageType.CHAT)
                .build();
        
        messagingTemplate.convertAndSend("/topic/chat/" + chatRoomId, webSocketMessage);
    }
}