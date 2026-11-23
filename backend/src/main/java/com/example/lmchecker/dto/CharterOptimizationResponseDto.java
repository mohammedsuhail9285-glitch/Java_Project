package com.example.lmchecker.dto;

public class CharterOptimizationResponseDto {

    private String planReference;
    private String recommendedVesselClass;
    private String charterDecision; // CHARTER_NOW, HOLD_WAIT, SPLIT_COA
    private Double spotRateUsd;
    private Double predictedFreightUsdTon;
    private Double bunkerAdjustmentUsdTon;
    private Double portChargesUsdTon;
    private Double demurrageRiskUsdTon;
    private Double totalLandedCostUsdTon;
    private Double totalLandedCostInrTon;
    private Double estimatedCostSavingsInr;
    private String decisionRationale;
    private String portCompatibilityNotice;

    // Dimensional constraints (Draft, LOA, Beam)
    private Double portDraftMeters;
    private Double vesselMaxDraftMeters;
    private boolean draftFeasible;

    private Double portMaxLoaMeters;
    private Double vesselLoaMeters;
    private boolean loaFeasible;

    private Double portMaxBeamMeters;
    private Double vesselBeamMeters;
    private boolean beamFeasible;

    // Spot vs COA Comparison
    private String contractType = "SPOT";
    private Double spotFreightUsdTon;
    private Double coaFreightUsdTon;
    private Double coaDiscountPct = 0.0;
    private Double spotTotalLandedInrTon;
    private Double coaTotalLandedInrTon;
    private Double coaNetSavingsInr;
    private String coaSuitabilityRecommendation;

    // Haldia Shallow Draft & Sagar-Sandheads Lighterage
    private boolean lighterageRequired;
    private String lighterageAnchorage;
    private Double lighteredQuantityTons = 0.0;
    private Double directDischargeQuantityTons = 0.0;
    private Double lighterageCostUsdTon = 0.0;
    private String lighterageOperationsNotes;

    // Berth Wait, Demurrage & Alternative Port
    private Double estimatedBerthWaitDays;
    private Double dailyDemurrageRateUsd;
    private Double projectedDemurrageUsd;
    private String demurrageRiskLevel = "LOW"; // LOW, MODERATE, HIGH, CRITICAL
    private String alternativePortRecommendation;

    // Early Warning Alerts & Model Traceability
    private String volatilityAlertLevel = "NORMAL"; // NORMAL, ELEVATED, HIGH_VOLATILITY
    private String earlyWarningMessage;
    private String confidenceInterval95;
    private String dataSource = "HISTORICAL_BALTIC_INDEX & LIVE_PORT_TELEMETRY";

    public CharterOptimizationResponseDto() {}

    public String getPlanReference() { return planReference; }
    public void setPlanReference(String planReference) { this.planReference = planReference; }

    public String getRecommendedVesselClass() { return recommendedVesselClass; }
    public void setRecommendedVesselClass(String recommendedVesselClass) { this.recommendedVesselClass = recommendedVesselClass; }

    public String getCharterDecision() { return charterDecision; }
    public void setCharterDecision(String charterDecision) { this.charterDecision = charterDecision; }

    public Double getSpotRateUsd() { return spotRateUsd; }
    public void setSpotRateUsd(Double spotRateUsd) { this.spotRateUsd = spotRateUsd; }

    public Double getPredictedFreightUsdTon() { return predictedFreightUsdTon; }
    public void setPredictedFreightUsdTon(Double predictedFreightUsdTon) { this.predictedFreightUsdTon = predictedFreightUsdTon; }

    public Double getBunkerAdjustmentUsdTon() { return bunkerAdjustmentUsdTon; }
    public void setBunkerAdjustmentUsdTon(Double bunkerAdjustmentUsdTon) { this.bunkerAdjustmentUsdTon = bunkerAdjustmentUsdTon; }

    public Double getPortChargesUsdTon() { return portChargesUsdTon; }
    public void setPortChargesUsdTon(Double portChargesUsdTon) { this.portChargesUsdTon = portChargesUsdTon; }

