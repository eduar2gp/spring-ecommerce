package com.ecommerce.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class to set up a Resource Handler.
 * This maps the public URL prefix used in the ProviderController
 * to the actual directory on the file system where images are stored.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Define the base path for uploaded files, matching the UPLOAD_DIR
    // used in ProviderController, but resolving to an absolute file path.
    // This assumes files are stored in a directory named 'uploads'
    // within the application's root directory.
    private static final String FILE_SYSTEM_ROOT =
            "file:///" + System.getProperty("user.dir") + "/uploads/";

    /**
     * Maps the public web path /images/** to the file system directory /uploads/.
     * This allows clients to request an image using the URL stored in the database.
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Resource Handler: /images/provider_images/provider_1_uuid.jpg
        // maps to: file:///path/to/app/uploads/provider_images/provider_1_uuid.jpg
        registry.addResourceHandler("/images/**")
                .addResourceLocations(FILE_SYSTEM_ROOT)
                .setCachePeriod(3600); // Cache resources for 1 hour
    }
}
