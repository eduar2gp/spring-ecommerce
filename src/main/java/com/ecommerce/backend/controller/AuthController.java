package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.IdTokenRequestDTO;
import com.ecommerce.backend.dto.LoginRequestDTO;
import com.ecommerce.backend.dto.LoginResponseDTO;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.security.GoogleTokenVerifierService;
import com.ecommerce.backend.security.JwtService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Optional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    private final GoogleTokenVerifierService tokenVerifierService;

    private final PasswordEncoder passwordEncoder;

    public AuthController(GoogleTokenVerifierService tokenVerifierService, AuthenticationManager authenticationManager, JwtService jwtService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.tokenVerifierService = tokenVerifierService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
                // 2. Fetch all authorities (roles) granted to the authenticated user
                Set<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority) // Extracts the role string (e.g., "ROLE_ADMIN")
                        .collect(Collectors.toSet());
                // 3. Generate the JWT token
                String token = jwtService.generateToken(loginRequestDTO.getUsername());
                // Get the username (security principal name)
                String username = authentication.getName();
                // 4. Fetch the User entity to get the database ID
                Optional<User> user = userRepository.findByUsername(username);
                if (user.isEmpty()) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
                }
                Long userId = user.get().getId();
                Long providerId = user.get().getProviderId();
                String userName = user.get().getUsername();
                LoginResponseDTO responseDTO = new LoginResponseDTO(token, userName, userId, providerId, roles);
                return ResponseEntity.ok(responseDTO);
            } else {
                // Should be highly unlikely as authenticationManager.authenticate throws on failure
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (AuthenticationException e) {
            // Catches exceptions like BadCredentialsException if authentication fails
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/google/login")
    public ResponseEntity<?> googleLogin(@RequestBody IdTokenRequestDTO request) {

        GoogleIdToken.Payload payload = tokenVerifierService.verify(request.getIdToken());

        if (payload == null) {
            return ResponseEntity.status(401).body("Invalid Google ID Token");
        }

        // 1. Get user details from the payload
        String email = payload.getEmail();
//        String googleSub = payload.getSubject(); // Google's unique ID (sub claim)
//        String name = (String) payload.get("name"); // Display name

        // Check if a user with this email already exists
        Optional<User> existingUser = userRepository.findByUsername(email);
        Long userId;
        Long providerId = 0L;
        String userName;

        if (existingUser.isEmpty()) {
            // 2. User does not exist, provision a new user account
            User newUser = new User();
            newUser.setUsername(email);
            userName = email;
            // Use a secure placeholder password since the user logs in via Google
            newUser.setPassword(passwordEncoder.encode("GOOGLE_OAUTH2_USER"));
//            newUser.setFirstName(name);
//            newUser.setGoogleId(googleSub); // Store the unique Google ID
//            newUser.setRole("USER"); // Assign a default role

            User savedUser = userRepository.save(newUser);
            userId = savedUser.getId();
        }
        else {
            userId = existingUser.get().getId();
            providerId = existingUser.get().getProviderId();
            userName = existingUser.get().getUsername();
        }

        // 3. Generate your application's JWT for the successfully authenticated user
        String token = jwtService.generateToken(email);

        // 4. Return the application JWT and Google Sub (user identifier)
        LoginResponseDTO responseDTO = new LoginResponseDTO(token, userName, userId,  providerId, null);
        return ResponseEntity.ok(responseDTO);
    }
}