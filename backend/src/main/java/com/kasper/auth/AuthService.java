package com.kasper.auth;

import com.kasper.auth.dto.JwtResponse;
import com.kasper.auth.dto.LoginRequest;
import com.kasper.auth.dto.RefreshTokenRequest;
import com.kasper.auth.dto.SignupRequest;
import com.kasper.auth.model.RefreshToken;
import com.kasper.auth.repository.RefreshTokenRepository;
import com.kasper.common.dto.MessageResponse;
import com.kasper.common.security.UserDetailsImpl;
import com.kasper.common.security.jwt.JwtUtils;
import com.kasper.user.model.User;
import com.kasper.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;
    
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtUtils.generateAccessToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        // Save refresh token
        saveRefreshToken(refreshToken, userDetails.getId());

        return new JwtResponse(accessToken, refreshToken, userDetails.getId(), userDetails.getUsername(), userDetails.getEmail());
    }
    
    @Transactional
    public JwtResponse refreshToken(RefreshTokenRequest request) {
        String refreshTokenStr = request.getRefreshToken();
        
        // Validate refresh token
        if (!jwtUtils.validateJwtToken(refreshTokenStr)) {
            throw new RuntimeException("Invalid refresh token");
        }

        // Check if token exists in database and is not revoked
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenStr)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));

        if (refreshToken.isRevoked()) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RuntimeException("Refresh token has expired");
        }

        // Get user details
        String username = jwtUtils.getUserNameFromJwtToken(refreshTokenStr);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        // Create new tokens
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                UserDetailsImpl.build(user),
                null,
                null
        );

        String newAccessToken = jwtUtils.generateAccessToken(authentication);
        String newRefreshToken = jwtUtils.generateRefreshToken(authentication);

        // Revoke old refresh token
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);

        // Save new refresh token
        saveRefreshToken(newRefreshToken, user.getId());

        return new JwtResponse(newAccessToken, newRefreshToken, user.getId(), user.getUsername(), user.getEmail());
    }
    
    @Transactional
    public void logout(UUID userId) {
        refreshTokenRepository.revokeAllUserTokens(userId);
    }
    
    private void saveRefreshToken(String token, UUID userId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .userId(userId)
                .expiryDate(Instant.now().plusMillis(jwtUtils.getJwtRefreshExpirationMs()))
                .revoked(false)
                .createdAt(Instant.now())
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    public MessageResponse registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return new MessageResponse("Error: Username is already taken!");
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new MessageResponse("Error: Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .username(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(encoder.encode(signUpRequest.getPassword()))
                .firstName(signUpRequest.getFirstName())
                .lastName(signUpRequest.getLastName())
                .isActive(true)
                .build();

        userRepository.save(user);

        return new MessageResponse("User registered successfully!");
    }
    
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userDetails.getId()));
    }
}