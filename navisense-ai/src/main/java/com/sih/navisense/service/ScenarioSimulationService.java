package com.sih.navisense.service;

import com.sih.navisense.dto.CharterAnalysisRequest;
import com.sih.navisense.dto.CharterDecisionResponse;
import com.sih.navisense.dto.SimulationRequestDto;
import com.sih.navisense.dto.SimulationResultDto;
import com.sih.navisense.util.CurrencyFormatter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ScenarioSimulationService {

    private final CharterIntelligenceService charterService;

    public ScenarioSimulationService(CharterIntelligenceService charterService) {
        this.charterService = charterService;
    }

    public SimulationResultDto simulateScenario(SimulationRequestDto sim) {
        // 1. Base Scenario Execution
        CharterAnalysisRequest baseReq = new CharterAnalysisRequest();
        baseReq.setOriginCountry(sim.getOriginCountry());
        baseReq.setLoadingPort(sim.getLoadingPort());
        baseReq.setDestinationPort(sim.getDestinationPort());
        baseReq.setQuantityTonnes(sim.getCargoQuantityTonnes());
        baseReq.setPreferredVessel(sim.getVesselClass());

        CharterDecisionResponse baseResult = charterService.analyzeCharter(baseReq);

        // 2. Simulated Scenario Adjustments
        double simulatedFreight = baseResult.getCurrentRateUsd() * (1.0 + (sim.getFreightChangePct() / 100.0));
        simulatedFreight = Math.max(5.0, Math.round(simulatedFreight * 100.0) / 100.0);

        // Simulate cost modifier
        double costMultiplier = 1.0 + (sim.getFreightChangePct() / 100.0) * 0.70
                                    + (sim.getFuelPriceChangePct() / 100.0) * 0.18
                                    + (sim.getCongestionChangePct() / 100.0) * 0.12;

        BigDecimal simTotalCostInr = baseResult.getTotalCostInr()
                .multiply(BigDecimal.valueOf(costMultiplier))
                .setScale(2, RoundingMode.HALF_UP);

        // Adjusted Risk
        int simRiskScore = Math.max(10, Math.min(99,
                (int) Math.round(50.0 + (sim.getFreightChangePct() * 0.4)
                                      + (sim.getCongestionChangePct() * 0.3)
                                      + (sim.getDelayDays() * 2.5))));

        String simRiskLevel;
        if (simRiskScore <= 35) simRiskLevel = "LOW";
        else if (simRiskScore <= 65) simRiskLevel = "MEDIUM";
        else if (simRiskScore <= 85) simRiskLevel = "HIGH";
        else simRiskLevel = "CRITICAL";

        String simDecision;
        if (sim.getFreightChangePct() > 8.0) {
            simDecision = "BOOK_NOW";
        } else if (sim.getFreightChangePct() < -5.0 && simRiskScore < 60) {
            simDecision = "WAIT";
        } else if (simRiskScore >= 70) {
            simDecision = "SHORT_TERM_CONTRACT";
        } else {
            simDecision = "MONITOR";
        }

        // Cost Delta
        BigDecimal costDelta = simTotalCostInr.subtract(baseResult.getTotalCostInr());
        double deltaPct = 0.0;
        if (baseResult.getTotalCostInr().compareTo(BigDecimal.ZERO) > 0) {
            deltaPct = costDelta.divide(baseResult.getTotalCostInr(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100.0)).doubleValue();
        }

        SimulationResultDto dto = new SimulationResultDto();
        dto.setScenarioTitle(String.format("What-If: %+.1f%% Freight, %+.1f%% Congestion, +%d days delay",
                sim.getFreightChangePct(), sim.getCongestionChangePct(), sim.getDelayDays()));

        // Base Fields
        dto.setBaseFreightRateUsd(baseResult.getCurrentRateUsd());
        dto.setBaseTotalCostInr(baseResult.getTotalCostInr());
        dto.setBaseTotalCostInrFormatted(baseResult.getTotalCostInrFormatted());
        dto.setBaseRiskLevel(baseResult.getRiskLevel());
        dto.setBaseRiskScore(55);
        dto.setBaseRecommendedDecision(baseResult.getPrimaryDecision());
        dto.setBaseRecommendedVessel(baseResult.getRecommendedVessel());

        // Simulated Fields
        dto.setSimulatedFreightRateUsd(simulatedFreight);
        dto.setSimulatedTotalCostInr(simTotalCostInr);
        dto.setSimulatedTotalCostInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(simTotalCostInr));
        dto.setSimulatedRiskLevel(simRiskLevel);
        dto.setSimulatedRiskScore(simRiskScore);
        dto.setSimulatedRecommendedDecision(simDecision);
        dto.setSimulatedRecommendedVessel(baseResult.getRecommendedVessel());

        // Deltas & Insights
        dto.setCostDeltaInr(costDelta);
        dto.setCostDeltaInrFormatted((costDelta.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "")
                + CurrencyFormatter.formatInrCroresOrLakhs(costDelta));
        dto.setCostDeltaPercentage(Math.round(deltaPct * 10.0) / 10.0);
        dto.setRiskScoreDelta(simRiskScore - 55);

        if (costDelta.compareTo(BigDecimal.ZERO) > 0) {
            dto.setKeyInsight(String.format("Escalation scenario increases total SAIL procurement outlay by %s (%+.1f%%) due to market tightening.",
                    CurrencyFormatter.formatInrCroresOrLakhs(costDelta), deltaPct));
            dto.setSuggestedMitigation("Hedge against potential inflation by locking in Short-Term Contract capacity before market rate revision.");
        } else {
            dto.setKeyInsight(String.format("Softening scenario yields savings of %s (%+.1f%%) if procurement is timed with index dips.",
                    CurrencyFormatter.formatInrCroresOrLakhs(costDelta.abs()), deltaPct));
            dto.setSuggestedMitigation("Maintain spot procurement agility or negotiate index-linked hybrid contracts.");
        }

        return dto;
    }
}
