package com.ecommerce.backend.dto;

import com.ecommerce.backend.model.Provider;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) used for OUTGOING API responses.
 * This ensures that JPA proxy objects (like User/AppUser) are not exposed
 * to the client, resolving serialization errors caused by LAZY loading.
 */
public class ProviderResponseDTO {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String profileImageUrl;

    // Return only the ID of the associated User, not the full User object.
    private Long userId;

    // --- Constructors ---

    public ProviderResponseDTO() {
    }

    /**
     * Conversion constructor: maps a JPA Provider entity to this DTO.
     * @param provider The JPA entity to map from.
     */
    public ProviderResponseDTO(Provider provider) {
        this.id = provider.getId();
        this.name = provider.getName();
        this.email = provider.getEmail();
        this.phone = provider.getPhone();
        this.profileImageUrl = provider.getProfileImageUrl();

        // Safely extract the User ID. If appUser is null or a proxy is used,
        // we assume the ID is accessible, or use null if the User object is not present.
        if (provider.getAppUser() != null) {
            this.userId = provider.getAppUser().getId();
        } else {
            // This case should ideally not happen if 'user_id' is NOT NULL in the database
            this.userId = null;
        }
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
