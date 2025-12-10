package com.ecommerce.backend.mapper;

import com.ecommerce.backend.dto.ProductResponseDTO;
import com.ecommerce.backend.model.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    public ProductResponseDTO toDto(Product product) {
        ProductResponseDTO dto = new ProductResponseDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setProductImageUrl(product.getProductImageUrl());
        return dto;
    }
}