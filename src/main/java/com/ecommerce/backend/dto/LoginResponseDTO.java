package com.ecommerce.backend.dto;

public class LoginResponseDTO {
    private String jwtToken;
    private Long userId;

    public LoginResponseDTO(String token, Long userId) {
        this.jwtToken = token;
        this.userId = userId;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}