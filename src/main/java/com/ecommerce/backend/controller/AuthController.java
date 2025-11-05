package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.LoginRequestDTO;
import com.ecommerce.backend.dto.LoginResponseDTO;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthController(AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }

    /**
     * POST /api/v1/auth/login : Authenticates user and returns a JWT token.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> authenticateAndGetToken(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            // 1. Attempt to authenticate the user using the AuthenticationManager
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequestDTO.getUsername(), loginRequestDTO.getPassword())
            );
            if (authentication.isAuthenticated()) {
                // 2. If authenticated, generate the JWT token
                String token = jwtService.generateToken(loginRequestDTO.getUsername());
                // Get the username (security principal name)
                String username = authentication.getName();
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isEmpty()) {
                    // If the authenticated user is not found in the database, return 500.
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                Long userId = user.get().getId();
                // 4. Create the response DTO including the token and the actual database userId
                LoginResponseDTO responseDTO = new LoginResponseDTO(token, userId);
                // 5. Return the DTO with an HTTP 200 OK status
                return ResponseEntity.ok(responseDTO);
            } else {
                // Should technically be caught by the exception, but as a fallback
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (AuthenticationException e) {
            // Catches exceptions like BadCredentialsException
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}