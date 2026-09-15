package com.sih.navisense.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardSummaryDto {
    // KPIs
    private double currentFreightIndex;
    private double forecast7DayUsd;
    private double forecast30DayUsd;
    private String recommendedCharterWindow;
    private BigDecimal estimatedTotalSavingsInr;
    private String estimatedTotalSavingsInrFormatted;
    private String activeRiskLevel;
    private int activeRiskScore;
    private double portCongestionIndex;
    private String recommendedVessel;
    private int decisionConfidence;

    // Command Center Statuses (STABLE, WATCH, ACTION_REQUIRED, HIGH_RISK)
    private String freightMarketStatus;
    private String fleetSuitabilityStatus;
    private String portOperationsStatus;
    private String riskLevelStatus;
    private String procurementStrategyStatus;

    // Collections
    private List<AlertDto> recentAlerts = new ArrayList<>();
    private List<RouteHeatmapDto> routeHeatmaps = new ArrayList<>();
    private List<CharterDecisionResponse> recentDecisions = new ArrayList<>();

    public DashboardSummaryDto() {}

    public double getCurrentFreightIndex() { return currentFreightIndex; }
    public void setCurrentFreightIndex(double currentFreightIndex) { this.currentFreightIndex = currentFreightIndex; }

    public double getForecast7DayUsd() { return forecast7DayUsd; }
    public void setForecast7DayUsd(double forecast7DayUsd) { this.forecast7DayUsd = forecast7DayUsd; }

    public double getForecast30DayUsd() { return forecast30DayUsd; }
    public void setForecast30DayUsd(double forecast30DayUsd) { this.forecast30DayUsd = forecast30DayUsd; }

    public String getRecommendedCharterWindow() { return recommendedCharterWindow; }
    public void setRecommendedCharterWindow(String recommendedCharterWindow) { this.recommendedCharterWindow = recommendedCharterWindow; }

    public BigDecimal getEstimatedTotalSavingsInr() { return estimatedTotalSavingsInr; }
    public void setEstimatedTotalSavingsInr(BigDecimal estimatedTotalSavingsInr) { this.estimatedTotalSavingsInr = estimatedTotalSavingsInr; }

    public String getEstimatedTotalSavingsInrFormatted() { return estimatedTotalSavingsInrFormatted; }
    public void setEstimatedTotalSavingsInrFormatted(String estimatedTotalSavingsInrFormatted) { this.estimatedTotalSavingsInrFormatted = estimatedTotalSavingsInrFormatted; }

    public String getActiveRiskLevel() { return activeRiskLevel; }
    public void setActiveRiskLevel(String activeRiskLevel) { this.activeRiskLevel = activeRiskLevel; }

    public int getActiveRiskScore() { return activeRiskScore; }
    public void setActiveRiskScore(int activeRiskScore) { this.activeRiskScore = activeRiskScore; }

    public double getPortCongestionIndex() { return portCongestionIndex; }
    public void setPortCongestionIndex(double portCongestionIndex) { this.portCongestionIndex = portCongestionIndex; }

    public String getRecommendedVessel() { return recommendedVessel; }
    public void setRecommendedVessel(String recommendedVessel) { this.recommendedVessel = recommendedVessel; }

    public int getDecisionConfidence() { return decisionConfidence; }
    public void setDecisionConfidence(int decisionConfidence) { this.decisionConfidence = decisionConfidence; }

    public String getFreightMarketStatus() { return freightMarketStatus; }
    public void setFreightMarketStatus(String freightMarketStatus) { this.freightMarketStatus = freightMarketStatus; }

    public String getFleetSuitabilityStatus() { return fleetSuitabilityStatus; }
    public void setFleetSuitabilityStatus(String fleetSuitabilityStatus) { this.fleetSuitabilityStatus = fleetSuitabilityStatus; }

    public String getPortOperationsStatus() { return portOperationsStatus; }
    public void setPortOperationsStatus(String portOperationsStatus) { this.portOperationsStatus = portOperationsStatus; }

    public String getRiskLevelStatus() { return riskLevelStatus; }
    public void setRiskLevelStatus(String riskLevelStatus) { this.riskLevelStatus = riskLevelStatus; }

    public String getProcurementStrategyStatus() { return procurementStrategyStatus; }
    public void setProcurementStrategyStatus(String procurementStrategyStatus) { this.procurementStrategyStatus = procurementStrategyStatus; }

    public List<AlertDto> getRecentAlerts() { return recentAlerts; }
    public void setRecentAlerts(List<AlertDto> recentAlerts) { this.recentAlerts = recentAlerts; }

    public List<RouteHeatmapDto> getRouteHeatmaps() { return routeHeatmaps; }
    public void setRouteHeatmaps(List<RouteHeatmapDto> routeHeatmaps) { this.routeHeatmaps = routeHeatmaps; }

    public List<CharterDecisionResponse> getRecentDecisions() { return recentDecisions; }
    public void setRecentDecisions(List<CharterDecisionResponse> recentDecisions) { this.recentDecisions = recentDecisions; }
}
