package com.ecommerce.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Service class responsible for creating, validating, and parsing JSON Web Tokens (JWT).
 * It uses the secret key defined in application.properties for security.
 */
@Service
public class JwtService {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.ms:3600000}") // Default to 1 hour (3,600,000 ms) if not configured
    private long jwtExpirationMs;

    // --- Token Generation ---

    /**
     * Generates a JWT for a given username.
     * @param userName The username to embed as the subject.
     * @return The generated JWT string.
     */
    public String generateToken(String userName) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userName);
    }

    private String createToken(Map<String, Object> claims, String userName) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userName)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                // Set the expiration time based on the value loaded from configuration
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // --- Token Validation and Parsing ---

    /**
     * Validates if a token is valid for a given UserDetails object.
     * @param token The JWT to validate.
     * @param userDetails The UserDetails to compare the username against.
     * @return true if the token is valid and not expired, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUsername(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // --- Claim Extraction ---

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // --- Key Management ---

    private Key getSigningKey() {
        // Decode the Base64 secret key string into bytes
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        // Create the HMAC-SHA key
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
