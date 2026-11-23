package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "product_images")
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "original_path", nullable = false, length = 512)
    private String originalPath;

    @Column(name = "processed_path", length = 512)
    private String processedPath;

    @Column(name = "image_type", length = 64)
    private String imageType = "PACKAGE_LABEL";

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    public ProductImage() {}

    public ProductImage(Long id, Long productId, String originalPath, String processedPath, String imageType, LocalDateTime uploadedAt) {
        this.id = id;
        this.productId = productId;
        this.originalPath = originalPath;
        this.processedPath = processedPath;
        this.imageType = imageType != null ? imageType : "PACKAGE_LABEL";
        this.uploadedAt = uploadedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (uploadedAt == null) {
            uploadedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getOriginalPath() { return originalPath; }
    public void setOriginalPath(String originalPath) { this.originalPath = originalPath; }

    public String getProcessedPath() { return processedPath; }
    public void setProcessedPath(String processedPath) { this.processedPath = processedPath; }

    public String getImageType() { return imageType; }
    public void setImageType(String imageType) { this.imageType = imageType; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private String originalPath;
        private String processedPath;
        private String imageType = "PACKAGE_LABEL";
        private LocalDateTime uploadedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder originalPath(String originalPath) { this.originalPath = originalPath; return this; }
        public Builder processedPath(String processedPath) { this.processedPath = processedPath; return this; }
        public Builder imageType(String imageType) { this.imageType = imageType; return this; }
        public Builder uploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; return this; }

        public ProductImage build() {
            return new ProductImage(id, productId, originalPath, processedPath, imageType, uploadedAt);
        }
    }
}
