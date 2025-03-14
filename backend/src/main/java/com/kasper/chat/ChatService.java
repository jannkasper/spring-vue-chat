package com.kasper.chat;

import com.kasper.chat.dto.ChatRoomRequest;
import com.kasper.chat.dto.ChatRoomResponse;
import com.kasper.chat.model.ChatRoom;
import com.kasper.chat.model.ChatRoomMember;
import com.kasper.chat.repository.ChatRoomMemberRepository;
import com.kasper.chat.repository.ChatRoomRepository;
import com.kasper.user.model.User;
import com.kasper.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {
    
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomMemberRepository chatRoomMemberRepository;
    private final UserRepository userRepository;
    
    @Transactional
    public ChatRoomResponse createChatRoom(ChatRoomRequest request, UUID creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + creatorId));
        
        // Create the chat room
        ChatRoom chatRoom = ChatRoom.builder()
                .name(request.getName())
                .isPrivate(request.isPrivate())
                .createdBy(creator)
                .build();
        
        chatRoom = chatRoomRepository.save(chatRoom);
        
        // Add creator as admin
        ChatRoomMember membership = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(creator)
                .isAdmin(true)
                .build();
        
        chatRoomMemberRepository.save(membership);
        
        return mapToChatRoomResponse(chatRoom, 1);
    }
    
    @Transactional
    public ChatRoomResponse addUserToChatRoom(UUID chatRoomId, UUID userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        // Check if user is already a member
        if (chatRoomMemberRepository.existsByChatRoomAndUser(chatRoom, user)) {
            throw new IllegalStateException("User is already a member of this chat room");
        }
        
        // Add user as a member
        ChatRoomMember membership = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .isAdmin(false)
                .build();
        
        chatRoomMemberRepository.save(membership);
        
        int memberCount = chatRoomMemberRepository.findByChatRoom(chatRoom).size();
        return mapToChatRoomResponse(chatRoom, memberCount);
    }
    
    public ChatRoomResponse getChatRoomById(UUID chatRoomId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        int memberCount = chatRoomMemberRepository.findByChatRoom(chatRoom).size();
        return mapToChatRoomResponse(chatRoom, memberCount);
    }
    
    public List<ChatRoomResponse> getChatRoomsByUserId(UUID userId) {
        List<ChatRoom> chatRooms = chatRoomRepository.findAllChatRoomsByUserId(userId);
        
        return chatRooms.stream()
                .map(chatRoom -> {
                    int memberCount = chatRoomMemberRepository.findByChatRoom(chatRoom).size();
                    return mapToChatRoomResponse(chatRoom, memberCount);
                })
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void removeMemberFromChatRoom(UUID chatRoomId, UUID userId, UUID requesterId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        // Check if requester is admin or the user themself
        boolean isAdmin = chatRoomRepository.isUserAdminOfChatRoom(requesterId, chatRoomId);
        boolean isSelfRemoval = userId.equals(requesterId);
        
        if (!isAdmin && !isSelfRemoval) {
            throw new IllegalStateException("Only admins can remove other members");
        }
        
        chatRoomMemberRepository.deleteByChatRoomAndUser(chatRoom, user);
    }
    
    public boolean isUserInChatRoom(UUID userId, UUID chatRoomId) {
        return chatRoomRepository.isUserInChatRoom(userId, chatRoomId);
    }
    
    public boolean isUserAdmin(UUID userId, UUID chatRoomId) {
        return chatRoomRepository.isUserAdminOfChatRoom(userId, chatRoomId);
    }
    
    /**
     * Get a paginated list of public chat rooms
     */
    public Page<ChatRoomResponse> getPublicChatRooms(Pageable pageable) {
        Page<ChatRoom> publicChats = chatRoomRepository.findByIsPrivateFalseOrderByCreatedAtDesc(pageable);
        
        return publicChats.map(chatRoom -> {
            int memberCount = chatRoomMemberRepository.findByChatRoom(chatRoom).size();
            return mapToChatRoomResponse(chatRoom, memberCount);
        });
    }
    
    /**
     * Join a public chat room automatically
     * 
     * @param chatRoomId ID of the chat room to join
     * @param userId ID of the user joining the chat
     * @return ChatRoomResponse with chat room details
     * @throws IllegalStateException if the chat room is private
     */
    @Transactional
    public ChatRoomResponse joinPublicChatRoom(UUID chatRoomId, UUID userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("Chat room not found with ID: " + chatRoomId));
        
        // Check if the chat room is public
        if (chatRoom.isPrivate()) {
            throw new IllegalStateException("Cannot automatically join a private chat room");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        // Check if user is already a member
        if (chatRoomMemberRepository.existsByChatRoomAndUser(chatRoom, user)) {
            // User is already a member, just return the chat room
            int memberCount = chatRoomMemberRepository.findByChatRoom(chatRoom).size();
            return mapToChatRoomResponse(chatRoom, memberCount);
        }
        
        // Add user as a regular member
        ChatRoomMember membership = ChatRoomMember.builder()
                .chatRoom(chatRoom)
                .user(user)
                .isAdmin(false)
                .build();
        
        chatRoomMemberRepository.save(membership);
        
        int memberCount = chatRoomMemberRepository.findByChatRoom(chatRoom).size();
        return mapToChatRoomResponse(chatRoom, memberCount);
    }
    
    private ChatRoomResponse mapToChatRoomResponse(ChatRoom chatRoom, int memberCount) {
        return ChatRoomResponse.builder()
                .id(chatRoom.getId())
                .name(chatRoom.getName())
                .isPrivate(chatRoom.isPrivate())
                .createdBy(ChatRoomResponse.UserSummary.builder()
                        .id(chatRoom.getCreatedBy().getId())
                        .username(chatRoom.getCreatedBy().getUsername())
                        .build())
                .createdAt(chatRoom.getCreatedAt())
                .updatedAt(chatRoom.getUpdatedAt())
                .memberCount(memberCount)
                .build();
    }
}