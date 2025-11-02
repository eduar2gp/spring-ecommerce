package com.ecommerce.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service responsible for handling file system operations (saving, locating, managing directories).
 * This service is designed to be generic for various entity types (e.g., providers, products).
 */
@Service
public class FileStorageService {

    // Injects the base directory from application.properties (e.g., file.upload-base-dir=uploads)
    @Value("${file.upload-base-dir}")
    private String uploadBaseDir;

    private Path fileStorageLocation;

    /**
     * Initializes the service by resolving the base storage path and creating the directory if it doesn't exist.
     */
    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadBaseDir).toAbsolutePath().normalize();
        try {
            if (!Files.exists(this.fileStorageLocation)) {
                Files.createDirectories(this.fileStorageLocation);
                System.out.println("Created base upload directory: " + this.fileStorageLocation);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not create the base directory where uploaded files will be stored: " + uploadBaseDir, e);
        }
    }

    /**
     * Stores a file on the file system in an entity-specific subdirectory and returns the public URL path.
     *
     * @param file The file to store (MultipartFile).
     * @param entityType The subdirectory name (e.g., "providers", "products").
     * @param entityId The ID of the associated entity, used for unique file naming.
     * @return The public URL path used for database storage (e.g., /images/providers/filename.jpg).
     * @throws IOException if there is an error writing the file.
     */
    public String storeFile(MultipartFile file, String entityType, Long entityId) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Cannot store empty file.");
        }
        // 1. Ensure the entity-specific directory exists (e.g., uploads/providers)
        Path entityPath = fileStorageLocation.resolve(entityType).normalize();
        if (!Files.exists(entityPath)) {
            Files.createDirectories(entityPath);
        }
        // 2. Create a unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex > 0) {
            extension = originalFilename.substring(dotIndex);
        }
        // Example unique name: providers_123_uuid.jpg
        String uniqueFilename = entityType + "_" + entityId + "_" + UUID.randomUUID().toString() + extension;
        // 3. Resolve the final path for saving the file
        Path targetLocation = entityPath.resolve(uniqueFilename);
        // 4. Save the file to the file system
        Files.copy(file.getInputStream(), targetLocation);
        // 5. Return the public URL path (must match the path configured in WebConfig)
        // Assumes WebConfig maps /images/ to the fileStorageLocation
        return "/images/" + entityType + "/" + uniqueFilename;
    }
}
