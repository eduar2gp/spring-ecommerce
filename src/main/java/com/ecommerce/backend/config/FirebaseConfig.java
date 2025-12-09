package com.ecommerce.backend.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @Value("classpath:helloworld-a6508-firebase-adminsdk-fbsvc-b08ed5c9ce.json")
    private Resource serviceAccount;

    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount.getInputStream()))
                // You can optionally set the database URL if using the Realtime Database
                // .setDatabaseUrl("https://<YOUR-PROJECT-ID>.firebaseio.com")
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.initializeApp(options);
        }
        return FirebaseApp.getInstance();
    }
}