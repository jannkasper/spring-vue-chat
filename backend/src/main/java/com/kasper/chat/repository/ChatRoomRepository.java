package com.kasper.chat.repository;

import com.kasper.chat.model.ChatRoom;
import com.kasper.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, UUID> {
    
    List<ChatRoom> findByCreatedBy(User user);
    
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.user.id = :userId")
    List<ChatRoom> findAllChatRoomsByUserId(@Param("userId") UUID userId);
    
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM ChatRoom cr JOIN cr.members m " +
           "WHERE cr.id = :chatRoomId AND m.user.id = :userId")
    boolean isUserInChatRoom(@Param("userId") UUID userId, @Param("chatRoomId") UUID chatRoomId);
    
    @Query("SELECT CASE WHEN COUNT(cr) > 0 THEN true ELSE false END FROM ChatRoom cr JOIN cr.members m " +
           "WHERE cr.id = :chatRoomId AND m.user.id = :userId AND m.isAdmin = true")
    boolean isUserAdminOfChatRoom(@Param("userId") UUID userId, @Param("chatRoomId") UUID chatRoomId);
    
    // Find all public chat rooms (isPrivate = false)
    Page<ChatRoom> findByIsPrivateFalseOrderByCreatedAtDesc(Pageable pageable);
}