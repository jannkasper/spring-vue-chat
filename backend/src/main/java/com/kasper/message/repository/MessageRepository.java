package com.kasper.message.repository;

import com.kasper.chat.model.ChatRoom;
import com.kasper.message.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    List<Message> findByChatRoomOrderByCreatedAtAsc(ChatRoom chatRoom);
    
    Page<Message> findByChatRoomOrderByCreatedAtDesc(ChatRoom chatRoom, Pageable pageable);
    
    @Query(value = "SELECT * FROM messages WHERE chat_room_id = :chatRoomId AND " +
                  "search_vector @@ plainto_tsquery(:query) " +
                  "ORDER BY created_at DESC", 
           nativeQuery = true)
    Page<Message> searchMessages(@Param("chatRoomId") UUID chatRoomId, 
                                 @Param("query") String query, 
                                 Pageable pageable);
}