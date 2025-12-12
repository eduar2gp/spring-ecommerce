package com.ecommerce.backend.dto;

import java.util.Set;

public class LoginResponseDTO {
    private String jwtToken;
    private Long userId;
    private Set<String> roles;
    private Long providerId;
    private String userName;

    public LoginResponseDTO(String jwtToken, String userName, Long userId, Long providerId, Set<String> roles) {
        this.jwtToken = jwtToken;
        this.userId = userId;
        this.roles = roles;
        this.providerId = providerId;
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Long getProviderId() {
        return providerId;
    }

    public void setProviderId(Long providerId) {
        this.providerId = providerId;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
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