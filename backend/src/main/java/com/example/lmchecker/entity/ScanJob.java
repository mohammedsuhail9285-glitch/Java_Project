package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scan_jobs")
public class ScanJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ScanJobStatus status = ScanJobStatus.QUEUED;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "batch_id", length = 64)
    private String batchId;

    @Column(name = "progress_pct", nullable = false)
    private Integer progressPct = 0;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ScanJob() {}

    public ScanJob(Long id, ScanJobStatus status, Long productId, String batchId, Integer progressPct, LocalDateTime startedAt, LocalDateTime completedAt, String errorMessage, LocalDateTime createdAt) {
        this.id = id;
        this.status = status != null ? status : ScanJobStatus.QUEUED;
        this.productId = productId;
        this.batchId = batchId;
        this.progressPct = progressPct != null ? progressPct : 0;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
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

    public ScanJobStatus getStatus() { return status; }
    public void setStatus(ScanJobStatus status) { this.status = status; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public Integer getProgressPct() { return progressPct; }
    public void setProgressPct(Integer progressPct) {
        this.progressPct = progressPct;
        if (progressPct != null && progressPct >= 100 && this.status != ScanJobStatus.FAILED) {
            this.status = ScanJobStatus.COMPLETED;
            if (this.completedAt == null) {
                this.completedAt = LocalDateTime.now();
            }
        }
    }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private ScanJobStatus status = ScanJobStatus.QUEUED;
        private Long productId;
        private String batchId;
        private Integer progressPct = 0;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
        private String errorMessage;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder status(ScanJobStatus status) { this.status = status; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder batchId(String batchId) { this.batchId = batchId; return this; }
        public Builder progressPct(Integer progressPct) { this.progressPct = progressPct; return this; }
        public Builder startedAt(LocalDateTime startedAt) { this.startedAt = startedAt; return this; }
        public Builder completedAt(LocalDateTime completedAt) { this.completedAt = completedAt; return this; }
        public Builder errorMessage(String errorMessage) { this.errorMessage = errorMessage; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ScanJob build() {
            return new ScanJob(id, status, productId, batchId, progressPct, startedAt, completedAt, errorMessage, createdAt);
        }
    }
}
