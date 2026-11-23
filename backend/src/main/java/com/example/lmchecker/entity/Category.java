package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "categories")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String name;

    @Column(name = "parent_category", length = 128)
    private String parentCategory;

    @Column(name = "risk_weight", nullable = false)
    private Double riskWeight = 1.0;

    @Column(name = "mandatory_fields", columnDefinition = "TEXT")
    private String mandatoryFields;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Category() {}

    public Category(Long id, String name, String parentCategory, Double riskWeight, String mandatoryFields, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.parentCategory = parentCategory;
        this.riskWeight = riskWeight != null ? riskWeight : 1.0;
        this.mandatoryFields = mandatoryFields;
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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParentCategory() { return parentCategory; }
    public void setParentCategory(String parentCategory) { this.parentCategory = parentCategory; }

    public Double getRiskWeight() { return riskWeight; }
    public void setRiskWeight(Double riskWeight) { this.riskWeight = riskWeight; }

    public String getMandatoryFields() { return mandatoryFields; }
    public void setMandatoryFields(String mandatoryFields) { this.mandatoryFields = mandatoryFields; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String parentCategory;
        private Double riskWeight = 1.0;
        private String mandatoryFields;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder parentCategory(String parentCategory) { this.parentCategory = parentCategory; return this; }
        public Builder riskWeight(Double riskWeight) { this.riskWeight = riskWeight; return this; }
        public Builder mandatoryFields(String mandatoryFields) { this.mandatoryFields = mandatoryFields; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Category build() {
            return new Category(id, name, parentCategory, riskWeight, mandatoryFields, createdAt);
        }
    }
}
