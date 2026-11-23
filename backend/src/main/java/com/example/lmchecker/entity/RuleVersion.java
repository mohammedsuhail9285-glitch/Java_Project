package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "rule_versions")
public class RuleVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "rule_id", nullable = false, length = 32)
    private String ruleId;

    @Column(name = "version_number", nullable = false)
    private Integer versionNumber;

    @Column(name = "config_snapshot", columnDefinition = "TEXT", nullable = false)
    private String configSnapshot;

    @Column(name = "changed_by", length = 64)
    private String changedBy;

    @Column(name = "change_reason", columnDefinition = "TEXT")
    private String changeReason;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public RuleVersion() {}

    public RuleVersion(Long id, String ruleId, Integer versionNumber, String configSnapshot, String changedBy, String changeReason, LocalDateTime changedAt) {
        this.id = id;
        this.ruleId = ruleId;
        this.versionNumber = versionNumber;
        this.configSnapshot = configSnapshot;
        this.changedBy = changedBy;
        this.changeReason = changeReason;
        this.changedAt = changedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (changedAt == null) {
            changedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public Integer getVersionNumber() { return versionNumber; }
    public void setVersionNumber(Integer versionNumber) { this.versionNumber = versionNumber; }

    public String getConfigSnapshot() { return configSnapshot; }
    public void setConfigSnapshot(String configSnapshot) { this.configSnapshot = configSnapshot; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getChangeReason() { return changeReason; }
    public void setChangeReason(String changeReason) { this.changeReason = changeReason; }

    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String ruleId;
        private Integer versionNumber;
        private String configSnapshot;
        private String changedBy;
        private String changeReason;
        private LocalDateTime changedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder ruleId(String ruleId) { this.ruleId = ruleId; return this; }
        public Builder versionNumber(Integer versionNumber) { this.versionNumber = versionNumber; return this; }
        public Builder configSnapshot(String configSnapshot) { this.configSnapshot = configSnapshot; return this; }
        public Builder changedBy(String changedBy) { this.changedBy = changedBy; return this; }
        public Builder changeReason(String changeReason) { this.changeReason = changeReason; return this; }
        public Builder changedAt(LocalDateTime changedAt) { this.changedAt = changedAt; return this; }

        public RuleVersion build() {
            return new RuleVersion(id, ruleId, versionNumber, configSnapshot, changedBy, changeReason, changedAt);
        }
    }
}
