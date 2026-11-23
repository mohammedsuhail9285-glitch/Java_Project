package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_checks")
public class ComplianceCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(name = "scan_job_id")
    private Long scanJobId;

    @Column(name = "overall_score", nullable = false)
    private Double overallScore = 0.0;

    @Column(name = "mandatory_field_completion_pct", nullable = false)
    private Double mandatoryFieldCompletionPct = 0.0;

    @Column(name = "critical_count", nullable = false)
    private Integer criticalCount = 0;

    @Column(name = "major_count", nullable = false)
    private Integer majorCount = 0;

    @Column(name = "minor_count", nullable = false)
    private Integer minorCount = 0;

    @Column(name = "ocr_confidence", nullable = false)
    private Double ocrConfidence = 0.0;

    @Column(name = "ai_confidence", nullable = false)
    private Double aiConfidence = 0.0;

    @Column(name = "rule_engine_confidence", nullable = false)
    private Double ruleEngineConfidence = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 32)
    private RiskLevel riskLevel = RiskLevel.LOW;

    @Column(name = "summary_notes", columnDefinition = "TEXT")
    private String summaryNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public ComplianceCheck() {}

    public ComplianceCheck(Long id, Long productId, Long scanJobId, Double overallScore, Double mandatoryFieldCompletionPct, Integer criticalCount, Integer majorCount, Integer minorCount, Double ocrConfidence, Double aiConfidence, Double ruleEngineConfidence, RiskLevel riskLevel, String summaryNotes, LocalDateTime createdAt) {
        this.id = id;
        this.productId = productId;
        this.scanJobId = scanJobId;
        this.overallScore = overallScore != null ? overallScore : 0.0;
        this.mandatoryFieldCompletionPct = mandatoryFieldCompletionPct != null ? mandatoryFieldCompletionPct : 0.0;
        this.criticalCount = criticalCount != null ? criticalCount : 0;
        this.majorCount = majorCount != null ? majorCount : 0;
        this.minorCount = minorCount != null ? minorCount : 0;
        this.ocrConfidence = ocrConfidence != null ? ocrConfidence : 0.0;
        this.aiConfidence = aiConfidence != null ? aiConfidence : 0.0;
        this.ruleEngineConfidence = ruleEngineConfidence != null ? ruleEngineConfidence : 0.0;
        this.riskLevel = riskLevel != null ? riskLevel : RiskLevel.LOW;
        this.summaryNotes = summaryNotes;
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

    public Long getScanJobId() { return scanJobId; }
    public void setScanJobId(Long scanJobId) { this.scanJobId = scanJobId; }

    public Double getOverallScore() { return overallScore; }
    public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }

    public Double getMandatoryFieldCompletionPct() { return mandatoryFieldCompletionPct; }
    public void setMandatoryFieldCompletionPct(Double mandatoryFieldCompletionPct) { this.mandatoryFieldCompletionPct = mandatoryFieldCompletionPct; }

    public Integer getCriticalCount() { return criticalCount; }
    public void setCriticalCount(Integer criticalCount) { this.criticalCount = criticalCount; }

    public Integer getMajorCount() { return majorCount; }
    public void setMajorCount(Integer majorCount) { this.majorCount = majorCount; }

    public Integer getMinorCount() { return minorCount; }
    public void setMinorCount(Integer minorCount) { this.minorCount = minorCount; }

    public Double getOcrConfidence() { return ocrConfidence; }
    public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }

    public Double getAiConfidence() { return aiConfidence; }
    public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }

    public Double getRuleEngineConfidence() { return ruleEngineConfidence; }
    public void setRuleEngineConfidence(Double ruleEngineConfidence) { this.ruleEngineConfidence = ruleEngineConfidence; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getSummaryNotes() { return summaryNotes; }
    public void setSummaryNotes(String summaryNotes) { this.summaryNotes = summaryNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long productId;
        private Long scanJobId;
        private Double overallScore = 0.0;
        private Double mandatoryFieldCompletionPct = 0.0;
        private Integer criticalCount = 0;
        private Integer majorCount = 0;
        private Integer minorCount = 0;
        private Double ocrConfidence = 0.0;
        private Double aiConfidence = 0.0;
        private Double ruleEngineConfidence = 0.0;
        private RiskLevel riskLevel = RiskLevel.LOW;
        private String summaryNotes;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder productId(Long productId) { this.productId = productId; return this; }
        public Builder scanJobId(Long scanJobId) { this.scanJobId = scanJobId; return this; }
        public Builder overallScore(Double overallScore) { this.overallScore = overallScore; return this; }
        public Builder mandatoryFieldCompletionPct(Double mandatoryFieldCompletionPct) { this.mandatoryFieldCompletionPct = mandatoryFieldCompletionPct; return this; }
        public Builder criticalCount(Integer criticalCount) { this.criticalCount = criticalCount; return this; }
        public Builder majorCount(Integer majorCount) { this.majorCount = majorCount; return this; }
        public Builder minorCount(Integer minorCount) { this.minorCount = minorCount; return this; }
        public Builder ocrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; return this; }
        public Builder aiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; return this; }
        public Builder ruleEngineConfidence(Double ruleEngineConfidence) { this.ruleEngineConfidence = ruleEngineConfidence; return this; }
        public Builder riskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; return this; }
        public Builder summaryNotes(String summaryNotes) { this.summaryNotes = summaryNotes; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public ComplianceCheck build() {
            return new ComplianceCheck(id, productId, scanJobId, overallScore, mandatoryFieldCompletionPct, criticalCount, majorCount, minorCount, ocrConfidence, aiConfidence, ruleEngineConfidence, riskLevel, summaryNotes, createdAt);
        }
    }
}
