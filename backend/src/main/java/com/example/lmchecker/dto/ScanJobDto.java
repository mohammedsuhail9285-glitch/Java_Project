package com.example.lmchecker.dto;

import com.example.lmchecker.entity.ScanJobStatus;
import java.time.LocalDateTime;

public class ScanJobDto {
    private Long id;
    private ScanJobStatus status;
    private Long productId;
    private String batchId;
    private Integer progressPct;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String errorMessage;
    private LocalDateTime createdAt;
    private Integer totalItems;
    private Integer processedItems;
    private String jobType;

    public ScanJobDto() {}

    public ScanJobDto(Long id, ScanJobStatus status, Long productId, String batchId, Integer progressPct, LocalDateTime startedAt, LocalDateTime completedAt, String errorMessage, LocalDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.productId = productId;
        this.batchId = batchId;
        this.progressPct = progressPct;
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.errorMessage = errorMessage;
        this.createdAt = createdAt;
        this.totalItems = 1;
        this.processedItems = (status == ScanJobStatus.COMPLETED) ? 1 : 0;
        this.jobType = batchId != null ? "BATCH_SCAN" : "LMPC_AUTOMATED_SCAN";
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
    public void setProgressPct(Integer progressPct) { this.progressPct = progressPct; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getTotalItems() { return totalItems; }
    public void setTotalItems(Integer totalItems) { this.totalItems = totalItems; }

    public Integer getProcessedItems() { return processedItems; }
    public void setProcessedItems(Integer processedItems) { this.processedItems = processedItems; }

    public String getJobType() { return jobType; }
    public void setJobType(String jobType) { this.jobType = jobType; }
}
