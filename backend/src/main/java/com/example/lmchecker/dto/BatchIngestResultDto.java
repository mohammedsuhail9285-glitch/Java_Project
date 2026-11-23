package com.example.lmchecker.dto;

import java.util.List;

public class BatchIngestResultDto {
    private String batchId;
    private int totalProcessed;
    private int successCount;
    private int failureCount;
    private Long scanJobId;
    private List<String> errorMessages;

    public BatchIngestResultDto() {}

    public BatchIngestResultDto(String batchId, int totalProcessed, int successCount, int failureCount, Long scanJobId, List<String> errorMessages) {
        this.batchId = batchId;
        this.totalProcessed = totalProcessed;
        this.successCount = successCount;
        this.failureCount = failureCount;
        this.scanJobId = scanJobId;
        this.errorMessages = errorMessages;
    }

    public String getBatchId() { return batchId; }
    public void setBatchId(String batchId) { this.batchId = batchId; }

    public int getTotalProcessed() { return totalProcessed; }
    public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }

    public int getSuccessCount() { return successCount; }
    public void setSuccessCount(int successCount) { this.successCount = successCount; }

    public int getFailureCount() { return failureCount; }
    public void setFailureCount(int failureCount) { this.failureCount = failureCount; }

    public Long getScanJobId() { return scanJobId; }
    public void setScanJobId(Long scanJobId) { this.scanJobId = scanJobId; }

    public List<String> getErrorMessages() { return errorMessages; }
    public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }
}
