package com.sih.navisense.service;

import com.sih.navisense.dto.*;
import com.sih.navisense.entity.CharterDecisionRecord;
import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.CharterDecisionRecordRepository;
import com.sih.navisense.repository.TradeRouteRepository;
import com.sih.navisense.repository.VesselTypeRepository;
import com.sih.navisense.util.CurrencyFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CharterIntelligenceService {

    private static final Logger log = LoggerFactory.getLogger(CharterIntelligenceService.class);
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    private final FreightForecastService forecastService;
    private final PortCompatibilityService compatibilityService;
    private final VesselOptimizationService vesselOptimizationService;
    private final VoyageCostService voyageCostService;
    private final RiskAssessmentService riskAssessmentService;
    private final ContractStrategyService contractStrategyService;
    private final IdleTimeOptimizationService idleTimeOptimizationService;
    private final TradeRouteRepository tradeRouteRepository;
    private final VesselTypeRepository vesselTypeRepository;
    private final CharterDecisionRecordRepository decisionRecordRepository;

    public CharterIntelligenceService(
            FreightForecastService forecastService,
            PortCompatibilityService compatibilityService,
            VesselOptimizationService vesselOptimizationService,
            VoyageCostService voyageCostService,
            RiskAssessmentService riskAssessmentService,
            ContractStrategyService contractStrategyService,
            IdleTimeOptimizationService idleTimeOptimizationService,
            TradeRouteRepository tradeRouteRepository,
            VesselTypeRepository vesselTypeRepository,
            CharterDecisionRecordRepository decisionRecordRepository) {
        this.forecastService = forecastService;
        this.compatibilityService = compatibilityService;
        this.vesselOptimizationService = vesselOptimizationService;
        this.voyageCostService = voyageCostService;
        this.riskAssessmentService = riskAssessmentService;
        this.contractStrategyService = contractStrategyService;
        this.idleTimeOptimizationService = idleTimeOptimizationService;
        this.tradeRouteRepository = tradeRouteRepository;
        this.vesselTypeRepository = vesselTypeRepository;
        this.decisionRecordRepository = decisionRecordRepository;
    }

    @Transactional
    public CharterDecisionResponse analyzeCharter(CharterAnalysisRequest request) {
        log.info("Starting Charter Intelligence analysis for cargo={}, qty={} t, route={} -> {}",
                request.getCargoType(), request.getQuantityTonnes(), request.getLoadingPort(), request.getDestinationPort());

        // 1. Resolve Route
        Optional<TradeRoute> routeOpt = tradeRouteRepository
                .findByOriginCountryIgnoreCaseAndLoadingPortIgnoreCaseAndDestinationPortIgnoreCase(
                        request.getOriginCountry(), request.getLoadingPort(), request.getDestinationPort());

        TradeRoute route = routeOpt.orElseGet(() -> {
            TradeRoute fallback = new TradeRoute();
            fallback.setOriginCountry(request.getOriginCountry());
            fallback.setLoadingPort(request.getLoadingPort());
            fallback.setDestinationPort(request.getDestinationPort());
            fallback.setDistanceNauticalMiles(5450.0);
            fallback.setTypicalVoyageDays(16.0);
            fallback.setRiskFactor(30.0);
            fallback.setSeasonalityFactor(1.05);
            fallback.setBaseFreightUsdPerTonne(24.50);
            return fallback;
        });

        // 2. Base Freight Forecasting
        String initialVessel = (request.getPreferredVessel() != null && !request.getPreferredVessel().isBlank())
                ? request.getPreferredVessel().trim() : "Panamax";

        ForecastSummaryDto forecastSummary = forecastService.generateForecast(
                request.getOriginCountry(), request.getDestinationPort(), initialVessel);

        double currentSpotRate = forecastSummary.getCurrentRateUsd();
        double forecast14dRate = forecastSummary.getForecastPoints().size() > 1
                ? forecastSummary.getForecastPoints().get(1).getPredictedRateUsd() : currentSpotRate;

        // 3. Vessel Optimization & Port Physical Compatibility
        List<VesselRankingDto> rankings = vesselOptimizationService.optimizeVesselSelection(
                request.getQuantityTonnes(), request.getOriginCountry(), request.getLoadingPort(),
                request.getDestinationPort(), currentSpotRate);

        // Select the top-ranked compatible vessel
        VesselRankingDto chosenVesselDto = rankings.stream()
                .filter(VesselRankingDto::isCompatible)
                .findFirst()
                .orElse(rankings.get(0));

        VesselType chosenVesselEntity = vesselTypeRepository.findByNameIgnoreCase(chosenVesselDto.getVesselName())
                .orElseGet(() -> vesselTypeRepository.findAll().get(0));

        // 4. Detailed Multi-Voyage Cost Engine
        double selectedVesselFreightRate = currentSpotRate * chosenVesselEntity.getCharterCostFactor();
        VoyageCostService.CostBreakdown costBreakdown = voyageCostService.calculateVoyageCost(
                request.getQuantityTonnes(), selectedVesselFreightRate, chosenVesselEntity, route, request.getDestinationPort());

        // 5. Risk Assessment
        RiskAssessmentService.RiskAssessmentResult risk = riskAssessmentService.evaluateRisk(
                request.getDestinationPort(),
                route.getRiskFactor(),
                forecastSummary.getAnnualizedVolatility(),
                chosenVesselDto.isCompatible(),
                costBreakdown.capacityUtilizationPct,
                14);

        // 6. Contract Strategy Comparison
        List<ContractOptionDto> contractStrategies = contractStrategyService.evaluateStrategies(
                costBreakdown.totalCostInr,
                forecastSummary.getAnnualizedVolatility(),
                forecastSummary.getPrimaryTrend(),
                risk.getOverallScore());

        ContractOptionDto recommendedContract = contractStrategies.stream()
                .filter(ContractOptionDto::isRecommended)
                .findFirst()
                .orElse(contractStrategies.get(0));

        // 7. Port Alternatives Assessment
        List<PortAlternativeDto> portAlternatives = idleTimeOptimizationService.evaluatePortAlternatives(
                request.getDestinationPort(), chosenVesselEntity, costBreakdown.totalCostInr);

        // 8. Primary Charter Decision Engine
        String primaryDecision;
        String badgeClass;
        double rateChangePct = ((forecast14dRate - currentSpotRate) / currentSpotRate) * 100.0;

        if (!chosenVesselDto.isCompatible()) {
            primaryDecision = "CHANGE_VESSEL";
            badgeClass = "badge-danger";
        } else if (risk.getCongestionScore() >= 65 && portAlternatives.stream().anyMatch(a -> "BETTER".equals(a.getComparisonVerdict()))) {
            primaryDecision = "ALTERNATIVE_PORT";
            badgeClass = "badge-warning";
        } else if (request.getQuantityTonnes() >= 220000.0 && costBreakdown.voyagesRequired >= 4) {
            primaryDecision = "SPLIT_CARGO";
            badgeClass = "badge-info";
        } else if (rateChangePct >= 2.0) {
            primaryDecision = "BOOK_NOW";
            badgeClass = "badge-success";
        } else if (rateChangePct <= -2.5 && risk.getOverallScore() < 65) {
            primaryDecision = "WAIT";
            badgeClass = "badge-info";
        } else if (recommendedContract.getStrategyName().contains("Hybrid")) {
            primaryDecision = "HYBRID_CONTRACT";
            badgeClass = "badge-info";
        } else if (recommendedContract.getStrategyName().contains("Medium-Term")) {
            primaryDecision = "MEDIUM_TERM_CONTRACT";
            badgeClass = "badge-success";
        } else if ("HIGH".equals(risk.getRiskCategory()) || "CRITICAL".equals(risk.getRiskCategory())) {
            primaryDecision = "SHORT_TERM_CONTRACT";
            badgeClass = "badge-warning";
        } else {
            primaryDecision = "MONITOR";
            badgeClass = "badge-secondary";
        }

        // 9. Explainability & Contribution Factors
        List<FactorContributionDto> positiveFactors = new ArrayList<>();
        List<FactorContributionDto> negativeFactors = new ArrayList<>();

        if (rateChangePct >= 0) {
            positiveFactors.add(new FactorContributionDto(
                    "Freight Trend Protection",
                    (int) Math.min(35, Math.round(20 + rateChangePct * 2)),
                    "POSITIVE",
                    String.format("Freight rates expected to rise %.1f%%; locking capacity avoids higher spot tariffs.", rateChangePct)
            ));
        } else {
            negativeFactors.add(new FactorContributionDto(
                    "Downward Freight Softening",
                    (int) Math.round(rateChangePct * 1.5),
                    "NEGATIVE",
                    String.format("Projected softening of %.1f%% suggests potential savings if chartering is delayed.", Math.abs(rateChangePct))
            ));
        }

        if (chosenVesselDto.isCompatible()) {
            positiveFactors.add(new FactorContributionDto(
                    "Port Physical Compatibility",
                    25,
                    "POSITIVE",
                    String.format("Fully clears draft (%.1fm) and LOA constraints at both %s and %s.",
                            chosenVesselEntity.getDraft(), request.getLoadingPort(), request.getDestinationPort())
            ));
        }

        if (costBreakdown.capacityUtilizationPct >= 80.0) {
            positiveFactors.add(new FactorContributionDto(
                    "Vessel Capacity Utilization",
                    22,
                    "POSITIVE",
                    String.format("Parcel yields %.1f%% deadweight utilization across %d voyage(s).",
                            costBreakdown.capacityUtilizationPct, costBreakdown.voyagesRequired)
            ));
        } else {
            negativeFactors.add(new FactorContributionDto(
                    "Sub-optimal Capacity Slack",
                    -12,
                    "NEGATIVE",
                    String.format("Capacity utilization is %.1f%%; consider adjusting parcel size to reduce deadweight loss.",
                            costBreakdown.capacityUtilizationPct)
            ));
        }

        if (risk.getCongestionScore() > 50) {
            negativeFactors.add(new FactorContributionDto(
                    "Port Berth Congestion Risk",
                    -14,
                    "NEGATIVE",
                    String.format("%s congestion index at %.0f/100, risking demurrage delays.",
                            request.getDestinationPort(), risk.getCongestionScore())
            ));
        }

        if (forecastSummary.getAnnualizedVolatility() > 20.0) {
            negativeFactors.add(new FactorContributionDto(
                    "Market Volatility Premium",
                    -10,
                    "NEGATIVE",
                    String.format("Annualized freight volatility of %.1f%% requires protective contracting.",
                            forecastSummary.getAnnualizedVolatility())
            ));
        }

        // Decision Confidence (0 - 100)
        int confidence = Math.max(60, Math.min(95,
                (int) Math.round(92.0 - (forecastSummary.getAnnualizedVolatility() * 0.25)
                        - (risk.getOverallScore() * 0.15) + (chosenVesselDto.isCompatible() ? 10 : -25))));

        // Dynamic "Why this recommendation?" paragraph
        String rationale = String.format(
                "%s is recommended because the selected cargo volume (%,.0f tonnes) achieves optimal capacity utilization (%.1f%%) across %d voyage(s) while strictly satisfying maximum draft, beam, and LOA navigation limits at both %s and %s. The freight model projects rates to shift from $%.2f to $%.2f/tonne over the next two weeks with %s volatility. Therefore, adopting a %s within the window of %s yields an estimated logistics benefit of %s compared to volatile spot procurement.",
                chosenVesselDto.getVesselName(),
                request.getQuantityTonnes(),
                costBreakdown.capacityUtilizationPct,
                costBreakdown.voyagesRequired,
                request.getLoadingPort(),
                request.getDestinationPort(),
                currentSpotRate,
                forecast14dRate,
                forecastSummary.getAnnualizedVolatility() > 20.0 ? "heightened" : "moderate",
                recommendedContract.getStrategyName(),
                forecastSummary.getRecommendedCharterWindow(),
                recommendedContract.getPotentialSavingInrFormatted()
        );

        // Generate Decision Fingerprint: NAV-26006-YYYYMMDD-SEQ
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String fingerprint = String.format("NAV-26006-%s-%03d", dateStr, SEQUENCE.getAndIncrement());

        // Build Response DTO
        CharterDecisionResponse response = new CharterDecisionResponse();
        response.setFingerprint(fingerprint);
        response.setPrimaryDecision(primaryDecision);
        response.setDecisionBadgeClass(badgeClass);
        response.setRecommendedVessel(chosenVesselDto.getVesselName());
        response.setRouteDescription(String.format("%s (%s) → %s (%s)",
                request.getOriginCountry(), request.getLoadingPort(), request.getDestinationPort(), route.getDestinationPort()));
        response.setOriginCountry(request.getOriginCountry());
        response.setLoadingPort(request.getLoadingPort());
        response.setDestinationPort(request.getDestinationPort());
        response.setRecommendedContract(recommendedContract.getStrategyName());
        response.setCurrentRateUsd(currentSpotRate);
        response.setForecastRateUsd(forecast14dRate);
        response.setTotalCostUsd(costBreakdown.totalCostUsd);
        response.setTotalCostInr(costBreakdown.totalCostInr);
        response.setTotalCostInrFormatted(costBreakdown.totalCostInrFormatted);
        response.setPotentialSavingsInr(recommendedContract.getPotentialSavingInr());
        response.setPotentialSavingsInrFormatted(recommendedContract.getPotentialSavingInrFormatted());

        double savingsPct = 0.0;
        if (costBreakdown.totalCostInr.compareTo(BigDecimal.ZERO) > 0) {
            savingsPct = recommendedContract.getPotentialSavingInr()
                    .divide(costBreakdown.totalCostInr, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100.0)).doubleValue();
        }
        response.setSavingsPercentage(Math.round(savingsPct * 10.0) / 10.0);

        response.setDecisionConfidence(confidence);
        response.setRiskLevel(risk.getRiskCategory());
        response.setRiskBadgeClass(risk.getBadgeClass());
        response.setBestCharterWindow(forecastSummary.getRecommendedCharterWindow());
        response.setPortCompatibilityStatus(chosenVesselDto.isCompatible() ? "PASSED" : "REJECTED");
        response.setVoyageCount(costBreakdown.voyagesRequired);
        response.setCapacityUtilizationPct(costBreakdown.capacityUtilizationPct);
        response.setVoyageDays(costBreakdown.voyageDaysOneWay);
        response.setTurnaroundHours(costBreakdown.turnaroundHours);
        response.setRelativeCo2Index(chosenVesselDto.getRelativeCo2Index());
        response.setRelativeCo2Text(String.format("Relative emissions index %.1f/100 (high deadweight utilization minimizes trips)", chosenVesselDto.getRelativeCo2Index()));
        response.setRationale(rationale);
        response.setPositiveFactors(positiveFactors);
        response.setNegativeFactors(negativeFactors);
        response.setVesselRankings(rankings);
        response.setContractOptions(contractStrategies);
        response.setPortAlternatives(portAlternatives);
        response.setCalculationBreakdown(costBreakdown.auditMap);

        // Optional Budget & Expected Freight Rate Handling
        if (request.getBudgetInr() != null && request.getBudgetInr() > 0) {
            BigDecimal budgetInr = BigDecimal.valueOf(request.getBudgetInr());
            response.setBudgetInr(request.getBudgetInr());
            response.setBudgetInrFormatted(CurrencyFormatter.formatInr(budgetInr));
            BigDecimal variance = budgetInr.subtract(costBreakdown.totalCostInr);
            response.setBudgetVarianceInr(variance);
            response.setBudgetVarianceInrFormatted(CurrencyFormatter.formatInr(variance.abs()));
            if (variance.compareTo(BigDecimal.ZERO) >= 0) {
                response.setBudgetStatus("WITHIN_BUDGET");
                positiveFactors.add(new FactorContributionDto(
                        "Budget Compliant Allocation",
                        15,
                        "POSITIVE",
                        String.format("Logistics spend of %s remains %s within target allocation.",
                                costBreakdown.totalCostInrFormatted, CurrencyFormatter.formatInr(variance))
                ));
            } else {
                response.setBudgetStatus("EXCEEDS_BUDGET");
                negativeFactors.add(new FactorContributionDto(
                        "Budget Overrun Deficit",
                        -15,
                        "NEGATIVE",
                        String.format("Logistics spend of %s exceeds target budget by %s.",
                                costBreakdown.totalCostInrFormatted, CurrencyFormatter.formatInr(variance.abs()))
                ));
            }
        } else {
            response.setBudgetStatus("NOT_SPECIFIED");
        }

        if (request.getExpectedFreightRateUsd() != null && request.getExpectedFreightRateUsd() > 0) {
            response.setExpectedFreightRateUsd(request.getExpectedFreightRateUsd());
            double rateVariance = selectedVesselFreightRate - request.getExpectedFreightRateUsd();
            response.setRateVarianceVsExpectedUsd(Math.round(rateVariance * 100.0) / 100.0);
        }

        // 10. Persist Decision Record for Traceability
        CharterDecisionRecord record = new CharterDecisionRecord();
        record.setFingerprint(fingerprint);
        record.setDecisionTimestamp(LocalDateTime.now());
        record.setCargoType(request.getCargoType());
        record.setQuantityTonnes(request.getQuantityTonnes());
        record.setOriginCountry(request.getOriginCountry());
        record.setLoadingPort(request.getLoadingPort());
        record.setDestinationPort(request.getDestinationPort());
        record.setRecommendedVessel(chosenVesselDto.getVesselName());
        record.setContractStrategy(recommendedContract.getStrategyName());
        record.setPrimaryDecision(primaryDecision);
        record.setEstimatedFreightRateUsd(currentSpotRate);
        record.setForecastFreightRateUsd(forecast14dRate);
        record.setTotalCostUsd(costBreakdown.totalCostUsd);
        record.setTotalCostInr(costBreakdown.totalCostInr);
        record.setPotentialSavingsInr(recommendedContract.getPotentialSavingInr());
        record.setConfidenceScore(confidence);
        record.setRiskLevel(risk.getRiskCategory());
        record.setRationale(rationale);
        record.setBestCharterWindow(forecastSummary.getRecommendedCharterWindow());
        if (request.getBudgetInr() != null && request.getBudgetInr() > 0) {
            record.setBudgetInr(BigDecimal.valueOf(request.getBudgetInr()));
        }

        decisionRecordRepository.save(record);
        log.info("Saved Charter Decision Record with fingerprint {}", fingerprint);

        return response;
    }

    public Optional<CharterDecisionResponse> getDecisionByFingerprint(String fingerprint) {
        return decisionRecordRepository.findByFingerprint(fingerprint)
                .map(rec -> {
                    CharterAnalysisRequest req = new CharterAnalysisRequest();
                    req.setCargoType(rec.getCargoType());
                    req.setQuantityTonnes(rec.getQuantityTonnes());
                    req.setOriginCountry(rec.getOriginCountry());
                    req.setLoadingPort(rec.getLoadingPort());
                    req.setDestinationPort(rec.getDestinationPort());
                    req.setPreferredVessel(rec.getRecommendedVessel());
                    return analyzeCharter(req);
                });
    }
}
