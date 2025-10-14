package com.ecommerce.backend.controller;

import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST Controller for managing Product entities.
 * Handles CRUD operations via RESTful endpoints.
 */
@RestController
@RequestMapping("/api/v1/products") // Base URL for all product endpoints
public class ProductController {

    private final ProductRepository productRepository;

    // Dependency injection via constructor (recommended practice)
    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
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
}
