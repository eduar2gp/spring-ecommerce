package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.service.FileStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * REST Controller for managing Product entities.
 * Handles CRUD operations via RESTful endpoints.
 */
@RestController
@RequestMapping("/api/v1/products") // Base URL for all product endpoints
public class ProductController {

    private final ProductRepository productRepository;
    private final FileStorageService fileStorageService;

    // Dependency injection via constructor (recommended practice)
    public ProductController(ProductRepository productRepository, FileStorageService fileStorageService) {
        this.productRepository = productRepository;
        this.fileStorageService = fileStorageService;
    }

    /**
     * GET /api/v1/products : Retrieve all products
     */
    @GetMapping
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * GET /api/v1/products/{id} : Retrieve a single product by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok) // Return 200 OK with product body
                .orElseGet(() -> ResponseEntity.notFound().build()); // Return 404 Not Found
    }

    /**
     * POST /api/v1/products : Create a new product
     * @Valid triggers the validation constraints defined in the Product model.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Return 201 Created on success
    public Product createProduct(@Valid @RequestBody Product product) {
        return productRepository.save(product);
    }

    /**
     * PUT /api/v1/products/{id} : Update an existing product
     */
    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        return productRepository.findById(id)
                .map(existingProduct -> {
                    // Update fields of the existing product
                    existingProduct.setName(productDetails.getName());
                    existingProduct.setDescription(productDetails.getDescription());
                    existingProduct.setPrice(productDetails.getPrice());
                    existingProduct.setStockQuantity(productDetails.getStockQuantity());

                    // Save the updated entity
                    Product updatedProduct = productRepository.save(existingProduct);
                    return ResponseEntity.ok(updatedProduct);
                })
                .orElseGet(() -> ResponseEntity.notFound().build()); // Return 404 if not found
    }

    /**
     * DELETE /api/v1/products/{id} : Delete a product
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        if (productRepository.existsById(id)) {
            productRepository.deleteById(id);
            return ResponseEntity.noContent().build(); // Return 204 No Content on successful deletion
        } else {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Product> updateProductImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        // 1. Check if the Provider exists
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            // Return 404 Not Found (no body needed for DTO or entity)
            return ResponseEntity.notFound().build();
        }
        // 2. Handle the file storage logic
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        try {
            // DELEGATE: Call the service to save the file and get the public path
            String publicUrlPath = fileStorageService.storeFile(file, "product", id);
            // Update the database record
            product.setProductImageUrl(publicUrlPath);
            Product updatedProduct = productRepository.save(product);
            // 4. Return 200 OK with the updated DTO (MAPPING)
           return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
