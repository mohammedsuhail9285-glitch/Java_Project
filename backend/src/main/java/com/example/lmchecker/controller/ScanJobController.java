package com.example.lmchecker.controller;

import com.example.lmchecker.dto.ApiResponse;
import com.example.lmchecker.dto.ScanJobDto;
import com.example.lmchecker.entity.ScanJob;
import com.example.lmchecker.entity.ScanJobStatus;
import com.example.lmchecker.exception.ResourceNotFoundException;
import com.example.lmchecker.ingestion.JobQueueService;
import com.example.lmchecker.repository.ScanJobRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/scan-jobs", "/api/v1/scans/jobs"})
public class ScanJobController {

    private final ScanJobRepository scanJobRepository;
    private final JobQueueService jobQueueService;

    public ScanJobController(ScanJobRepository scanJobRepository, JobQueueService jobQueueService) {
        this.scanJobRepository = scanJobRepository;
        this.jobQueueService = jobQueueService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ScanJobDto>>> getScanJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<ScanJob> jobs = scanJobRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
        Page<ScanJobDto> dtos = jobs.map(j -> {
            ScanJobStatus resolvedStatus = j.getStatus();
            if (j.getProgressPct() != null && j.getProgressPct() >= 100 && resolvedStatus != ScanJobStatus.FAILED) {
                resolvedStatus = ScanJobStatus.COMPLETED;
            }
            ScanJobDto dto = new ScanJobDto(
                j.getId(), resolvedStatus, j.getProductId(), j.getBatchId(),
                j.getProgressPct(), j.getStartedAt(), j.getCompletedAt(),
                j.getErrorMessage(), j.getCreatedAt()
            );
            int total = (j.getBatchId() != null && j.getBatchId().startsWith("BATCH-CRAWL")) ? 5 : 1;
            dto.setTotalItems(total);
            dto.setProcessedItems(resolvedStatus == ScanJobStatus.COMPLETED ? total : 
                (j.getProgressPct() != null ? Math.max(0, (j.getProgressPct() * total) / 100) : 0));
            dto.setJobType(j.getBatchId() != null ? "BATCH_INGESTION" : "SINGLE_PRODUCT_SCAN");
            return dto;
        });
        return ResponseEntity.ok(ApiResponse.ok("Scan jobs retrieved", dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ScanJobDto>> getScanJobById(@PathVariable Long id) {
        ScanJob j = scanJobRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Scan job not found with id: " + id));

        ScanJobStatus resolvedStatus = j.getStatus();
        if (j.getProgressPct() != null && j.getProgressPct() >= 100 && resolvedStatus != ScanJobStatus.FAILED) {
            resolvedStatus = ScanJobStatus.COMPLETED;
        }

        ScanJobDto dto = new ScanJobDto(
            j.getId(), resolvedStatus, j.getProductId(), j.getBatchId(),
            j.getProgressPct(), j.getStartedAt(), j.getCompletedAt(),
            j.getErrorMessage(), j.getCreatedAt()
        );
        return ResponseEntity.ok(ApiResponse.ok("Scan job retrieved", dto));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelJob(@PathVariable Long id) {
        jobQueueService.cancelJob(id);
        return ResponseEntity.ok(ApiResponse.ok("Scan job cancellation requested", "CANCELLED"));
    }
}
