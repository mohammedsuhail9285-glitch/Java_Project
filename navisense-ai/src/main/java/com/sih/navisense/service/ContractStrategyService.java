package com.sih.navisense.service;

import com.sih.navisense.dto.ContractOptionDto;
import com.sih.navisense.util.CurrencyFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class ContractStrategyService {

    private final double shortTermDiscount;
    private final double mediumTermDiscount;

    public ContractStrategyService(
            @Value("${navisense.contract.short-term-discount:0.045}") double shortTermDiscount,
            @Value("${navisense.contract.medium-term-discount:0.080}") double mediumTermDiscount) {
        this.shortTermDiscount = shortTermDiscount;
        this.mediumTermDiscount = mediumTermDiscount;
    }

    public List<ContractOptionDto> evaluateStrategies(
            BigDecimal baselineSpotCostInr,
            double marketVolatility,
            String trendDirection,
            int overallRiskScore) {

        List<ContractOptionDto> options = new ArrayList<>();
        if (baselineSpotCostInr == null || baselineSpotCostInr.compareTo(BigDecimal.ZERO) <= 0) {
            return options;
        }

        // 1. Spot Charter Option
        ContractOptionDto spot = new ContractOptionDto();
        spot.setStrategyName("Spot Charter");
        spot.setExpectedCostInr(baselineSpotCostInr);
        spot.setExpectedCostInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(baselineSpotCostInr));
        spot.setPotentialSavingInr(BigDecimal.ZERO);
        spot.setPotentialSavingInrFormatted("₹0.00");
        spot.setRiskLevel("HIGH");
        spot.setVolatilityExposure("HIGH");
        spot.setFlexibility("HIGH");
        spot.setDescription("Single voyage commitment at prevailing market rates; full exposure to freight volatility and port laycan surges.");

        // 2. Short-Term Multi-Voyage Contract (COA 3-6 months)
        BigDecimal shortTermSaving = baselineSpotCostInr.multiply(BigDecimal.valueOf(shortTermDiscount))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal shortTermCost = baselineSpotCostInr.subtract(shortTermSaving);

        ContractOptionDto shortTerm = new ContractOptionDto();
        shortTerm.setStrategyName("Short-Term Multi-Voyage Contract");
        shortTerm.setExpectedCostInr(shortTermCost);
        shortTerm.setExpectedCostInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(shortTermCost));
        shortTerm.setPotentialSavingInr(shortTermSaving);
        shortTerm.setPotentialSavingInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(shortTermSaving));
        shortTerm.setRiskLevel("MEDIUM");
        shortTerm.setVolatilityExposure("MODERATE");
        shortTerm.setFlexibility("MODERATE");
        shortTerm.setDescription("Locked freight rate with bunker adjustment factor (BAF) across 2–4 consecutive voyages; guarantees tonnage supply.");

        // 3. Medium-Term Multi-Voyage Contract (COA 6-12 months)
        BigDecimal mediumTermSaving = baselineSpotCostInr.multiply(BigDecimal.valueOf(mediumTermDiscount))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal mediumTermCost = baselineSpotCostInr.subtract(mediumTermSaving);

        ContractOptionDto mediumTerm = new ContractOptionDto();
        mediumTerm.setStrategyName("Medium-Term Annual Contract (COA)");
        mediumTerm.setExpectedCostInr(mediumTermCost);
        mediumTerm.setExpectedCostInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(mediumTermCost));
        mediumTerm.setPotentialSavingInr(mediumTermSaving);
        mediumTerm.setPotentialSavingInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(mediumTermSaving));
        mediumTerm.setRiskLevel("LOW");
        mediumTerm.setVolatilityExposure("LOW");
        mediumTerm.setFlexibility("LOW");
        mediumTerm.setDescription("Fixed carrier term commitment offering maximum rate discount but limited flexibility if market softens.");

        // 4. Hybrid Strategic Allocation
        double fixedSplit = 0.60;
        double spotSplit = 0.40;
        if (marketVolatility > 22.0 || "UPWARD".equalsIgnoreCase(trendDirection)) {
            fixedSplit = 0.70;
            spotSplit = 0.30;
        } else if ("DOWNWARD".equalsIgnoreCase(trendDirection)) {
            fixedSplit = 0.40;
            spotSplit = 0.60;
        }

        BigDecimal hybridCost = shortTermCost.multiply(BigDecimal.valueOf(fixedSplit))
                .add(baselineSpotCostInr.multiply(BigDecimal.valueOf(spotSplit)))
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal hybridSaving = baselineSpotCostInr.subtract(hybridCost).setScale(2, RoundingMode.HALF_UP);

        ContractOptionDto hybrid = new ContractOptionDto();
        int fixedPct = (int) Math.round(fixedSplit * 100);
        int spotPct = 100 - fixedPct;
        hybrid.setStrategyName(String.format("Hybrid Contract (%d%% Fixed / %d%% Spot)", fixedPct, spotPct));
        hybrid.setExpectedCostInr(hybridCost);
        hybrid.setExpectedCostInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(hybridCost));
        hybrid.setPotentialSavingInr(hybridSaving);
        hybrid.setPotentialSavingInrFormatted(CurrencyFormatter.formatInrCroresOrLakhs(hybridSaving));
        hybrid.setRiskLevel("MEDIUM-LOW");
        hybrid.setVolatilityExposure("BALANCED");
        hybrid.setFlexibility("BALANCED");
        hybrid.setDescription(String.format("Binds %d%% volume under guaranteed freight rates while keeping %d%% spot flexibility to capture rate dips.", fixedPct, spotPct));

        // Score strategies
        if ("UPWARD".equalsIgnoreCase(trendDirection)) {
            shortTerm.setSuitabilityScore(94);
            hybrid.setSuitabilityScore(88);
            mediumTerm.setSuitabilityScore(85);
            spot.setSuitabilityScore(48);
            shortTerm.setRecommended(true);
        } else if ("DOWNWARD".equalsIgnoreCase(trendDirection)) {
            hybrid.setSuitabilityScore(92);
            spot.setSuitabilityScore(82);
            shortTerm.setSuitabilityScore(78);
            mediumTerm.setSuitabilityScore(65);
            hybrid.setRecommended(true);
        } else {
            // Stable / Neutral
            shortTerm.setSuitabilityScore(90);
            hybrid.setSuitabilityScore(86);
            mediumTerm.setSuitabilityScore(80);
            spot.setSuitabilityScore(55);
            shortTerm.setRecommended(true);
        }

        options.add(shortTerm);
        options.add(hybrid);
        options.add(mediumTerm);
        options.add(spot);

        return options;
    }
}
