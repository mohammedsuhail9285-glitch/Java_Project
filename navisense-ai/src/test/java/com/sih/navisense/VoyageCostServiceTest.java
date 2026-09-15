package com.sih.navisense;

import com.sih.navisense.entity.TradeRoute;
import com.sih.navisense.entity.VesselType;
import com.sih.navisense.provider.ConfiguredExchangeRateProvider;
import com.sih.navisense.provider.DemoCongestionDataProvider;
import com.sih.navisense.provider.DemoMarketDataProvider;
import com.sih.navisense.repository.PortRepository;
import com.sih.navisense.service.VoyageCostService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class VoyageCostServiceTest {

    private VoyageCostService voyageCostService;

    @BeforeEach
    void setUp() {
        ConfiguredExchangeRateProvider exchangeRateProvider = new ConfiguredExchangeRateProvider(85.50);
        DemoMarketDataProvider marketDataProvider = new DemoMarketDataProvider(620.0);
        DemoCongestionDataProvider congestionDataProvider = new DemoCongestionDataProvider();
        PortRepository portRepository = Mockito.mock(PortRepository.class);

        voyageCostService = new VoyageCostService(
                exchangeRateProvider,
                marketDataProvider,
                congestionDataProvider,
                portRepository,
                3.25,
                18500.0
        );
    }

    @Test
    @DisplayName("Should accurately calculate multi-voyage logistics cost in USD and INR for 150,000t parcel")
    void testCalculateVoyageCost() {
        VesselType panamax = new VesselType(1L, "Panamax", 65000.0, 85000.0, 75000.0, 14.5, 229.0, 32.3, 14.0, 32.0, 1.0, 18500.0);
        TradeRoute route = new TradeRoute(1L, "Australia", "Hay Point", "Paradip", 5450.0, 16.2, 30.0, 1.05, 24.50);

        VoyageCostService.CostBreakdown cost = voyageCostService.calculateVoyageCost(
                150000.0, 24.50, panamax, route, "Paradip");

        assertNotNull(cost);
        assertEquals(2, cost.voyagesRequired, "150,000t on 75,000t capacity requires exactly 2 voyages");
        assertEquals(100.0, cost.capacityUtilizationPct, "Utilization should be 100%");

        assertTrue(cost.freightChargeUsd.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(cost.bunkerCostUsd.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(cost.portTariffUsd.compareTo(BigDecimal.ZERO) > 0);
        assertTrue(cost.totalCostUsd.compareTo(cost.freightChargeUsd) > 0);
        assertTrue(cost.totalCostInr.compareTo(BigDecimal.ZERO) > 0);

        // Check INR formatting
        assertNotNull(cost.totalCostInrFormatted);
        assertTrue(cost.totalCostInrFormatted.contains("₹") && cost.totalCostInrFormatted.contains("Cr"));

        // Audit map must contain components
        assertFalse(cost.auditMap.isEmpty());
        assertTrue(cost.auditMap.containsKey("freightSubtotalUsd"));
        assertTrue(cost.auditMap.containsKey("totalLogisticsCostInr"));
    }

    @Test
    @DisplayName("Edge cases: 0 cargo or null vessel should return zero safely without NullPointer")
    void testZeroOrNegativeCargo() {
        VesselType panamax = new VesselType(1L, "Panamax", 65000.0, 85000.0, 75000.0, 14.5, 229.0, 32.3, 14.0, 32.0, 1.0, 18500.0);
        TradeRoute route = new TradeRoute(1L, "Australia", "Hay Point", "Paradip", 5450.0, 16.2, 30.0, 1.05, 24.50);

        VoyageCostService.CostBreakdown zeroCost = voyageCostService.calculateVoyageCost(0, 24.50, panamax, route, "Paradip");
        assertEquals(BigDecimal.ZERO, zeroCost.totalCostUsd);
        assertEquals("₹0.00", zeroCost.totalCostInrFormatted);

        VoyageCostService.CostBreakdown nullVesselCost = voyageCostService.calculateVoyageCost(150000.0, 24.50, null, route, "Paradip");
        assertEquals(BigDecimal.ZERO, nullVesselCost.totalCostUsd);
    }
}
