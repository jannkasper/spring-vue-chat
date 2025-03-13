package com.kasper.file.repository;

import com.kasper.file.model.FileAttachment;
import com.kasper.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, UUID> {
    
    List<FileAttachment> findByUploadedBy(User user);
    
    List<FileAttachment> findByFileNameContainingIgnoreCase(String fileName);
}