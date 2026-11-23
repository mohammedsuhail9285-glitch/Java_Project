package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_seller_id", columnList = "seller_id"),
        @Index(name = "idx_products_brand_id", columnList = "brand_id"),
        @Index(name = "idx_products_category_id", columnList = "category_id")
    }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "brand_id")
    private Long brandId;

    @Column(name = "seller_id")
    private Long sellerId;

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "declared_mrp")
    private Double declaredMrp;

    @Column(name = "listing_price")
    private Double listingPrice;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "country_of_origin", length = 64)
    private String countryOfOrigin;

    @Column(name = "product_url", length = 512)
    private String productUrl;

    @Column(name = "ingestion_source", length = 64)
    private String ingestionSource = "MANUAL";

    @Column(length = 32)
    private String status = "PENDING_SCAN";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Product() {}

    public Product(Long id, String name, Long brandId, Long sellerId, Long categoryId, Double declaredMrp, Double listingPrice, String description, String countryOfOrigin, String productUrl, String ingestionSource, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.brandId = brandId;
        this.sellerId = sellerId;
        this.categoryId = categoryId;
        this.declaredMrp = declaredMrp;
        this.listingPrice = listingPrice;
        this.description = description;
        this.countryOfOrigin = countryOfOrigin;
        this.productUrl = productUrl;
        this.ingestionSource = ingestionSource != null ? ingestionSource : "MANUAL";
        this.status = status != null ? status : "PENDING_SCAN";
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public Double getDeclaredMrp() { return declaredMrp; }
    public void setDeclaredMrp(Double declaredMrp) { this.declaredMrp = declaredMrp; }

    public Double getListingPrice() { return listingPrice; }
    public void setListingPrice(Double listingPrice) { this.listingPrice = listingPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getIngestionSource() { return ingestionSource; }
    public void setIngestionSource(String ingestionSource) { this.ingestionSource = ingestionSource; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private Long brandId;
        private Long sellerId;
        private Long categoryId;
        private Double declaredMrp;
        private Double listingPrice;
        private String description;
        private String countryOfOrigin;
        private String productUrl;
        private String ingestionSource = "MANUAL";
        private String status = "PENDING_SCAN";
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder brandId(Long brandId) { this.brandId = brandId; return this; }
        public Builder sellerId(Long sellerId) { this.sellerId = sellerId; return this; }
        public Builder categoryId(Long categoryId) { this.categoryId = categoryId; return this; }
        public Builder declaredMrp(Double declaredMrp) { this.declaredMrp = declaredMrp; return this; }
        public Builder listingPrice(Double listingPrice) { this.listingPrice = listingPrice; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder countryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; return this; }
        public Builder productUrl(String productUrl) { this.productUrl = productUrl; return this; }
        public Builder ingestionSource(String ingestionSource) { this.ingestionSource = ingestionSource; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Product build() {
            return new Product(id, name, brandId, sellerId, categoryId, declaredMrp, listingPrice, description, countryOfOrigin, productUrl, ingestionSource, status, createdAt, updatedAt);
        }
    }
}
