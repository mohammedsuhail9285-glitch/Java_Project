package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String name;

    @Column(length = 255)
    private String location;

    @Column(name = "state_region", length = 64)
    private String stateRegion;

    @Column(name = "risk_score", nullable = false)
    private Double riskScore = 0.0;

    @Column(name = "contact_email", length = 128)
    private String contactEmail;

    @Column(name = "registration_number", length = 64)
    private String registrationNumber;

    @Column(name = "total_products", nullable = false)
    private Integer totalProducts = 0;

    @Column(name = "violation_count", nullable = false)
    private Integer violationCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Seller() {}

    public Seller(Long id, String name, String location, String stateRegion, Double riskScore, String contactEmail, String registrationNumber, Integer totalProducts, Integer violationCount, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.stateRegion = stateRegion;
        this.riskScore = riskScore != null ? riskScore : 0.0;
        this.contactEmail = contactEmail;
        this.registrationNumber = registrationNumber;
        this.totalProducts = totalProducts != null ? totalProducts : 0;
        this.violationCount = violationCount != null ? violationCount : 0;
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

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStateRegion() { return stateRegion; }
    public void setStateRegion(String stateRegion) { this.stateRegion = stateRegion; }

    public Double getRiskScore() { return riskScore; }
    public void setRiskScore(Double riskScore) { this.riskScore = riskScore; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public Integer getTotalProducts() { return totalProducts; }
    public void setTotalProducts(Integer totalProducts) { this.totalProducts = totalProducts; }

    public Integer getViolationCount() { return violationCount; }
    public void setViolationCount(Integer violationCount) { this.violationCount = violationCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String location;
        private String stateRegion;
        private Double riskScore = 0.0;
        private String contactEmail;
        private String registrationNumber;
        private Integer totalProducts = 0;
        private Integer violationCount = 0;
        private LocalDateTime createdAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder location(String location) { this.location = location; return this; }
        public Builder stateRegion(String stateRegion) { this.stateRegion = stateRegion; return this; }
        public Builder riskScore(Double riskScore) { this.riskScore = riskScore; return this; }
        public Builder contactEmail(String contactEmail) { this.contactEmail = contactEmail; return this; }
        public Builder registrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; return this; }
        public Builder totalProducts(Integer totalProducts) { this.totalProducts = totalProducts; return this; }
        public Builder violationCount(Integer violationCount) { this.violationCount = violationCount; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Seller build() {
            return new Seller(id, name, location, stateRegion, riskScore, contactEmail, registrationNumber, totalProducts, violationCount, createdAt);
        }
    }
}
