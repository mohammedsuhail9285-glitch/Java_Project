package com.sih.navisense.config;

import com.sih.navisense.entity.*;
import com.sih.navisense.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final PortRepository portRepository;
    private final VesselTypeRepository vesselTypeRepository;
    private final TradeRouteRepository tradeRouteRepository;
    private final FreightObservationRepository freightObservationRepository;
    private final CharterDecisionRecordRepository decisionRecordRepository;

    public DataLoader(PortRepository portRepository,
                      VesselTypeRepository vesselTypeRepository,
                      TradeRouteRepository tradeRouteRepository,
                      FreightObservationRepository freightObservationRepository,
                      CharterDecisionRecordRepository decisionRecordRepository) {
        this.portRepository = portRepository;
        this.vesselTypeRepository = vesselTypeRepository;
        this.tradeRouteRepository = tradeRouteRepository;
        this.freightObservationRepository = freightObservationRepository;
        this.decisionRecordRepository = decisionRecordRepository;
    }

    @Override
    public void run(String... args) {
        if (portRepository.count() > 0) {
            log.info("Database already seeded with demo data. Skipping seeding.");
            return;
        }

        log.info("Seeding deterministic demo data for NAVISENSE AI (SIH26006)...");
        seedPorts();
        seedVesselTypes();
        seedTradeRoutes();
        seedFreightObservations();
        seedDecisionRecords();
        log.info("Demo data seeding completed successfully.");
    }

    private void seedPorts() {
        List<Port> ports = List.of(
            // Indian East Coast Destination Ports
            new Port(null, "Paradip", "India", "INPRT", 16.5, 260.0, 43.0, 3000.0, 55.0, 36.0, 20.26, 86.67, "DESTINATION"),
            new Port(null, "Visakhapatnam", "India", "INVTZ", 18.5, 300.0, 48.0, 3500.0, 42.0, 30.0, 17.68, 83.21, "DESTINATION"),
            new Port(null, "Gangavaram", "India", "INGGV", 19.5, 320.0, 50.0, 4000.0, 35.0, 24.0, 17.62, 83.24, "DESTINATION"),
            new Port(null, "Dhamra", "India", "INDHR", 18.5, 310.0, 48.0, 3800.0, 38.0, 28.0, 20.82, 86.97, "DESTINATION"),
            new Port(null, "Gopalpur", "India", "INGPR", 14.5, 230.0, 33.0, 2200.0, 28.0, 32.0, 19.30, 84.97, "DESTINATION"),
            new Port(null, "Haldia", "India", "INHAL", 10.5, 215.0, 32.0, 1800.0, 68.0, 52.0, 22.02, 88.06, "DESTINATION"),
            new Port(null, "Sagar", "India", "INSAG", 12.0, 225.0, 32.5, 2000.0, 45.0, 40.0, 21.65, 88.03, "DESTINATION"),

            // Major International Loading Ports
            new Port(null, "Hay Point", "Australia", "AUHAY", 19.5, 330.0, 55.0, 6000.0, 25.0, 24.0, -21.28, 149.30, "LOADING"),
            new Port(null, "Gladstone", "Australia", "AUGLA", 17.5, 300.0, 50.0, 5000.0, 30.0, 26.0, -23.84, 151.27, "LOADING"),
            new Port(null, "Hampton Roads", "United States", "USHPT", 18.5, 310.0, 48.0, 5000.0, 35.0, 30.0, 36.95, -76.33, "LOADING"),
            new Port(null, "Maputo", "Mozambique", "MZMPT", 14.2, 250.0, 38.0, 2800.0, 40.0, 36.0, -25.96, 32.58, "LOADING"),
            new Port(null, "Ust-Luga", "Russia", "RUULU", 15.5, 265.0, 42.0, 3200.0, 35.0, 34.0, 59.68, 28.40, "LOADING"),
            new Port(null, "Tanjung Bara", "Indonesia", "IDTJB", 16.0, 260.0, 42.0, 3500.0, 32.0, 28.0, 0.53, 117.63, "LOADING")
        );
        portRepository.saveAll(ports);
    }

    private void seedVesselTypes() {
        List<VesselType> vessels = List.of(
            new VesselType(null, "Handysize", 25000.0, 40000.0, 35000.0, 10.2, 180.0, 28.0, 13.0, 21.0, 1.25, 12500.0),
            new VesselType(null, "Supramax",  50000.0, 65000.0, 58000.0, 12.8, 199.0, 32.2, 13.5, 27.0, 1.10, 15800.0),
            new VesselType(null, "Panamax",   65000.0, 85000.0, 75000.0, 14.5, 229.0, 32.3, 14.0, 32.0, 1.00, 18500.0),
            new VesselType(null, "Capesize",  120000.0, 200000.0, 175000.0, 18.0, 292.0, 45.0, 14.5, 52.0, 0.82, 26000.0)
        );
        vesselTypeRepository.saveAll(vessels);
    }

    private void seedTradeRoutes() {
        List<TradeRoute> routes = List.of(
            new TradeRoute(null, "Australia", "Hay Point", "Paradip", 5450.0, 16.2, 30.0, 1.05, 24.50),
            new TradeRoute(null, "Australia", "Gladstone", "Visakhapatnam", 5520.0, 16.4, 28.0, 1.04, 24.20),
            new TradeRoute(null, "Australia", "Hay Point", "Dhamra", 5420.0, 16.1, 29.0, 1.05, 24.30),
            new TradeRoute(null, "United States", "Hampton Roads", "Visakhapatnam", 9850.0, 29.3, 42.0, 1.02, 44.00),
            new TradeRoute(null, "United States", "Hampton Roads", "Paradip", 9920.0, 29.5, 45.0, 1.02, 44.50),
            new TradeRoute(null, "Mozambique", "Maputo", "Dhamra", 4380.0, 13.0, 38.0, 1.08, 19.80),
            new TradeRoute(null, "Mozambique", "Maputo", "Paradip", 4420.0, 13.2, 40.0, 1.08, 20.10),
            new TradeRoute(null, "Indonesia", "Tanjung Bara", "Haldia", 2450.0, 7.8, 35.0, 1.10, 12.50),
            new TradeRoute(null, "Indonesia", "Tanjung Bara", "Paradip", 2380.0, 7.5, 25.0, 1.06, 11.80),
            new TradeRoute(null, "Russia", "Ust-Luga", "Paradip", 8650.0, 25.7, 52.0, 1.12, 38.50)
        );
        tradeRouteRepository.saveAll(routes);
    }

    private void seedFreightObservations() {
        Random random = new Random(26006L);
        LocalDate endDate = LocalDate.now(); // e.g. 2026-09-11
        LocalDate startDate = endDate.minusDays(365);

        List<TradeRoute> routes = tradeRouteRepository.findAll();
        List<VesselType> vessels = vesselTypeRepository.findAll();
        List<FreightObservation> observations = new ArrayList<>();

        for (TradeRoute route : routes) {
            for (VesselType vessel : vessels) {
                // Base freight adjusted for vessel size factor
                double vesselBaseRate = route.getBaseFreightUsdPerTonne() * vessel.getCharterCostFactor();

                double currentRate = vesselBaseRate;
                for (int d = 0; d <= 365; d++) {
                    LocalDate obsDate = startDate.plusDays(d);

                    // Deterministic seasonality (sinusoidal over 365 days)
                    double seasonalComponent = Math.sin((d / 365.0) * 2 * Math.PI) * (vesselBaseRate * 0.08);

                    // Slight long-term cyclical trend
                    double trendComponent = (d / 365.0) * (vesselBaseRate * 0.04);

                    // Small deterministic noise
                    double noise = (random.nextGaussian() * 0.35);

                    // Occasional volatility spike (5% chance)
                    double spike = 0.0;
                    if (random.nextDouble() < 0.05) {
                        spike = (random.nextBoolean() ? 1.0 : -1.0) * (1.2 + random.nextDouble() * 1.5);
                    }

                    double rate = Math.max(5.0, vesselBaseRate + seasonalComponent + trendComponent + noise + spike);
                    rate = Math.round(rate * 100.0) / 100.0;

                    // Synthetic market indicators
                    double bdi = 1600.0 + (rate / vesselBaseRate) * 300.0 + random.nextGaussian() * 20.0;
                    double fuel = 600.0 + (d / 365.0) * 40.0 + random.nextGaussian() * 15.0;
                    double demand = 70.0 + Math.sin(d / 60.0) * 10.0;
                    double supply = 65.0 + Math.cos(d / 90.0) * 8.0;
                    double congestion = 45.0 + Math.sin(d / 45.0) * 12.0;

                    observations.add(new FreightObservation(
                        null,
                        obsDate,
                        route.getOriginCountry(),
                        route.getLoadingPort(),
                        route.getDestinationPort(),
                        vessel.getName(),
                        rate,
                        Math.round(bdi * 10.0) / 10.0,
                        Math.round(fuel * 10.0) / 10.0,
                        Math.round(demand * 10.0) / 10.0,
                        Math.round(supply * 10.0) / 10.0,
                        Math.round(congestion * 10.0) / 10.0
                    ));
                }
            }
        }

        // Batch save observations
        freightObservationRepository.saveAll(observations);
        log.info("Seeded {} historical freight observations.", observations.size());
    }

    private void seedDecisionRecords() {
        if (decisionRecordRepository.count() > 0) return;

        CharterDecisionRecord r1 = new CharterDecisionRecord();
        r1.setFingerprint("NAV-26006-20260902-001");
        r1.setDecisionTimestamp(LocalDateTime.now().minusDays(9));
        r1.setCargoType("Coking Coal");
        r1.setQuantityTonnes(150000.0);
        r1.setOriginCountry("Australia");
        r1.setLoadingPort("Hay Point");
        r1.setDestinationPort("Paradip");
        r1.setRecommendedVessel("Panamax");
        r1.setContractStrategy("Short-Term Multi-Voyage Contract");
        r1.setPrimaryDecision("BOOK_NOW");
        r1.setEstimatedFreightRateUsd(26.25);
        r1.setForecastFreightRateUsd(27.40);
        r1.setTotalCostUsd(BigDecimal.valueOf(5793638.10));
        r1.setTotalCostInr(BigDecimal.valueOf(495356057.55));
        r1.setPotentialSavingsInr(BigDecimal.valueOf(18400000.00));
        r1.setConfidenceScore(88);
        r1.setRiskLevel("LOW");
        r1.setRationale("Panamax achieves optimal 100% capacity utilization across 2 voyages while strictly complying with Paradip 16.5m draft. Securing Short-Term COA locks rate before anticipated market surge.");
        r1.setBestCharterWindow("05 Sep 2026 – 12 Sep 2026");
        r1.setBudgetInr(BigDecimal.valueOf(500000000.00));

        CharterDecisionRecord r2 = new CharterDecisionRecord();
        r2.setFingerprint("NAV-26006-20260905-002");
        r2.setDecisionTimestamp(LocalDateTime.now().minusDays(6));
        r2.setCargoType("Coking Coal");
        r2.setQuantityTonnes(80000.0);
        r2.setOriginCountry("Mozambique");
        r2.setLoadingPort("Maputo");
        r2.setDestinationPort("Dhamra");
        r2.setRecommendedVessel("Supramax");
        r2.setContractStrategy("Hybrid Contract (Spot + COA)");
        r2.setPrimaryDecision("SHORT_TERM_CONTRACT");
        r2.setEstimatedFreightRateUsd(21.40);
        r2.setForecastFreightRateUsd(22.10);
        r2.setTotalCostUsd(BigDecimal.valueOf(3207017.54));
        r2.setTotalCostInr(BigDecimal.valueOf(274200000.00));
        r2.setPotentialSavingsInr(BigDecimal.valueOf(11200000.00));
        r2.setConfidenceScore(84);
        r2.setRiskLevel("MEDIUM");
        r2.setRationale("Supramax provides best operational flexibility for mid-sized parcel with onboard geared discharge. Hybrid contract balances cost certainty with market flexibility.");
        r2.setBestCharterWindow("08 Sep 2026 – 15 Sep 2026");
        r2.setBudgetInr(BigDecimal.valueOf(280000000.00));

        CharterDecisionRecord r3 = new CharterDecisionRecord();
        r3.setFingerprint("NAV-26006-20260908-003");
        r3.setDecisionTimestamp(LocalDateTime.now().minusDays(3));
        r3.setCargoType("Thermal Coal");
        r3.setQuantityTonnes(55000.0);
        r3.setOriginCountry("Indonesia");
        r3.setLoadingPort("Tanjung Bara");
        r3.setDestinationPort("Haldia");
        r3.setRecommendedVessel("Handysize");
        r3.setContractStrategy("Spot Voyage Charter");
        r3.setPrimaryDecision("WAIT");
        r3.setEstimatedFreightRateUsd(14.80);
        r3.setForecastFreightRateUsd(14.20);
        r3.setTotalCostUsd(BigDecimal.valueOf(2064327.48));
        r3.setTotalCostInr(BigDecimal.valueOf(176500000.00));
        r3.setPotentialSavingsInr(BigDecimal.valueOf(8500000.00));
        r3.setConfidenceScore(79);
        r3.setRiskLevel("HIGH");
        r3.setRationale("Strict 10.5m riverine draft at Haldia mandates Handysize class. Short-term forecast shows rate softening; delaying fixture captures projected tariff declines.");
        r3.setBestCharterWindow("18 Sep 2026 – 25 Sep 2026");
        r3.setBudgetInr(BigDecimal.valueOf(180000000.00));

        CharterDecisionRecord r4 = new CharterDecisionRecord();
        r4.setFingerprint("NAV-26006-20260910-004");
        r4.setDecisionTimestamp(LocalDateTime.now().minusDays(1));
        r4.setCargoType("Hard Coking Coal");
        r4.setQuantityTonnes(180000.0);
        r4.setOriginCountry("United States");
        r4.setLoadingPort("Hampton Roads");
        r4.setDestinationPort("Visakhapatnam");
        r4.setRecommendedVessel("Capesize");
        r4.setContractStrategy("Annual Contract of Affreightment");
        r4.setPrimaryDecision("BOOK_NOW");
        r4.setEstimatedFreightRateUsd(45.20);
        r4.setForecastFreightRateUsd(46.80);
        r4.setTotalCostUsd(BigDecimal.valueOf(8514619.88));
        r4.setTotalCostInr(BigDecimal.valueOf(728000000.00));
        r4.setPotentialSavingsInr(BigDecimal.valueOf(32000000.00));
        r4.setConfidenceScore(91);
        r4.setRiskLevel("LOW");
        r4.setRationale("Visakhapatnam deepwater berth (18.1m) accommodates Capesize deadweight without lightering. Single voyage transports full parcel at lowest unit cost.");
        r4.setBestCharterWindow("15 Sep 2026 – 22 Sep 2026");
        r4.setBudgetInr(BigDecimal.valueOf(750000000.00));

        decisionRecordRepository.saveAll(List.of(r1, r2, r3, r4));
        log.info("Seeded 4 realistic historical charter decision records.");
    }
}
