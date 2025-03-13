package com.kasper.chat.repository;

import com.kasper.chat.model.ChatRoom;
import com.kasper.chat.model.ChatRoomMember;
import com.kasper.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, UUID> {
    
    List<ChatRoomMember> findByChatRoom(ChatRoom chatRoom);
    
    List<ChatRoomMember> findByUser(User user);
    
    Optional<ChatRoomMember> findByChatRoomAndUser(ChatRoom chatRoom, User user);
    
    boolean existsByChatRoomAndUser(ChatRoom chatRoom, User user);
    
    List<ChatRoomMember> findByChatRoomAndIsAdminTrue(ChatRoom chatRoom);
    
    void deleteByChatRoomAndUser(ChatRoom chatRoom, User user);
}