package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "rules")
public class Rule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false, unique = true, length = 32)
    private String ruleId;

    @Column(name = "rule_name", nullable = false, length = 128)
    private String ruleName;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 64)
    private String category;

    @Column(name = "applicable_product_category", length = 128)
    private String applicableProductCategory = "ALL";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "validation_type", nullable = false, length = 64)
    private ValidationType validationType;

    @Column(name = "expected_field", length = 64)
    private String expectedField;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(nullable = false)
    private Integer version = 1;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @Column(columnDefinition = "TEXT")
    private String source;

    @Column(name = "remediation_message", columnDefinition = "TEXT")
    private String remediationMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Rule() {}

    public Rule(Long id, String ruleId, String ruleName, String description, String category, String applicableProductCategory, Severity severity, ValidationType validationType, String expectedField, Boolean enabled, Integer version, LocalDate effectiveDate, String source, String remediationMessage, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.description = description;
        this.category = category;
        this.applicableProductCategory = applicableProductCategory != null ? applicableProductCategory : "ALL";
        this.severity = severity;
        this.validationType = validationType;
        this.expectedField = expectedField;
        this.enabled = enabled != null ? enabled : true;
        this.version = version != null ? version : 1;
        this.effectiveDate = effectiveDate;
        this.source = source;
        this.remediationMessage = remediationMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
        if (effectiveDate == null) {
            effectiveDate = LocalDate.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getApplicableProductCategory() { return applicableProductCategory; }
    public void setApplicableProductCategory(String applicableProductCategory) { this.applicableProductCategory = applicableProductCategory; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public ValidationType getValidationType() { return validationType; }
    public void setValidationType(ValidationType validationType) { this.validationType = validationType; }

    public String getExpectedField() { return expectedField; }
    public void setExpectedField(String expectedField) { this.expectedField = expectedField; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getRemediationMessage() { return remediationMessage; }
    public void setRemediationMessage(String remediationMessage) { this.remediationMessage = remediationMessage; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String ruleId;
        private String ruleName;
        private String description;
        private String category;
        private String applicableProductCategory = "ALL";
        private Severity severity;
        private ValidationType validationType;
        private String expectedField;
        private Boolean enabled = true;
        private Integer version = 1;
        private LocalDate effectiveDate;
        private String source;
        private String remediationMessage;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder ruleName(String ruleName) { this.ruleName = ruleName; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder applicableProductCategory(String applicableProductCategory) { this.applicableProductCategory = applicableProductCategory; return this; }
        public Builder severity(Severity severity) { this.severity = severity; return this; }
        public Builder validationType(ValidationType validationType) { this.validationType = validationType; return this; }
        public Builder expectedField(String expectedField) { this.expectedField = expectedField; return this; }
        public Builder enabled(Boolean enabled) { this.enabled = enabled; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder effectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; return this; }
        public Builder source(String source) { this.source = source; return this; }
        public Builder remediationMessage(String remediationMessage) { this.remediationMessage = remediationMessage; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public Rule build() {
            return new Rule(id, ruleId, ruleName, description, category, applicableProductCategory, severity, validationType, expectedField, enabled, version, effectiveDate, source, remediationMessage, createdAt, updatedAt);
        }
    }
}
