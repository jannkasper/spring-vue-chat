package com.kasper.file;

import com.kasper.file.model.FileAttachment;
import com.kasper.file.repository.FileAttachmentRepository;
import com.kasper.user.model.User;
import com.kasper.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileAttachmentRepository fileAttachmentRepository;
    private final UserRepository userRepository;
    
    @Value("${app.file.upload-dir:./uploads}")
    private String uploadDir;
    
    public String uploadFile(MultipartFile file, UUID userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID() + fileExtension;
        
        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        
        // Store file metadata in database
        FileAttachment fileAttachment = FileAttachment.builder()
                .fileName(originalFilename)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .filePath(uniqueFilename)
                .uploadedBy(user)
                .build();
        
        fileAttachmentRepository.save(fileAttachment);
        
        // Return the file URL
        return "/api/files/" + fileAttachment.getId();
    }
    
    public byte[] getFileContent(UUID fileId) throws IOException {
        FileAttachment fileAttachment = fileAttachmentRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found with ID: " + fileId));
        
        Path filePath = Paths.get(uploadDir).resolve(fileAttachment.getFilePath());
        return Files.readAllBytes(filePath);
    }
    
    public String getFileContentType(UUID fileId) {
        FileAttachment fileAttachment = fileAttachmentRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found with ID: " + fileId));
        
        return fileAttachment.getFileType();
    }
    
    public String getFileName(UUID fileId) {
        FileAttachment fileAttachment = fileAttachmentRepository.findById(fileId)
                .orElseThrow(() -> new EntityNotFoundException("File not found with ID: " + fileId));
        
        return fileAttachment.getFileName();
    }
}