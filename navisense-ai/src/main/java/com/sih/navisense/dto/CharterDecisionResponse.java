package com.sih.navisense.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CharterDecisionResponse {

    private String fingerprint;
    private String primaryDecision; // BOOK_NOW, WAIT, MONITOR, SHORT_TERM_CONTRACT, etc.
    private String decisionBadgeClass;
    private String recommendedVessel;
    private String routeDescription;
    private String originCountry;
    private String loadingPort;
    private String destinationPort;
    private String recommendedContract;
    private double currentRateUsd;
    private double forecastRateUsd;
    private BigDecimal totalCostUsd;
    private BigDecimal totalCostInr;
    private String totalCostInrFormatted;
    private BigDecimal potentialSavingsInr;
    private String potentialSavingsInrFormatted;
    private double savingsPercentage;
    private int decisionConfidence; // 0 - 100
    private String riskLevel; // LOW, MEDIUM, HIGH, CRITICAL
    private String riskBadgeClass;
    private String bestCharterWindow;
    private String portCompatibilityStatus; // PASSED, WARNING, REJECTED
    private int voyageCount;
    private double capacityUtilizationPct;
    private double voyageDays;
    private double turnaroundHours;
    private double relativeCo2Index;
    private String relativeCo2Text;
    private String rationale;

    // Optional Budget & Expected Rate Comparison
    private Double budgetInr;
    private String budgetInrFormatted;
    private BigDecimal budgetVarianceInr;
    private String budgetVarianceInrFormatted;
    private String budgetStatus; // WITHIN_BUDGET, EXCEEDS_BUDGET, NOT_SPECIFIED
    private Double expectedFreightRateUsd;
    private Double rateVarianceVsExpectedUsd;

    private List<FactorContributionDto> positiveFactors = new ArrayList<>();
    private List<FactorContributionDto> negativeFactors = new ArrayList<>();
    private List<VesselRankingDto> vesselRankings = new ArrayList<>();
    private List<ContractOptionDto> contractOptions = new ArrayList<>();
    private List<PortAlternativeDto> portAlternatives = new ArrayList<>();
    private Map<String, Object> calculationBreakdown = new HashMap<>();

    public CharterDecisionResponse() {}

    public String getFingerprint() { return fingerprint; }
    public void setFingerprint(String fingerprint) { this.fingerprint = fingerprint; }

    public String getPrimaryDecision() { return primaryDecision; }
    public void setPrimaryDecision(String primaryDecision) { this.primaryDecision = primaryDecision; }

    public String getDecisionBadgeClass() { return decisionBadgeClass; }
    public void setDecisionBadgeClass(String decisionBadgeClass) { this.decisionBadgeClass = decisionBadgeClass; }

    public String getRecommendedVessel() { return recommendedVessel; }
    public void setRecommendedVessel(String recommendedVessel) { this.recommendedVessel = recommendedVessel; }

    public String getRouteDescription() { return routeDescription; }
    public void setRouteDescription(String routeDescription) { this.routeDescription = routeDescription; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public String getRecommendedContract() { return recommendedContract; }
    public void setRecommendedContract(String recommendedContract) { this.recommendedContract = recommendedContract; }

    public double getCurrentRateUsd() { return currentRateUsd; }
    public void setCurrentRateUsd(double currentRateUsd) { this.currentRateUsd = currentRateUsd; }

    public double getForecastRateUsd() { return forecastRateUsd; }
    public void setForecastRateUsd(double forecastRateUsd) { this.forecastRateUsd = forecastRateUsd; }

    public BigDecimal getTotalCostUsd() { return totalCostUsd; }
    public void setTotalCostUsd(BigDecimal totalCostUsd) { this.totalCostUsd = totalCostUsd; }

    public BigDecimal getTotalCostInr() { return totalCostInr; }
    public void setTotalCostInr(BigDecimal totalCostInr) { this.totalCostInr = totalCostInr; }

    public String getTotalCostInrFormatted() { return totalCostInrFormatted; }
    public void setTotalCostInrFormatted(String totalCostInrFormatted) { this.totalCostInrFormatted = totalCostInrFormatted; }

    public BigDecimal getPotentialSavingsInr() { return potentialSavingsInr; }
    public void setPotentialSavingsInr(BigDecimal potentialSavingsInr) { this.potentialSavingsInr = potentialSavingsInr; }

    public String getPotentialSavingsInrFormatted() { return potentialSavingsInrFormatted; }
    public void setPotentialSavingsInrFormatted(String potentialSavingsInrFormatted) { this.potentialSavingsInrFormatted = potentialSavingsInrFormatted; }

    public double getSavingsPercentage() { return savingsPercentage; }
    public void setSavingsPercentage(double savingsPercentage) { this.savingsPercentage = savingsPercentage; }

    public int getDecisionConfidence() { return decisionConfidence; }
    public void setDecisionConfidence(int decisionConfidence) { this.decisionConfidence = decisionConfidence; }

    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }

    public String getRiskBadgeClass() { return riskBadgeClass; }
    public void setRiskBadgeClass(String riskBadgeClass) { this.riskBadgeClass = riskBadgeClass; }

    public String getBestCharterWindow() { return bestCharterWindow; }
    public void setBestCharterWindow(String bestCharterWindow) { this.bestCharterWindow = bestCharterWindow; }

    public String getPortCompatibilityStatus() { return portCompatibilityStatus; }
    public void setPortCompatibilityStatus(String portCompatibilityStatus) { this.portCompatibilityStatus = portCompatibilityStatus; }

    public int getVoyageCount() { return voyageCount; }
    public void setVoyageCount(int voyageCount) { this.voyageCount = voyageCount; }

    public double getCapacityUtilizationPct() { return capacityUtilizationPct; }
    public void setCapacityUtilizationPct(double capacityUtilizationPct) { this.capacityUtilizationPct = capacityUtilizationPct; }

    public double getVoyageDays() { return voyageDays; }
    public void setVoyageDays(double voyageDays) { this.voyageDays = voyageDays; }

    public double getTurnaroundHours() { return turnaroundHours; }
    public void setTurnaroundHours(double turnaroundHours) { this.turnaroundHours = turnaroundHours; }

    public double getRelativeCo2Index() { return relativeCo2Index; }
    public void setRelativeCo2Index(double relativeCo2Index) { this.relativeCo2Index = relativeCo2Index; }

    public String getRelativeCo2Text() { return relativeCo2Text; }
    public void setRelativeCo2Text(String relativeCo2Text) { this.relativeCo2Text = relativeCo2Text; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public List<FactorContributionDto> getPositiveFactors() { return positiveFactors; }
    public void setPositiveFactors(List<FactorContributionDto> positiveFactors) { this.positiveFactors = positiveFactors; }

    public List<FactorContributionDto> getNegativeFactors() { return negativeFactors; }
    public void setNegativeFactors(List<FactorContributionDto> negativeFactors) { this.negativeFactors = negativeFactors; }

    public List<VesselRankingDto> getVesselRankings() { return vesselRankings; }
    public void setVesselRankings(List<VesselRankingDto> vesselRankings) { this.vesselRankings = vesselRankings; }

    public List<ContractOptionDto> getContractOptions() { return contractOptions; }
    public void setContractOptions(List<ContractOptionDto> contractOptions) { this.contractOptions = contractOptions; }

    public List<PortAlternativeDto> getPortAlternatives() { return portAlternatives; }
    public void setPortAlternatives(List<PortAlternativeDto> portAlternatives) { this.portAlternatives = portAlternatives; }

    public Map<String, Object> getCalculationBreakdown() { return calculationBreakdown; }
    public void setCalculationBreakdown(Map<String, Object> calculationBreakdown) { this.calculationBreakdown = calculationBreakdown; }

    public Double getBudgetInr() { return budgetInr; }
    public void setBudgetInr(Double budgetInr) { this.budgetInr = budgetInr; }

    public String getBudgetInrFormatted() { return budgetInrFormatted; }
    public void setBudgetInrFormatted(String budgetInrFormatted) { this.budgetInrFormatted = budgetInrFormatted; }

    public BigDecimal getBudgetVarianceInr() { return budgetVarianceInr; }
    public void setBudgetVarianceInr(BigDecimal budgetVarianceInr) { this.budgetVarianceInr = budgetVarianceInr; }

    public String getBudgetVarianceInrFormatted() { return budgetVarianceInrFormatted; }
    public void setBudgetVarianceInrFormatted(String budgetVarianceInrFormatted) { this.budgetVarianceInrFormatted = budgetVarianceInrFormatted; }

    public String getBudgetStatus() { return budgetStatus; }
    public void setBudgetStatus(String budgetStatus) { this.budgetStatus = budgetStatus; }

    public Double getExpectedFreightRateUsd() { return expectedFreightRateUsd; }
    public void setExpectedFreightRateUsd(Double expectedFreightRateUsd) { this.expectedFreightRateUsd = expectedFreightRateUsd; }

    public Double getRateVarianceVsExpectedUsd() { return rateVarianceVsExpectedUsd; }
    public void setRateVarianceVsExpectedUsd(Double rateVarianceVsExpectedUsd) { this.rateVarianceVsExpectedUsd = rateVarianceVsExpectedUsd; }
}
