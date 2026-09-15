package com.sih.navisense.service;

import com.sih.navisense.provider.CongestionDataProvider;
import com.sih.navisense.provider.MarketDataProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RiskAssessmentService {

    private final MarketDataProvider marketDataProvider;
    private final CongestionDataProvider congestionDataProvider;

    private final double weightVolatility;
    private final double weightCongestion;
    private final double weightCompatibility;
    private final double weightRoute;
    private final double weightUncertainty;
    private final double weightIdleTime;

    public RiskAssessmentService(
            MarketDataProvider marketDataProvider,
            CongestionDataProvider congestionDataProvider,
            @Value("${navisense.risk.weights.volatility:0.25}") double weightVolatility,
            @Value("${navisense.risk.weights.congestion:0.25}") double weightCongestion,
            @Value("${navisense.risk.weights.compatibility:0.15}") double weightCompatibility,
            @Value("${navisense.risk.weights.route:0.15}") double weightRoute,
            @Value("${navisense.risk.weights.uncertainty:0.10}") double weightUncertainty,
            @Value("${navisense.risk.weights.idle-time:0.10}") double weightIdleTime) {
        this.marketDataProvider = marketDataProvider;
        this.congestionDataProvider = congestionDataProvider;
        this.weightVolatility = weightVolatility;
        this.weightCongestion = weightCongestion;
        this.weightCompatibility = weightCompatibility;
        this.weightRoute = weightRoute;
        this.weightUncertainty = weightUncertainty;
        this.weightIdleTime = weightIdleTime;
    }

    public static class RiskAssessmentResult {
        private final int overallScore;
        private final String riskCategory; // LOW, MEDIUM, HIGH, CRITICAL
        private final String badgeClass;
        private final String primaryReason;
        private final String secondaryReason;
        private final String mitigationAdvice;
        private final double volatilityScore;
        private final double congestionScore;
        private final double routeScore;

        public RiskAssessmentResult(int overallScore, String riskCategory, String badgeClass,
                                    String primaryReason, String secondaryReason, String mitigationAdvice,
                                    double volatilityScore, double congestionScore, double routeScore) {
            this.overallScore = overallScore;
            this.riskCategory = riskCategory;
            this.badgeClass = badgeClass;
            this.primaryReason = primaryReason;
            this.secondaryReason = secondaryReason;
            this.mitigationAdvice = mitigationAdvice;
            this.volatilityScore = volatilityScore;
            this.congestionScore = congestionScore;
            this.routeScore = routeScore;
        }

        public int getOverallScore() { return overallScore; }
        public String getRiskCategory() { return riskCategory; }
        public String getBadgeClass() { return badgeClass; }
        public String getPrimaryReason() { return primaryReason; }
        public String getSecondaryReason() { return secondaryReason; }
        public String getMitigationAdvice() { return mitigationAdvice; }
        public double getVolatilityScore() { return volatilityScore; }
        public double getCongestionScore() { return congestionScore; }
        public double getRouteScore() { return routeScore; }
    }

    public RiskAssessmentResult evaluateRisk(
            String destinationPort,
            double routeRiskFactor,
            double historicalVolatility,
            boolean isVesselCompatible,
            double capacityUtilization,
            int forecastHorizonDays) {

        // 1. Volatility factor (0 - 100)
        double volScore = Math.min(100.0, (historicalVolatility / 30.0) * 100.0);

        // 2. Port Congestion factor (0 - 100)
        double congScore = congestionDataProvider.getCongestionIndex(destinationPort);

        // 3. Compatibility & Capacity factor
        double compatScore = isVesselCompatible ? Math.max(10.0, 100.0 - capacityUtilization) : 100.0;

        // 4. Route geopolitical & weather factor (0 - 100)
        double rScore = Math.min(100.0, routeRiskFactor);

        // 5. Forecast uncertainty factor
        double uncertScore = Math.min(100.0, (forecastHorizonDays / 90.0) * 80.0 + 20.0);

        // 6. Idle time risk
        double idleScore = (congScore * 0.6) + (capacityUtilization < 70 ? 40.0 : 10.0);

        // Composite Weighted Risk Score
        double composite = (volScore * weightVolatility) +
                           (congScore * weightCongestion) +
                           (compatScore * weightCompatibility) +
                           (rScore * weightRoute) +
                           (uncertScore * weightUncertainty) +
                           (idleScore * weightIdleTime);

        int finalScore = Math.max(10, Math.min(99, (int) Math.round(composite)));

        String category;
        String badgeClass;
        if (finalScore <= 35) {
            category = "LOW";
            badgeClass = "bg-success";
        } else if (finalScore <= 65) {
            category = "MEDIUM";
            badgeClass = "bg-warning text-dark";
        } else if (finalScore <= 85) {
            category = "HIGH";
            badgeClass = "bg-danger";
        } else {
            category = "CRITICAL";
            badgeClass = "bg-dark text-white border border-danger";
        }

        // Identify Primary and Secondary Driver
        String primary;
        String secondary;
        String mitigation;

        if (volScore >= congScore && volScore >= rScore) {
            primary = String.format("Freight market volatility is elevated (%.1f%% annualized).", historicalVolatility);
            secondary = String.format("Destination port (%s) congestion index is at %.0f/100.", destinationPort, congScore);
            mitigation = "Secure volume under a short-term multi-voyage contract (COA) to cap upside freight rate exposure while preserving booking agility.";
        } else if (congScore >= volScore && congScore >= rScore) {
            primary = String.format("Destination port (%s) berth congestion is above baseline (index %.0f/100).", destinationPort, congScore);
            secondary = String.format("Potential vessel turnaround delay and demurrage risk.", destinationPort);
            mitigation = "Synchronize laycan windows with port authority or evaluate nearby alternative deepwater ports (e.g. Gangavaram or Dhamra).";
        } else {
            primary = String.format("Trade corridor transit exposure and maritime distance risk (factor %.0f/100).", rScore);
            secondary = "Longer transit window increases bunker price and scheduling sensitivity.";
            mitigation = "Implement bunker fuel escalation clauses and stagger vessel parcel departures.";
        }

        return new RiskAssessmentResult(finalScore, category, badgeClass, primary, secondary, mitigation,
                Math.round(volScore * 10.0) / 10.0, Math.round(congScore * 10.0) / 10.0, Math.round(rScore * 10.0) / 10.0);
    }
}
