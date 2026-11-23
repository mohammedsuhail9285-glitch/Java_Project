package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ocr_results")
public class OCRResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_image_id", nullable = false)
    private Long productImageId;

    @Column(name = "raw_text", columnDefinition = "TEXT")
    private String rawText;

    @Column(length = 32)
    private String language = "eng";

    @Column(nullable = false)
    private Double confidence = 0.0;

    @Column(length = 64)
    private String engine = "tesseract";

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    public OCRResult() {}

    public OCRResult(Long id, Long productImageId, String rawText, String language, Double confidence, String engine, LocalDateTime processedAt) {
        this.id = id;
        this.productImageId = productImageId;
        this.rawText = rawText;
        this.language = language != null ? language : "eng";
        this.confidence = confidence != null ? confidence : 0.0;
        this.engine = engine != null ? engine : "tesseract";
        this.processedAt = processedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (processedAt == null) {
            processedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductImageId() { return productImageId; }
    public void setProductImageId(Long productImageId) { this.productImageId = productImageId; }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public Double getConfidence() { return confidence; }
    public void setConfidence(Double confidence) { this.confidence = confidence; }

    public String getEngine() { return engine; }
    public void setEngine(String engine) { this.engine = engine; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productImageId;
        private String rawText;
        private String language = "eng";
        private Double confidence = 0.0;
        private String engine = "tesseract";
        private LocalDateTime processedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productImageId(Long productImageId) { this.productImageId = productImageId; return this; }
        public Builder rawText(String rawText) { this.rawText = rawText; return this; }
        public Builder language(String language) { this.language = language; return this; }
        public Builder confidence(Double confidence) { this.confidence = confidence; return this; }
        public Builder engine(String engine) { this.engine = engine; return this; }
        public Builder processedAt(LocalDateTime processedAt) { this.processedAt = processedAt; return this; }

        public OCRResult build() {
            return new OCRResult(id, productImageId, rawText, language, confidence, engine, processedAt);
        }
    }
}
