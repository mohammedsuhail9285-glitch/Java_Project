package com.sih.navisense.dto;

import java.math.BigDecimal;

public class SimulationResultDto {
    private String scenarioTitle;

    // Base Scenario
    private double baseFreightRateUsd;
    private BigDecimal baseTotalCostInr;
    private String baseTotalCostInrFormatted;
    private String baseRiskLevel;
    private int baseRiskScore;
    private String baseRecommendedDecision;
    private String baseRecommendedVessel;

    // Modified / Simulated Scenario
    private double simulatedFreightRateUsd;
    private BigDecimal simulatedTotalCostInr;
    private String simulatedTotalCostInrFormatted;
    private String simulatedRiskLevel;
    private int simulatedRiskScore;
    private String simulatedRecommendedDecision;
    private String simulatedRecommendedVessel;

    // Deltas
    private BigDecimal costDeltaInr;
    private String costDeltaInrFormatted;
    private double costDeltaPercentage;
    private int riskScoreDelta;
    private String keyInsight;
    private String suggestedMitigation;

    public SimulationResultDto() {}

    public String getScenarioTitle() { return scenarioTitle; }
    public void setScenarioTitle(String scenarioTitle) { this.scenarioTitle = scenarioTitle; }

    public double getBaseFreightRateUsd() { return baseFreightRateUsd; }
    public void setBaseFreightRateUsd(double baseFreightRateUsd) { this.baseFreightRateUsd = baseFreightRateUsd; }

    public BigDecimal getBaseTotalCostInr() { return baseTotalCostInr; }
    public void setBaseTotalCostInr(BigDecimal baseTotalCostInr) { this.baseTotalCostInr = baseTotalCostInr; }

    public String getBaseTotalCostInrFormatted() { return baseTotalCostInrFormatted; }
    public void setBaseTotalCostInrFormatted(String baseTotalCostInrFormatted) { this.baseTotalCostInrFormatted = baseTotalCostInrFormatted; }

    public String getBaseRiskLevel() { return baseRiskLevel; }
    public void setBaseRiskLevel(String baseRiskLevel) { this.baseRiskLevel = baseRiskLevel; }

    public int getBaseRiskScore() { return baseRiskScore; }
    public void setBaseRiskScore(int baseRiskScore) { this.baseRiskScore = baseRiskScore; }

    public String getBaseRecommendedDecision() { return baseRecommendedDecision; }
    public void setBaseRecommendedDecision(String baseRecommendedDecision) { this.baseRecommendedDecision = baseRecommendedDecision; }

    public String getBaseRecommendedVessel() { return baseRecommendedVessel; }
    public void setBaseRecommendedVessel(String baseRecommendedVessel) { this.baseRecommendedVessel = baseRecommendedVessel; }

    public double getSimulatedFreightRateUsd() { return simulatedFreightRateUsd; }
    public void setSimulatedFreightRateUsd(double simulatedFreightRateUsd) { this.simulatedFreightRateUsd = simulatedFreightRateUsd; }

    public BigDecimal getSimulatedTotalCostInr() { return simulatedTotalCostInr; }
    public void setSimulatedTotalCostInr(BigDecimal simulatedTotalCostInr) { this.simulatedTotalCostInr = simulatedTotalCostInr; }

    public String getSimulatedTotalCostInrFormatted() { return simulatedTotalCostInrFormatted; }
    public void setSimulatedTotalCostInrFormatted(String simulatedTotalCostInrFormatted) { this.simulatedTotalCostInrFormatted = simulatedTotalCostInrFormatted; }

    public String getSimulatedRiskLevel() { return simulatedRiskLevel; }
    public void setSimulatedRiskLevel(String simulatedRiskLevel) { this.simulatedRiskLevel = simulatedRiskLevel; }

    public int getSimulatedRiskScore() { return simulatedRiskScore; }
    public void setSimulatedRiskScore(int simulatedRiskScore) { this.simulatedRiskScore = simulatedRiskScore; }

    public String getSimulatedRecommendedDecision() { return simulatedRecommendedDecision; }
    public void setSimulatedRecommendedDecision(String simulatedRecommendedDecision) { this.simulatedRecommendedDecision = simulatedRecommendedDecision; }

    public String getSimulatedRecommendedVessel() { return simulatedRecommendedVessel; }
    public void setSimulatedRecommendedVessel(String simulatedRecommendedVessel) { this.simulatedRecommendedVessel = simulatedRecommendedVessel; }

    public BigDecimal getCostDeltaInr() { return costDeltaInr; }
    public void setCostDeltaInr(BigDecimal costDeltaInr) { this.costDeltaInr = costDeltaInr; }

    public String getCostDeltaInrFormatted() { return costDeltaInrFormatted; }
    public void setCostDeltaInrFormatted(String costDeltaInrFormatted) { this.costDeltaInrFormatted = costDeltaInrFormatted; }

    public double getCostDeltaPercentage() { return costDeltaPercentage; }
    public void setCostDeltaPercentage(double costDeltaPercentage) { this.costDeltaPercentage = costDeltaPercentage; }

    public int getRiskScoreDelta() { return riskScoreDelta; }
    public void setRiskScoreDelta(int riskScoreDelta) { this.riskScoreDelta = riskScoreDelta; }

    public String getKeyInsight() { return keyInsight; }
    public void setKeyInsight(String keyInsight) { this.keyInsight = keyInsight; }

    public String getSuggestedMitigation() { return suggestedMitigation; }
    public void setSuggestedMitigation(String suggestedMitigation) { this.suggestedMitigation = suggestedMitigation; }
}
