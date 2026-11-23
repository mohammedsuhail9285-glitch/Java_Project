package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_results")
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "scan_job_id", nullable = false)
    private Long scanJobId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ScanResult() {}

    public ScanResult(Long id, Long scanJobId, Long productId, String summary, LocalDateTime createdAt) {
        this.id = id;
        this.scanJobId = scanJobId;
        this.productId = productId;
        this.summary = summary;
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

    public Long getScanJobId() { return scanJobId; }
    public void setScanJobId(Long scanJobId) { this.scanJobId = scanJobId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long scanJobId;
        private Long productId;
        private String summary;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder scanJobId(Long scanJobId) { this.scanJobId = scanJobId; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ScanResult build() {
            return new ScanResult(id, scanJobId, productId, summary, createdAt);
        }
    }
}
