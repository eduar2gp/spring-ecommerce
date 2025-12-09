package com.ecommerce.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Apply to all endpoints starting with /api
//                .allowedOrigins(
//                        "http://192.168.1.68:4200"
//                )
                .allowedOrigins(
                        "http://localhost:4200",
                        "http://127.0.0.1:4200",
                        "http://192.168.1.68:4200"
                ) // Allow your Angular/React development server
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Allow common HTTP methods
                .allowedHeaders("*") // Allow all headers
                .allowCredentials(true) // Important for session/cookie authentication
                .maxAge(3600); // How long the p
    }
}