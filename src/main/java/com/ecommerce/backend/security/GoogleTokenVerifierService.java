package com.ecommerce.backend.security;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class GoogleTokenVerifierService {

    private final GoogleIdTokenVerifier verifier;

    // Inject your Google Client ID from application.properties
    public GoogleTokenVerifierService(@Value("${google.client.id}") String clientId) {
        // Initializes the verifier with Google's public keys and your client ID
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), // HTTP transport layer
                new GsonFactory()       // JSON parsing factory
        )
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    /**
     * Verifies the Google JWT ID token.
     * @param idTokenString The raw JWT string received from the frontend.
     * @return The verified GoogleIdToken object, or null if verification fails.
     */
    public GoogleIdToken.Payload verify(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);

            if (idToken != null) {
                // Verification successful. Extract the payload claims.
                return idToken.getPayload();
            }
        } catch (Exception e) {
            // Log the exception for debugging (e.g., token expired, signature mismatch)
            System.err.println("Google ID Token verification failed: " + e.getMessage());
        }
        return null; // Token is invalid
    }
}