    public Double getDemurrageRiskUsdTon() { return demurrageRiskUsdTon; }
    public void setDemurrageRiskUsdTon(Double demurrageRiskUsdTon) { this.demurrageRiskUsdTon = demurrageRiskUsdTon; }

    public Double getTotalLandedCostUsdTon() { return totalLandedCostUsdTon; }
    public void setTotalLandedCostUsdTon(Double totalLandedCostUsdTon) { this.totalLandedCostUsdTon = totalLandedCostUsdTon; }

    public Double getTotalLandedCostInrTon() { return totalLandedCostInrTon; }
    public void setTotalLandedCostInrTon(Double totalLandedCostInrTon) { this.totalLandedCostInrTon = totalLandedCostInrTon; }

    public Double getEstimatedCostSavingsInr() { return estimatedCostSavingsInr; }
    public void setEstimatedCostSavingsInr(Double estimatedCostSavingsInr) { this.estimatedCostSavingsInr = estimatedCostSavingsInr; }

    public String getDecisionRationale() { return decisionRationale; }
    public void setDecisionRationale(String decisionRationale) { this.decisionRationale = decisionRationale; }

    public String getPortCompatibilityNotice() { return portCompatibilityNotice; }
    public void setPortCompatibilityNotice(String portCompatibilityNotice) { this.portCompatibilityNotice = portCompatibilityNotice; }

    public Double getPortDraftMeters() { return portDraftMeters; }
    public void setPortDraftMeters(Double portDraftMeters) { this.portDraftMeters = portDraftMeters; }

    public Double getVesselMaxDraftMeters() { return vesselMaxDraftMeters; }
    public void setVesselMaxDraftMeters(Double vesselMaxDraftMeters) { this.vesselMaxDraftMeters = vesselMaxDraftMeters; }

    public boolean isDraftFeasible() { return draftFeasible; }
    public void setDraftFeasible(boolean draftFeasible) { this.draftFeasible = draftFeasible; }

    public Double getPortMaxLoaMeters() { return portMaxLoaMeters; }
    public void setPortMaxLoaMeters(Double portMaxLoaMeters) { this.portMaxLoaMeters = portMaxLoaMeters; }

    public Double getVesselLoaMeters() { return vesselLoaMeters; }
    public void setVesselLoaMeters(Double vesselLoaMeters) { this.vesselLoaMeters = vesselLoaMeters; }

    public boolean isLoaFeasible() { return loaFeasible; }
    public void setLoaFeasible(boolean loaFeasible) { this.loaFeasible = loaFeasible; }

    public Double getPortMaxBeamMeters() { return portMaxBeamMeters; }
    public void setPortMaxBeamMeters(Double portMaxBeamMeters) { this.portMaxBeamMeters = portMaxBeamMeters; }

    public Double getVesselBeamMeters() { return vesselBeamMeters; }
    public void setVesselBeamMeters(Double vesselBeamMeters) { this.vesselBeamMeters = vesselBeamMeters; }

