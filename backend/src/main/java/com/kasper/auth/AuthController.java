package com.kasper.auth;

import com.kasper.auth.dto.JwtResponse;
import com.kasper.auth.dto.LoginRequest;
import com.kasper.auth.dto.RefreshTokenRequest;
import com.kasper.auth.dto.SignupRequest;
import com.kasper.common.dto.MessageResponse;
import com.kasper.common.security.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        MessageResponse messageResponse = authService.registerUser(signUpRequest);
        
        if (messageResponse.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(messageResponse);
        }
        
        return ResponseEntity.ok(messageResponse);
    }
    
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        try {
            JwtResponse jwtResponse = authService.refreshToken(request);
            return ResponseEntity.ok(jwtResponse);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        authService.logout(userDetails.getId());
        return ResponseEntity.ok(new MessageResponse("Logged out successfully"));
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}