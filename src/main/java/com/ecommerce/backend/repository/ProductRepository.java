package com.ecommerce.backend.repository;

import com.ecommerce.backend.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for Product entity. Spring Data JPA automatically provides
 * implementations for standard CRUD methods (findAll, findById, save, delete, etc.).
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Custom query method example: find products by name (Spring automatically infers SQL)
    Product findByName(String name);
    List<Product> findByProviderId(Long providerId);
}
