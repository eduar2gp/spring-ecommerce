package com.ecommerce.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for creating or updating a Provider.
 * This DTO simplifies the API contract by accepting only the 'userId' (Long)
 * instead of the full User object, which is then mapped to the JPA entity in the service layer.
 */
public class ProviderRequestDTO {

    @NotBlank(message = "Provider name is required")
    @Size(max = 255, message = "Name must be less than 255 characters")
    private String name;

    @Email(message = "Email should be valid")
    @Size(max = 255, message = "Email must be less than 255 characters")
    private String email;

    @Size(max = 20, message = "Phone must be less than 20 characters")
    private String phone;

    // This field holds the path to the provider's profile image
    @Size(max = 512, message = "Image URL must be less than 512 characters")
    private String profileImageUrl;

    // CRITICAL: This accepts the ID of the User, decoupling the API from the JPA entity structure.
    @NotNull(message = "User ID (userId) is required for provider association")
    private Long userId;

    // --- Constructors ---

    public ProviderRequestDTO() {
    }

    public ProviderRequestDTO(String name, String email, String phone, String profileImageUrl, Long userId) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
        this.userId = userId;
    }

    // --- Getters and Setters ---

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
