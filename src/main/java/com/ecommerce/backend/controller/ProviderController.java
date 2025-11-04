package com.ecommerce.backend.controller;

import com.ecommerce.backend.dto.ProviderRequestDTO;
import com.ecommerce.backend.dto.ProviderResponseDTO;
import com.ecommerce.backend.model.Provider;
import com.ecommerce.backend.model.User;
import com.ecommerce.backend.repository.ProviderRepository;
import com.ecommerce.backend.repository.UserRepository;
import com.ecommerce.backend.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final ProviderRepository providerRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public ProviderController(ProviderRepository providerRepository, UserRepository userRepository, FileStorageService fileStorageService) {
        this.providerRepository = providerRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * Creates a new Provider using the simplified request DTO.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProviderResponseDTO createProvider(@Valid @RequestBody ProviderRequestDTO providerDto) {
        // 1. Fetch the required User entity using the ID from the DTO
        User user = userRepository.findById(providerDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + providerDto.getUserId()));

        // 2. Map the DTO to the JPA Entity, setting the retrieved User object
        Provider provider = new Provider();
        provider.setName(providerDto.getName());
        provider.setEmail(providerDto.getEmail());
        provider.setAppUser(user); // Here we set the actual User object
        provider.setPhone(providerDto.getPhone());

        Provider savedProvider = providerRepository.save(provider);
        // Map the saved entity to the response DTO
        return new ProviderResponseDTO(savedProvider);
    }

    /**
     * Retrieves all Providers.
     * Mapped to: GET /api/v1/providers
     * Returns DTO list to prevent lazy-loading serialization issues.
     */
    @GetMapping
    public List<ProviderResponseDTO> getAllProviders() {
        // Fetch all Provider entities and map each one to a ProviderResponseDTO
        return providerRepository.findAll().stream()
                .map(ProviderResponseDTO::new)
                .toList();
    }

    /**
     * Retrieves a single Provider by ID.
     * Mapped to: GET /api/v1/providers/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> getProviderById(@PathVariable Long id) {
        Optional<Provider> providerOptional = providerRepository.findById(id);

        return providerOptional.map(provider -> {
            // Map the entity to the DTO and return 200 OK
            ProviderResponseDTO responseDTO = new ProviderResponseDTO(provider);
            return ResponseEntity.ok(responseDTO);
        }).orElseGet(() -> {
            // Return 404 Not Found if the provider does not exist
            return ResponseEntity.notFound().build();
        });
    }

    /**
     * Updates an existing Provider.
     * Mapped to: PUT /api/v1/providers/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProviderResponseDTO> updateProvider(@PathVariable Long id, @Valid @RequestBody ProviderRequestDTO providerDto) {
        return providerRepository.findById(id)
                .map(existingProvider -> {
                    // Update only the mutable fields from the DTO
                    existingProvider.setName(providerDto.getName());
                    existingProvider.setEmail(providerDto.getEmail());
                    existingProvider.setPhone(providerDto.getPhone());
                    // Note: We typically don't allow changing the 'User' relationship (appUser) in a PUT request.

                    // If profileImageUrl is provided in the DTO, update it (though usually handled by POST /image)
                    if (providerDto.getProfileImageUrl() != null) {
                        existingProvider.setProfileImageUrl(providerDto.getProfileImageUrl());
                    }

                    Provider updatedProvider = providerRepository.save(existingProvider);
                    ProviderResponseDTO responseDTO = new ProviderResponseDTO(updatedProvider);
                    return ResponseEntity.ok(responseDTO);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a Provider by ID.
     * Mapped to: DELETE /api/v1/providers/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Long id) {
        if (providerRepository.existsById(id)) {
            providerRepository.deleteById(id);
            // Return 204 No Content upon successful deletion
            return ResponseEntity.noContent().build();
        } else {
            // Return 404 Not Found if the provider does not exist
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<ProviderResponseDTO> updateProviderImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // 1. Check if the Provider exists
        Provider provider = providerRepository.findById(id)
                .orElse(null);
        if (provider == null) {
            // Return 404 Not Found (no body needed for DTO or entity)
            return ResponseEntity.notFound().build();
        }
        // 2. Handle the file storage logic
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            // DELEGATE: Call the service to save the file and get the public path
            String publicUrlPath = fileStorageService.storeFile(file, "provider", id);
            // Update the database record
            provider.setProfileImageUrl(publicUrlPath);
            Provider updatedProvider = providerRepository.save(provider);
            // 4. Return 200 OK with the updated DTO (MAPPING)
            ProviderResponseDTO responseDTO = new ProviderResponseDTO(updatedProvider);
            return ResponseEntity.ok(responseDTO);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}