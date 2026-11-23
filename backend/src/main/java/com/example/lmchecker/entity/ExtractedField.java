package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "extracted_fields")
public class ExtractedField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false, length = 32)
    private FieldType fieldType;

    @Column(name = "raw_value", columnDefinition = "TEXT")
    private String rawValue;

    @Column(name = "normalized_value", columnDefinition = "TEXT")
    private String normalizedValue;

    @Column(length = 32)
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FieldSource source = FieldSource.IMAGE;

    @Column(nullable = false)
    private Double confidence = 0.0;

    @Column(name = "bounding_box", length = 128)
    private String boundingBox;

    @Column(name = "verified_by")
    private Long verifiedBy;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ExtractedField() {}

    public ExtractedField(Long id, Long productId, FieldType fieldType, String rawValue, String normalizedValue, String unit, FieldSource source, Double confidence, String boundingBox, Long verifiedBy, LocalDateTime verifiedAt, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.fieldType = fieldType;
        this.rawValue = rawValue;
        this.normalizedValue = normalizedValue;
        this.unit = unit;
        this.source = source != null ? source : FieldSource.IMAGE;
        this.confidence = confidence != null ? confidence : 0.0;
        this.boundingBox = boundingBox;
        this.verifiedBy = verifiedBy;
        this.verifiedAt = verifiedAt;
        this.createdAt = createdAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public FieldType getFieldType() { return fieldType; }
    public void setFieldType(FieldType fieldType) { this.fieldType = fieldType; }

    public String getRawValue() { return rawValue; }
    public void setRawValue(String rawValue) { this.rawValue = rawValue; }

    public String getNormalizedValue() { return normalizedValue; }
    public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public FieldSource getSource() { return source; }
    public void setSource(FieldSource source) { this.source = source; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getBoundingBox() { return boundingBox; }
    public void setBoundingBox(String boundingBox) { this.boundingBox = boundingBox; }

    public Long getVerifiedBy() { return verifiedBy; }
    public void setVerifiedBy(Long verifiedBy) { this.verifiedBy = verifiedBy; }

    public LocalDateTime getVerifiedAt() { return verifiedAt; }
    public void setVerifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private FieldType fieldType;
        private String rawValue;
        private String normalizedValue;
        private String unit;
        private FieldSource source = FieldSource.IMAGE;
        private Double confidence = 0.0;
        private String boundingBox;
        private Long verifiedBy;
        private LocalDateTime verifiedAt;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder fieldType(FieldType fieldType) { this.fieldType = fieldType; return this; }
        public Builder rawValue(String rawValue) { this.rawValue = rawValue; return this; }
        public Builder normalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder source(FieldSource source) { this.source = source; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder boundingBox(String boundingBox) { this.boundingBox = boundingBox; return this; }
        public Builder verifiedBy(Long verifiedBy) { this.verifiedBy = verifiedBy; return this; }
        public Builder verifiedAt(LocalDateTime verifiedAt) { this.verifiedAt = verifiedAt; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ExtractedField build() {
            return new ExtractedField(id, productId, fieldType, rawValue, normalizedValue, unit, source, confidence, boundingBox, verifiedBy, verifiedAt, createdAt);
        }
    }
}
