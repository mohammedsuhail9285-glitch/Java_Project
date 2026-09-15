package com.sih.navisense.service;

import com.sih.navisense.entity.Port;
import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.provider.CongestionDataProvider;
import com.sih.navisense.provider.ExchangeRateProvider;
import com.sih.navisense.provider.MarketDataProvider;
import com.sih.navisense.repository.PortRepository;
import com.sih.navisense.util.CurrencyFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class VoyageCostService {

    private final ExchangeRateProvider exchangeRateProvider;
    private final MarketDataProvider marketDataProvider;
    private final CongestionDataProvider congestionDataProvider;
    private final PortRepository portRepository;

    private final double defaultPortTariffUsdPerTonne;
    private final double demurrageDailyRateUsd;

    public VoyageCostService(
            ExchangeRateProvider exchangeRateProvider,
            MarketDataProvider marketDataProvider,
            CongestionDataProvider congestionDataProvider,
            PortRepository portRepository,
            @Value("${navisense.port.tariff-per-tonne-usd:3.25}") double defaultPortTariffUsdPerTonne,
            @Value("${navisense.demurrage.daily-rate-usd:18500.00}") double demurrageDailyRateUsd) {
        this.exchangeRateProvider = exchangeRateProvider;
        this.marketDataProvider = marketDataProvider;
        this.congestionDataProvider = congestionDataProvider;
        this.portRepository = portRepository;
        this.defaultPortTariffUsdPerTonne = defaultPortTariffUsdPerTonne;
        this.demurrageDailyRateUsd = demurrageDailyRateUsd;
    }

    public static class CostBreakdown {
        public int voyagesRequired;
        public double capacityUtilizationPct;
        public double voyageDaysOneWay;
        public double turnaroundHours;
        public BigDecimal freightChargeUsd;
        public BigDecimal bunkerCostUsd;
        public BigDecimal portTariffUsd;
        public BigDecimal demurrageCostUsd;
        public BigDecimal totalCostUsd;
        public BigDecimal totalCostInr;
        public String totalCostInrFormatted;
        public BigDecimal costPerTonneUsd;
        public BigDecimal costPerTonneInr;
        public Map<String, Object> auditMap = new LinkedHashMap<>();
    }

    public CostBreakdown calculateVoyageCost(
            double cargoTonnes,
            double freightRateUsdPerTonne,
            VesselType vessel,
            TradeRoute route,
            String destinationPortName) {

        CostBreakdown res = new CostBreakdown();

        if (cargoTonnes <= 0 || vessel == null) {
            res.voyagesRequired = 0;
            res.capacityUtilizationPct = 0.0;
            res.freightChargeUsd = BigDecimal.ZERO;
            res.bunkerCostUsd = BigDecimal.ZERO;
            res.portTariffUsd = BigDecimal.ZERO;
            res.demurrageCostUsd = BigDecimal.ZERO;
            res.totalCostUsd = BigDecimal.ZERO;
            res.totalCostInr = BigDecimal.ZERO;
            res.totalCostInrFormatted = "₹0.00";
            res.costPerTonneUsd = BigDecimal.ZERO;
            res.costPerTonneInr = BigDecimal.ZERO;
            return res;
        }

        double capacity = vessel.getTypicalCapacity() > 0 ? vessel.getTypicalCapacity() : 50000.0;
        int voyages = (int) Math.ceil(cargoTonnes / capacity);
        if (voyages < 1) voyages = 1;
        res.voyagesRequired = voyages;

        double totalCarryingCapacity = voyages * capacity;
        res.capacityUtilizationPct = Math.min(100.0, Math.round((cargoTonnes / totalCarryingCapacity) * 1000.0) / 10.0);

        // Distance & voyage duration
        double distance = (route != null && route.getDistanceNauticalMiles() != null)
                ? route.getDistanceNauticalMiles() : 5000.0;
        double speed = vessel.getSpeedKnots() > 0 ? vessel.getSpeedKnots() : 13.5;
        double voyageDaysOneWay = distance / (speed * 24.0);
        res.voyageDaysOneWay = Math.round(voyageDaysOneWay * 10.0) / 10.0;

        // Port turnaround and congestion wait
        double waitDays = congestionDataProvider.getEstimatedWaitDays(destinationPortName);
        double handlingRate = 3000.0; // t/h default
        double turnaroundHours = 32.0;

        if (destinationPortName != null) {
            portRepository.findByNameIgnoreCase(destinationPortName.trim()).ifPresent(port -> {
                // handling rate from port entity
            });
        }
        res.turnaroundHours = turnaroundHours;

        // 1. Base Freight Charge (Rate * Tonnes)
        BigDecimal freightCharge = BigDecimal.valueOf(cargoTonnes)
                .multiply(BigDecimal.valueOf(freightRateUsdPerTonne))
                .setScale(2, RoundingMode.HALF_UP);
        res.freightChargeUsd = freightCharge;

        // 2. Bunker Fuel Approximation
        // Round trip sea days = voyageDaysOneWay * 2
        double roundTripDays = voyageDaysOneWay * 2.0;
        double bunkerPrice = marketDataProvider.getBunkerFuelPricePerTonneUsd();
        double fuelPerDay = vessel.getFuelConsumptionPerDay();
        double totalBunkerCost = roundTripDays * fuelPerDay * bunkerPrice * voyages;
        res.bunkerCostUsd = BigDecimal.valueOf(totalBunkerCost).setScale(2, RoundingMode.HALF_UP);

        // 3. Port Tariffs & Cargo Handling
        double totalPortTariff = cargoTonnes * defaultPortTariffUsdPerTonne;
        res.portTariffUsd = BigDecimal.valueOf(totalPortTariff).setScale(2, RoundingMode.HALF_UP);

        // 4. Demurrage / Waiting Risk Cost
        double dailyHire = vessel.getDailyHireBaseUsd() > 0 ? vessel.getDailyHireBaseUsd() : demurrageDailyRateUsd;
        double totalDemurrage = waitDays * dailyHire * voyages;
        res.demurrageCostUsd = BigDecimal.valueOf(totalDemurrage).setScale(2, RoundingMode.HALF_UP);

        // 5. Total Voyage Cost (USD & INR)
        // In maritime chartering, freight rate incorporates voyage costs, but total procurement budget
        // encompasses base freight + auxiliary port tariffs + bunker + congestion buffer
        BigDecimal totalUsd = freightCharge.add(res.bunkerCostUsd)
                .add(res.portTariffUsd)
                .add(res.demurrageCostUsd)
                .setScale(2, RoundingMode.HALF_UP);
        res.totalCostUsd = totalUsd;

        res.totalCostInr = exchangeRateProvider.convertUsdToInr(totalUsd);
        res.totalCostInrFormatted = CurrencyFormatter.formatInrCroresOrLakhs(res.totalCostInr);

        res.costPerTonneUsd = totalUsd.divide(BigDecimal.valueOf(cargoTonnes), 2, RoundingMode.HALF_UP);
        res.costPerTonneInr = res.totalCostInr.divide(BigDecimal.valueOf(cargoTonnes), 2, RoundingMode.HALF_UP);

        // Transparency Audit Map for UI "How was this calculated?"
        res.auditMap.put("cargoQuantityTonnes", cargoTonnes);
        res.auditMap.put("freightRateUsdPerTonne", freightRateUsdPerTonne);
        res.auditMap.put("freightSubtotalUsd", freightCharge);
        res.auditMap.put("vesselCapacityTonnes", capacity);
        res.auditMap.put("voyagesRequired", voyages);
        res.auditMap.put("capacityUtilizationPct", res.capacityUtilizationPct + "%");
        res.auditMap.put("oneWayDistanceNm", distance);
        res.auditMap.put("voyageDurationDays", res.voyageDaysOneWay);
        res.auditMap.put("fuelConsumptionTonnesPerDay", fuelPerDay);
        res.auditMap.put("bunkerFuelPricePerTonneUsd", "$" + bunkerPrice);
        res.auditMap.put("bunkerCostSubtotalUsd", res.bunkerCostUsd);
        res.auditMap.put("portTariffRatePerTonneUsd", "$" + defaultPortTariffUsdPerTonne);
        res.auditMap.put("portTariffsSubtotalUsd", res.portTariffUsd);
        res.auditMap.put("estimatedCongestionWaitDays", waitDays);
        res.auditMap.put("demurrageDailyHireRateUsd", "$" + dailyHire);
        res.auditMap.put("demurrageCostSubtotalUsd", res.demurrageCostUsd);
        res.auditMap.put("totalLogisticsCostUsd", res.totalCostUsd);
        res.auditMap.put("usdToInrExchangeRate", exchangeRateProvider.getUsdToInrRate());
        res.auditMap.put("totalLogisticsCostInr", res.totalCostInrFormatted);
        res.auditMap.put("costPerTonneUsd", "$" + res.costPerTonneUsd.toPlainString());
        res.auditMap.put("costPerTonneInr", "₹" + res.costPerTonneInr.toPlainString());

        return res;
    }
}
