package com.example.lmchecker.dto;

import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.Severity;
import com.example.lmchecker.entity.ViolationStatus;

import java.time.LocalDateTime;

public class ViolationDto {
    private Long id;
    private String violationId;
    private Long productId;
    private String productName;
    private String ruleId;
    private String ruleName;
    private Severity severity;
    private String detectedValue;
    private String expectedCondition;
    private ViolationStatus status;
    private FieldSource source;
    private Double ocrConfidence;
    private Double detectionConfidence;
    private LocalDateTime timestamp;

    // Evidence fields
    private Long evidenceId;
    private String originalImagePath;
    private String croppedImagePath;
    private String ocrText;
    private String boundingBox;
    private String sourceUrl;

    // Review fields
    private Long reviewerId;
    private String reviewerUsername;
    private String reviewerComment;
    private String resolution;
    private LocalDateTime resolutionTimestamp;

    public ViolationDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getViolationId() { return violationId; }
    public void setViolationId(String violationId) { this.violationId = violationId; }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDetectedValue() { return detectedValue; }
    public void setDetectedValue(String detectedValue) { this.detectedValue = detectedValue; }

    public String getExpectedCondition() { return expectedCondition; }
    public void setExpectedCondition(String expectedCondition) { this.expectedCondition = expectedCondition; }

    public ViolationStatus getStatus() { return status; }
    public void setStatus(ViolationStatus status) { this.status = status; }

    public FieldSource getSource() { return source; }
    public void setSource(FieldSource source) { this.source = source; }

    public Double getOcrConfidence() { return ocrConfidence; }
    public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }

    public Double getDetectionConfidence() { return detectionConfidence; }
    public void setDetectionConfidence(Double detectionConfidence) { this.detectionConfidence = detectionConfidence; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Long getEvidenceId() { return evidenceId; }
    public void setEvidenceId(Long evidenceId) { this.evidenceId = evidenceId; }

    public String getOriginalImagePath() { return originalImagePath; }
    public void setOriginalImagePath(String originalImagePath) { this.originalImagePath = originalImagePath; }

    public String getCroppedImagePath() { return croppedImagePath; }
    public void setCroppedImagePath(String croppedImagePath) { this.croppedImagePath = croppedImagePath; }

    public String getOcrText() { return ocrText; }
    public void setOcrText(String ocrText) { this.ocrText = ocrText; }

    public String getBoundingBox() { return boundingBox; }
    public void setBoundingBox(String boundingBox) { this.boundingBox = boundingBox; }

    public String getSourceUrl() { return sourceUrl; }
    public void setSourceUrl(String sourceUrl) { this.sourceUrl = sourceUrl; }

    public Long getReviewerId() { return reviewerId; }
    public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewerUsername() { return reviewerUsername; }
    public void setReviewerUsername(String reviewerUsername) { this.reviewerUsername = reviewerUsername; }

    public String getReviewerComment() { return reviewerComment; }
    public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }

    public String getResolution() { return resolution; }
    public void setResolution(String resolution) { this.resolution = resolution; }

    public LocalDateTime getResolutionTimestamp() { return resolutionTimestamp; }
    public void setResolutionTimestamp(LocalDateTime resolutionTimestamp) { this.resolutionTimestamp = resolutionTimestamp; }
}
