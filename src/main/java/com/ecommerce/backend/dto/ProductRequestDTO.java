package com.ecommerce.backend.dto;


public class ProductRequestDTO {

    private String name;
    private String description;
    private String productImageUrl;
    private Integer price;
    private Long provider;
    private Integer stockQuantity;

    public ProductRequestDTO() {
    }

    public ProductRequestDTO(String name, String description, String productImageUrl, Integer price, Long provider, Integer stockQuantity) {
        this.name = name;
        this.description = description;
        this.productImageUrl = productImageUrl;
        this.price = price;
        this.provider = provider;
        this.stockQuantity = stockQuantity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }

    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long provider) {
        this.provider = provider;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
