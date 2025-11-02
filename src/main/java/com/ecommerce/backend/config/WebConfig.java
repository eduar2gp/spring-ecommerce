package com.ecommerce.backend.config;

import org.springframework.beans.factory.annotation.Value;
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

    // Inject the base upload directory defined in application.properties.
    // This value must match the file.upload-base-dir used in the FileStorageService.
    @Value("${file.upload-base-dir}")
    private String uploadBaseDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // The mapping URL (the public web path that clients request)
        registry.addResourceHandler("/images/**")
                // The actual physical location on the server's file system.
                // It uses "file:" protocol and appends the base directory.
                // The trailing slash (/) is crucial for correct resource loading.
                .addResourceLocations("file:" + uploadBaseDir + "/")
                // Set cache period for better performance
                .setCachePeriod(3600);
    }
}
