package com.ecommerce.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Get the Authorization header
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        // 2. Check if header is present and starts with "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Extract the token after "Bearer "
            try {
                username = jwtService.extractUsername(token);
            } catch (Exception e) {
                // Log exception if token is invalid or expired
                logger.warn("JWT validation failed: " + e.getMessage());
            }
        }

        // 3. If username is found and no authentication is currently set
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user details from the store
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. Validate the token against the loaded user details
            if (jwtService.validateToken(token, userDetails)) {
                // If valid, create an Authentication object and set it in the SecurityContext
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 5. Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
