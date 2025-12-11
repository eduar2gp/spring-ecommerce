package com.ecommerce.backend.service;

import com.ecommerce.backend.dto.ProductRequestDTO;
import com.ecommerce.backend.model.Product;
import com.ecommerce.backend.model.Provider;
import com.ecommerce.backend.repository.ProductRepository;
import com.ecommerce.backend.repository.ProviderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private ProductRepository productRepository;

    public Product createProductFromDTO(ProductRequestDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setProductImageUrl(dto.getProductImageUrl());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());

        // 2. Load the actual relationship entity using the ID from the DTO
        Provider providerEntity = providerRepository.findById(dto.getProvider())
                .orElseThrow(() -> new EntityNotFoundException("Provider not found with ID: " + dto.getProvider()));

        // 3. Set the full entity on the Product object
        product.setProvider(providerEntity);
        // 4. Save the fully assembled Product entity
        return productRepository.save(product);
    }
}