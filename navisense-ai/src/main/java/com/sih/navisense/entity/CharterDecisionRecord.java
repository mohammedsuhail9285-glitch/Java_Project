package com.sih.navisense.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "charter_decision_records")
public class CharterDecisionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String fingerprint;

    @Column(nullable = false)
    private LocalDateTime decisionTimestamp;

    @Column(nullable = false, length = 100)
    private String cargoType;

    @Column(nullable = false)
    private Double quantityTonnes;

    @Column(nullable = false, length = 100)
    private String originCountry;

    @Column(nullable = false, length = 100)
    private String loadingPort;

    @Column(nullable = false, length = 100)
    private String destinationPort;

    @Column(nullable = false, length = 50)
    private String recommendedVessel;

    @Column(nullable = false, length = 100)
    private String contractStrategy;

    @Column(nullable = false, length = 50)
    private String primaryDecision; // BOOK_NOW, WAIT, MONITOR, etc.

    @Column(nullable = false)
    private Double estimatedFreightRateUsd;

    @Column(nullable = false)
    private Double forecastFreightRateUsd;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCostUsd;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalCostInr;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal potentialSavingsInr;

    @Column(nullable = false)
    private Integer confidenceScore;

    @Column(nullable = false, length = 30)
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL

    @Lob
    @Column(columnDefinition = "CLOB")
    private String rationale;

    @Column(length = 100)
    private String bestCharterWindow;

    @Column(precision = 19, scale = 2)
    private BigDecimal budgetInr;

    public CharterDecisionRecord() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public LocalDateTime getDecisionTimestamp() { return decisionTimestamp; }
    public void setDecisionTimestamp(LocalDateTime decisionTimestamp) { this.decisionTimestamp = decisionTimestamp; }

    public String getCargoType() { return cargoType; }
    public void setCargoType(String cargoType) { this.cargoType = cargoType; }

    public Double getQuantityTonnes() { return quantityTonnes; }
    public void setQuantityTonnes(Double quantityTonnes) { this.quantityTonnes = quantityTonnes; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public String getRecommendedVessel() { return recommendedVessel; }
    public void setRecommendedVessel(String recommendedVessel) { this.recommendedVessel = recommendedVessel; }

    public String getContractStrategy() { return contractStrategy; }
    public void setContractStrategy(String contractStrategy) { this.contractStrategy = contractStrategy; }

    public String getPrimaryDecision() { return primaryDecision; }
    public void setPrimaryDecision(String primaryDecision) { this.primaryDecision = primaryDecision; }

    public Double getEstimatedFreightRateUsd() { return estimatedFreightRateUsd; }
    public void setEstimatedFreightRateUsd(Double estimatedFreightRateUsd) { this.estimatedFreightRateUsd = estimatedFreightRateUsd; }

    public Double getForecastFreightRateUsd() { return forecastFreightRateUsd; }
    public void setForecastFreightRateUsd(Double forecastFreightRateUsd) { this.forecastFreightRateUsd = forecastFreightRateUsd; }

    public BigDecimal getTotalCostUsd() { return totalCostUsd; }
    public void setTotalCostUsd(BigDecimal totalCostUsd) { this.totalCostUsd = totalCostUsd; }

    public BigDecimal getTotalCostInr() { return totalCostInr; }
    public void setTotalCostInr(BigDecimal totalCostInr) { this.totalCostInr = totalCostInr; }

    public BigDecimal getPotentialSavingsInr() { return potentialSavingsInr; }
    public void setPotentialSavingsInr(BigDecimal potentialSavingsInr) { this.potentialSavingsInr = potentialSavingsInr; }

    public Integer getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Integer confidenceScore) { this.confidenceScore = confidenceScore; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public String getBestCharterWindow() { return bestCharterWindow; }
    public void setBestCharterWindow(String bestCharterWindow) { this.bestCharterWindow = bestCharterWindow; }

    public BigDecimal getBudgetInr() { return budgetInr; }
    public void setBudgetInr(BigDecimal budgetInr) { this.budgetInr = budgetInr; }
}
