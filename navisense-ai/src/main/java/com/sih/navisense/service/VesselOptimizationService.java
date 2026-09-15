package com.sih.navisense.service;

import com.sih.navisense.dto.VesselRankingDto;
import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.repository.TradeRouteRepository;
import com.sih.navisense.repository.VesselTypeRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class VesselOptimizationService {

    private final VesselTypeRepository vesselTypeRepository;
    private final PortCompatibilityService compatibilityService;
    private final VoyageCostService voyageCostService;
    private final TradeRouteRepository tradeRouteRepository;

    public VesselOptimizationService(
            VesselTypeRepository vesselTypeRepository,
            PortCompatibilityService compatibilityService,
            VoyageCostService voyageCostService,
            TradeRouteRepository tradeRouteRepository) {
        this.vesselTypeRepository = vesselTypeRepository;
        this.compatibilityService = compatibilityService;
        this.voyageCostService = voyageCostService;
        this.tradeRouteRepository = tradeRouteRepository;
    }

    public List<VesselRankingDto> optimizeVesselSelection(
            double cargoTonnes,
            String originCountry,
            String loadingPortName,
            String destinationPortName,
            double baseFreightRateUsd) {

        List<VesselType> allVessels = vesselTypeRepository.findAll();
        Optional<TradeRoute> routeOpt = tradeRouteRepository.findByLoadingPortIgnoreCaseAndDestinationPortIgnoreCase(
                loadingPortName, destinationPortName);
        TradeRoute route = routeOpt.orElseGet(() -> {
            TradeRoute fallback = new TradeRoute();
            fallback.setDistanceNauticalMiles(5200.0);
            return fallback;
        });

        List<VesselRankingDto> results = new ArrayList<>();

        for (VesselType vessel : allVessels) {
            VesselRankingDto dto = new VesselRankingDto();
            dto.setVesselName(vessel.getName());

            // 1. Port physical compatibility check
            PortCompatibilityService.CompatibilityResult compat =
                    compatibilityService.checkCompatibility(vessel, loadingPortName, destinationPortName);

            dto.setCompatible(compat.isCompatible());

            // Adjusted freight rate by vessel factor
            double vesselFreightRate = baseFreightRateUsd * vessel.getCharterCostFactor();

            // 2. Cost and voyages calculation
            VoyageCostService.CostBreakdown cost = voyageCostService.calculateVoyageCost(
                    cargoTonnes, vesselFreightRate, vessel, route, destinationPortName);

            dto.setVoyagesRequired(cost.voyagesRequired);
            dto.setCapacityUtilization(cost.capacityUtilizationPct);
            dto.setEstimatedCostPerTonneUsd(cost.costPerTonneUsd.doubleValue());
            dto.setTotalCostInr(cost.totalCostInr);
            dto.setTotalCostInrFormatted(cost.totalCostInrFormatted);
            dto.setVoyageDays(cost.voyageDaysOneWay);
            dto.setHandlingDays(Math.round((cost.turnaroundHours / 24.0) * 10.0) / 10.0);

            // Relative CO2 Index (Base 100 for Handysize; larger parcels = fewer voyages = lower relative emissions per tonne)
            double co2Index = Math.max(40.0, Math.min(100.0,
                    100.0 - ((vessel.getTypicalCapacity() / 175000.0) * 45.0) * (cost.capacityUtilizationPct / 100.0)));
            dto.setRelativeCo2Index(Math.round(co2Index * 10.0) / 10.0);

            if (!compat.isCompatible()) {
                dto.setRejectionReason(compat.getPrimaryReason());
                dto.setScore(0);
                dto.setIdleRiskScore(90.0);
            } else {
                dto.setRejectionReason(null);

                // Scoring Formula:
                // Utilization (35 pts max)
                double utilScore = (cost.capacityUtilizationPct / 100.0) * 35.0;

                // Cost competitiveness (35 pts max) - lower cost per tonne gets higher score
                double costScore = Math.max(5.0, 35.0 - (cost.costPerTonneUsd.doubleValue() * 0.35));

                // Turnaround & voyage efficiency (15 pts max)
                double timeScore = Math.max(5.0, 15.0 - (cost.voyageDaysOneWay * 0.2));

                // CO2 / Scale efficiency (15 pts max)
                double scaleScore = ((100.0 - co2Index) / 60.0) * 15.0;

                int totalScore = (int) Math.round(utilScore + costScore + timeScore + scaleScore);
                dto.setScore(Math.max(20, Math.min(98, totalScore)));

                double idleRisk = (cost.voyagesRequired > 2 ? 45.0 : 25.0) + (cost.capacityUtilizationPct < 75.0 ? 20.0 : 0.0);
                dto.setIdleRiskScore(Math.round(idleRisk * 10.0) / 10.0);
            }

            results.add(dto);
        }

        // Sort compatible vessels by score descending, then incompatible at the bottom
        results.sort((a, b) -> {
            if (a.isCompatible() != b.isCompatible()) {
                return a.isCompatible() ? -1 : 1;
            }
            return Integer.compare(b.getScore(), a.getScore());
        });

        // Assign ranks 1..N for compatible
        int rank = 1;
        for (VesselRankingDto dto : results) {
            if (dto.isCompatible()) {
                dto.setRank(rank++);
            } else {
                dto.setRank(0); // Not ranked due to rejection
            }
        }

        return results;
    }
}
