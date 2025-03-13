package com.kasper.user.repository;

import com.kasper.user.model.Branding;
import com.kasper.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandingRepository extends JpaRepository<Branding, UUID> {
    
    Optional<Branding> findByUser(User user);
    
    boolean existsByUser(User user);
}