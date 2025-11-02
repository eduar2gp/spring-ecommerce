package com.ecommerce.backend.dto;

import jakarta.validation.constraints.NotBlank;

// Used to capture username and password from the POST /api/v1/auth/login request body
public class LoginRequestDTO {

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    // Standard getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
