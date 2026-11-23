package com.example.lmchecker.ingestion;

public interface JobQueueService {
    void enqueueScanJob(Long scanJobId);
    void cancelJob(Long scanJobId);
}
