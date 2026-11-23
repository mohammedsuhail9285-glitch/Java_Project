package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_listings")
public class ProductListing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "raw_extracted_fields", columnDefinition = "TEXT")
    private String rawExtractedFields;

    @Column(name = "source_type", length = 64)
    private String sourceType = "WEB_LISTING";

    @Column(name = "scraped_at", nullable = false)
    private LocalDateTime scrapedAt;

    public ProductListing() {}

    public ProductListing(Long id, Long productId, String rawExtractedFields, String sourceType, LocalDateTime scrapedAt) {
        this.id = id;
        this.productId = productId;
        this.rawExtractedFields = rawExtractedFields;
        this.sourceType = sourceType != null ? sourceType : "WEB_LISTING";
        this.scrapedAt = scrapedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (scrapedAt == null) {
            scrapedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getRawExtractedFields() { return rawExtractedFields; }
    public void setRawExtractedFields(String rawExtractedFields) { this.rawExtractedFields = rawExtractedFields; }

    public String getSourceType() { return sourceType; }
    public void setSourceType(String sourceType) { this.sourceType = sourceType; }

    public LocalDateTime getScrapedAt() { return scrapedAt; }
    public void setScrapedAt(LocalDateTime scrapedAt) { this.scrapedAt = scrapedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private String rawExtractedFields;
        private String sourceType = "WEB_LISTING";
        private LocalDateTime scrapedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder rawExtractedFields(String rawExtractedFields) { this.rawExtractedFields = rawExtractedFields; return this; }
        public Builder sourceType(String sourceType) { this.sourceType = sourceType; return this; }
        public Builder scrapedAt(LocalDateTime scrapedAt) { this.scrapedAt = scrapedAt; return this; }

        public ProductListing build() {
            return new ProductListing(id, productId, rawExtractedFields, sourceType, scrapedAt);
        }
    }
}
