package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "violations",
    indexes = {
        @Index(name = "idx_violations_status", columnList = "status"),
        @Index(name = "idx_violations_severity", columnList = "severity"),
        @Index(name = "idx_violations_rule_id", columnList = "rule_id"),
        @Index(name = "idx_violations_product_id", columnList = "product_id")
    }
)
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "violation_id", nullable = false, unique = true, length = 64)
    private String violationId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "rule_id", nullable = false, length = 32)
    private String ruleId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Severity severity;

    @Column(name = "detected_value", columnDefinition = "TEXT")
    private String detectedValue;

    @Column(name = "expected_condition", columnDefinition = "TEXT")
    private String expectedCondition;

    @Column(name = "evidence_id")
    private Long evidenceId;

    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private FieldSource source = FieldSource.IMAGE;

    @Column(name = "ocr_confidence")
    private Double ocrConfidence = 0.0;

    @Column(name = "detection_confidence", nullable = false)
    private Double detectionConfidence = 0.0;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ViolationStatus status = ViolationStatus.NEW;

    @Column(name = "reviewer_id")
    private Long reviewerId;

    @Column(name = "reviewer_comment", columnDefinition = "TEXT")
    private String reviewerComment;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    @Column(name = "resolution_timestamp")
    private LocalDateTime resolutionTimestamp;

    public Violation() {}

    public Violation(Long id, String violationId, Long productId, String ruleId, Severity severity, String detectedValue, String expectedCondition, Long evidenceId, FieldSource source, Double ocrConfidence, Double detectionConfidence, LocalDateTime timestamp, ViolationStatus status, Long reviewerId, String reviewerComment, String resolution, LocalDateTime resolutionTimestamp) {
        this.id = id;
        this.violationId = violationId;
        this.productId = productId;
        this.ruleId = ruleId;
        this.severity = severity;
        this.detectedValue = detectedValue;
        this.expectedCondition = expectedCondition;
        this.evidenceId = evidenceId;
        this.source = source != null ? source : FieldSource.IMAGE;
        this.ocrConfidence = ocrConfidence != null ? ocrConfidence : 0.0;
        this.detectionConfidence = detectionConfidence != null ? detectionConfidence : 0.0;
        this.timestamp = timestamp;
        this.status = status != null ? status : ViolationStatus.NEW;
        this.reviewerId = reviewerId;
        this.reviewerComment = reviewerComment;
        this.resolution = resolution;
        this.resolutionTimestamp = resolutionTimestamp;
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
        if (violationId == null) {
            violationId = "VIO-" + System.currentTimeMillis();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getViolationId() { return violationId; }
    public void setViolationId(String violationId) { this.violationId = violationId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDetectedValue() { return detectedValue; }
    public void setDetectedValue(String detectedValue) { this.detectedValue = detectedValue; }

    public String getExpectedCondition() { return expectedCondition; }
    public void setExpectedCondition(String expectedCondition) { this.expectedCondition = expectedCondition; }

    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }

    public FieldSource getSource() { return source; }
    public void setSource(FieldSource source) { this.source = source; }

    public Double getOcrConfidence() { return ocrConfidence; }
    public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }

    public Double getDetectionConfidence() { return detectionConfidence; }
    public void setDetectionConfidence(Double detectionConfidence) { this.detectionConfidence = detectionConfidence; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public ViolationStatus getStatus() { return status; }
    public void setStatus(ViolationStatus status) { this.status = status; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public LocalDateTime getResolutionTimestamp() { return resolutionTimestamp; }
    public void setResolutionTimestamp(LocalDateTime resolutionTimestamp) { this.resolutionTimestamp = resolutionTimestamp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String violationId;
        private Long productId;
        private String ruleId;
        private Severity severity;
        private String detectedValue;
        private String expectedCondition;
        private Long evidenceId;
        private FieldSource source = FieldSource.IMAGE;
        private Double ocrConfidence = 0.0;
        private Double detectionConfidence = 0.0;
        private LocalDateTime timestamp;
        private ViolationStatus status = ViolationStatus.NEW;
        private Long reviewerId;
        private String reviewerComment;
        private String resolution;
        private LocalDateTime resolutionTimestamp;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder violationId(String violationId) { this.violationId = violationId; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder severity(Severity severity) { this.severity = severity; return this; }
        public Builder detectedValue(String detectedValue) { this.detectedValue = detectedValue; return this; }
        public Builder expectedCondition(String expectedCondition) { this.expectedCondition = expectedCondition; return this; }
        public Builder evidenceId(Long evidenceId) { this.evidenceId = evidenceId; return this; }
        public Builder source(FieldSource source) { this.source = source; return this; }
        public Builder ocrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; return this; }
        public Builder detectionConfidence(Double detectionConfidence) { this.detectionConfidence = detectionConfidence; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public Builder status(ViolationStatus status) { this.status = status; return this; }
        public Builder reviewerId(Long reviewerId) { this.reviewerId = reviewerId; return this; }
        public Builder reviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; return this; }
        public Builder resolution(String resolution) { this.resolution = resolution; return this; }
        public Builder resolutionTimestamp(LocalDateTime resolutionTimestamp) { this.resolutionTimestamp = resolutionTimestamp; return this; }

        public Violation build() {
            return new Violation(id, violationId, productId, ruleId, severity, detectedValue, expectedCondition, evidenceId, source, ocrConfidence, detectionConfidence, timestamp, status, reviewerId, reviewerComment, resolution, resolutionTimestamp);
        }
    }
}
