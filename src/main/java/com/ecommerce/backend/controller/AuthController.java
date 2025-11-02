package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.LoginRequestDTO;
import com.ecommerce.backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    /**
     * POST /api/v1/auth/login : Authenticates user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<?> authenticateAndGetToken(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            // 1. Attempt to authenticate the user using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
            );

            if (authentication.isAuthenticated()) {
                // 2. If authenticated, generate the JWT token
                String token = jwtService.generateToken(loginRequestDTO.getUsername());

                // 3. Return the token in the response body
                // In a real app, you might use a dedicated LoginResponse DTO.
                return ResponseEntity.ok(Map.of("jwtToken", token));
            } else {
                // Should technically be caught by the exception, but as a fallback
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
            }
        } catch (AuthenticationException e) {
            // Catches exceptions like BadCredentialsException
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }
}

// NOTE: We use Map.of for simplicity, assuming you'll return a simple JSON object like:
// { "jwtToken": "..." }
