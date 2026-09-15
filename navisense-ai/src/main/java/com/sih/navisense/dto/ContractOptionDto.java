package com.sih.navisense.dto;

import java.math.BigDecimal;

public class ContractOptionDto {
    private String strategyName; // Spot Charter, Short-Term COA, Medium-Term COA, Hybrid
    private BigDecimal expectedCostInr;
    private String expectedCostInrFormatted;
    private BigDecimal potentialSavingInr;
    private String potentialSavingInrFormatted;
    private String riskLevel; // LOW, MEDIUM, HIGH
    private int suitabilityScore; // 0 - 100
    private String volatilityExposure; // HIGH, MODERATE, LOW
    private String flexibility; // HIGH, MODERATE, LOW
    private String description;
    private boolean recommended;

    public ContractOptionDto() {}

    public String getStrategyName() { return strategyName; }
    public void setStrategyName(String strategyName) { this.strategyName = strategyName; }

    public BigDecimal getExpectedCostInr() { return expectedCostInr; }
    public void setExpectedCostInr(BigDecimal expectedCostInr) { this.expectedCostInr = expectedCostInr; }

    public String getExpectedCostInrFormatted() { return expectedCostInrFormatted; }
    public void setExpectedCostInrFormatted(String expectedCostInrFormatted) { this.expectedCostInrFormatted = expectedCostInrFormatted; }

    public BigDecimal getPotentialSavingInr() { return potentialSavingInr; }
    public void setPotentialSavingInr(BigDecimal potentialSavingInr) { this.potentialSavingInr = potentialSavingInr; }

    public String getPotentialSavingInrFormatted() { return potentialSavingInrFormatted; }
    public void setPotentialSavingInrFormatted(String potentialSavingInrFormatted) { this.potentialSavingInrFormatted = potentialSavingInrFormatted; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public int getSuitabilityScore() { return suitabilityScore; }
    public void setSuitabilityScore(int suitabilityScore) { this.suitabilityScore = suitabilityScore; }

    public String getVolatilityExposure() { return volatilityExposure; }
    public void setVolatilityExposure(String volatilityExposure) { this.volatilityExposure = volatilityExposure; }

    public String getFlexibility() { return flexibility; }
    public void setFlexibility(String flexibility) { this.flexibility = flexibility; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public boolean isRecommended() { return recommended; }
    public void setRecommended(boolean recommended) { this.recommended = recommended; }
}
