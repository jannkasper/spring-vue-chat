package com.kasper.auth;

import com.kasper.auth.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Component
public class RefreshTokenCleanupTask {

    private static final Logger logger = LoggerFactory.getLogger(RefreshTokenCleanupTask.class);

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    /**
     * Scheduled task to clean up expired refresh tokens
     * Runs every day at midnight
     */
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        logger.info("Starting cleanup of expired refresh tokens");
        refreshTokenRepository.deleteExpiredTokens(Instant.now());
        logger.info("Completed cleanup of expired refresh tokens");
    }
} 