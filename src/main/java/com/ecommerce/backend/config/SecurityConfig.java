package com.ecommerce.backend.config;

import com.ecommerce.backend.security.CustomUserDetailsService;
import com.ecommerce.backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main Spring Security configuration class.
 * Configured for stateless JWT authentication and uses CustomUserDetailsService
 * to load user data and roles from PostgreSQL.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    // Inject the service that loads users from PostgreSQL
    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter, CustomUserDetailsService customUserDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.customUserDetailsService = customUserDetailsService;
    }

    // --- 1. Password Encoder Configuration (Used for hashing and verification) ---
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // --- 2. Data Access Provider (Explicitly links UserDetailsService and PasswordEncoder) ---
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        // Set the custom service that fetches user data from the DB
        authProvider.setUserDetailsService(customUserDetailsService);
        // Set the encoder used to verify the raw password against the stored hash
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // --- 3. Authentication Manager (Manages the overall authentication process) ---
    // The AuthenticationManager uses the DaoAuthenticationProvider (defined above)
    // which in turn uses the CustomUserDetailsService (PostgreSQL source).
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // --- 4. HTTP Security Rules (Stateless & JWT Integration) ---
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                       // 1. Explicitly permit OPTIONS requests globally for CORS preflight checks
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // Allow POST to the custom login endpoint to get the token
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/images/**").permitAll()

                        // --- Product Controller Authorization Rules ---
                        // POST/PUT/DELETE require ADMIN role
//                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/providers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN")

                        // GET requests require any authenticated user
                        .requestMatchers(HttpMethod.GET, "/api/v1/products/**").authenticated()

                        // Fallback: All other requests must be authenticated
                        .anyRequest().authenticated()
                )
                // Set session management to stateless (Crucial for JWT)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // Add the JWT filter to intercept requests
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                // Register the custom authentication provider
                .authenticationProvider(authenticationProvider());

        return http.build();
    }
}