    public boolean isBeamFeasible() { return beamFeasible; }
    public void setBeamFeasible(boolean beamFeasible) { this.beamFeasible = beamFeasible; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public Double getSpotFreightUsdTon() { return spotFreightUsdTon; }
    public void setSpotFreightUsdTon(Double spotFreightUsdTon) { this.spotFreightUsdTon = spotFreightUsdTon; }

    public Double getCoaFreightUsdTon() { return coaFreightUsdTon; }
    public void setCoaFreightUsdTon(Double coaFreightUsdTon) { this.coaFreightUsdTon = coaFreightUsdTon; }

    public Double getCoaDiscountPct() { return coaDiscountPct; }
    public void setCoaDiscountPct(Double coaDiscountPct) { this.coaDiscountPct = coaDiscountPct; }

    public Double getSpotTotalLandedInrTon() { return spotTotalLandedInrTon; }
    public void setSpotTotalLandedInrTon(Double spotTotalLandedInrTon) { this.spotTotalLandedInrTon = spotTotalLandedInrTon; }

    public Double getCoaTotalLandedInrTon() { return coaTotalLandedInrTon; }
    public void setCoaTotalLandedInrTon(Double coaTotalLandedInrTon) { this.coaTotalLandedInrTon = coaTotalLandedInrTon; }

    public Double getCoaNetSavingsInr() { return coaNetSavingsInr; }
    public void setCoaNetSavingsInr(Double coaNetSavingsInr) { this.coaNetSavingsInr = coaNetSavingsInr; }

    public String getCoaSuitabilityRecommendation() { return coaSuitabilityRecommendation; }
    public void setCoaSuitabilityRecommendation(String coaSuitabilityRecommendation) { this.coaSuitabilityRecommendation = coaSuitabilityRecommendation; }

    public boolean isLighterageRequired() { return lighterageRequired; }
    public void setLighterageRequired(boolean lighterageRequired) { this.lighterageRequired = lighterageRequired; }

    public String getLighterageAnchorage() { return lighterageAnchorage; }
    public void setLighterageAnchorage(String lighterageAnchorage) { this.lighterageAnchorage = lighterageAnchorage; }

    public Double getLighteredQuantityTons() { return lighteredQuantityTons; }
    public void setLighteredQuantityTons(Double lighteredQuantityTons) { this.lighteredQuantityTons = lighteredQuantityTons; }

    public Double getDirectDischargeQuantityTons() { return directDischargeQuantityTons; }
    public void setDirectDischargeQuantityTons(Double directDischargeQuantityTons) { this.directDischargeQuantityTons = directDischargeQuantityTons; }

    public Double getLighterageCostUsdTon() { return lighterageCostUsdTon; }
    public void setLighterageCostUsdTon(Double lighterageCostUsdTon) { this.lighterageCostUsdTon = lighterageCostUsdTon; }

    public String getLighterageOperationsNotes() { return lighterageOperationsNotes; }
    public void setLighterageOperationsNotes(String lighterageOperationsNotes) { this.lighterageOperationsNotes = lighterageOperationsNotes; }

    public Double getEstimatedBerthWaitDays() { return estimatedBerthWaitDays; }
    public void setEstimatedBerthWaitDays(Double estimatedBerthWaitDays) { this.estimatedBerthWaitDays = estimatedBerthWaitDays; }

    public Double getDailyDemurrageRateUsd() { return dailyDemurrageRateUsd; }
    public void setDailyDemurrageRateUsd(Double dailyDemurrageRateUsd) { this.dailyDemurrageRateUsd = dailyDemurrageRateUsd; }

    public Double getProjectedDemurrageUsd() { return projectedDemurrageUsd; }
    public void setProjectedDemurrageUsd(Double projectedDemurrageUsd) { this.projectedDemurrageUsd = projectedDemurrageUsd; }

    public String getDemurrageRiskLevel() { return demurrageRiskLevel; }
    public void setDemurrageRiskLevel(String demurrageRiskLevel) { this.demurrageRiskLevel = demurrageRiskLevel; }

    public String getAlternativePortRecommendation() { return alternativePortRecommendation; }
    public void setAlternativePortRecommendation(String alternativePortRecommendation) { this.alternativePortRecommendation = alternativePortRecommendation; }

    public String getVolatilityAlertLevel() { return volatilityAlertLevel; }
    public void setVolatilityAlertLevel(String volatilityAlertLevel) { this.volatilityAlertLevel = volatilityAlertLevel; }

    public String getEarlyWarningMessage() { return earlyWarningMessage; }
    public void setEarlyWarningMessage(String earlyWarningMessage) { this.earlyWarningMessage = earlyWarningMessage; }

    public String getConfidenceInterval95() { return confidenceInterval95; }
    public void setConfidenceInterval95(String confidenceInterval95) { this.confidenceInterval95 = confidenceInterval95; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }
}
