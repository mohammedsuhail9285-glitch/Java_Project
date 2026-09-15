package com.sih.navisense;

import com.sih.navisense.dto.ForecastPointDto;
import com.sih.navisense.dto.ForecastSummaryDto;
import com.sih.navisense.entity.FreightObservation;
import com.sih.navisense.repository.FreightObservationRepository;
import com.sih.navisense.repository.TradeRouteRepository;
import com.sih.navisense.repository.VesselTypeRepository;
import com.sih.navisense.service.ForecastEvaluationService;
import com.sih.navisense.service.FreightForecastService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class FreightForecastServiceTest {

    private FreightObservationRepository observationRepository;
    private TradeRouteRepository routeRepository;
    private VesselTypeRepository vesselTypeRepository;
    private ForecastEvaluationService evaluationService;
    private FreightForecastService forecastService;

    @BeforeEach
    void setUp() {
        observationRepository = Mockito.mock(FreightObservationRepository.class);
        routeRepository = Mockito.mock(TradeRouteRepository.class);
        vesselTypeRepository = Mockito.mock(VesselTypeRepository.class);
        evaluationService = new ForecastEvaluationService();

        forecastService = new FreightForecastService(
                observationRepository,
                routeRepository,
                vesselTypeRepository,
                evaluationService,
                0.35, 0.30, 0.25, 0.10, 0.35
        );
    }

    @Test
    @DisplayName("Should generate forecast points across all 5 horizons (7, 14, 30, 60, 90 days)")
    void testGenerateForecastHorizons() {
        List<FreightObservation> obs = new ArrayList<>();
        LocalDate now = LocalDate.now();
        for (int i = 60; i >= 0; i--) {
            obs.add(new FreightObservation(
                    (long) i, now.minusDays(i), "Australia", "Hay Point", "Paradip", "Panamax",
                    24.0 + (i % 5) * 0.3, 1800.0, 600.0, 70.0, 65.0, 50.0
            ));
        }

        when(observationRepository.findByOriginCountryIgnoreCaseAndDestinationPortIgnoreCaseAndVesselClassIgnoreCaseOrderByObservationDateAsc(
                "Australia", "Paradip", "Panamax")).thenReturn(obs);

        ForecastSummaryDto summary = forecastService.generateForecast("Australia", "Paradip", "Panamax");

        assertNotNull(summary);
        assertEquals(5, summary.getForecastPoints().size(), "Must generate 5 standard horizons");
        assertEquals(7, summary.getForecastPoints().get(0).getHorizonDays());
        assertEquals(14, summary.getForecastPoints().get(1).getHorizonDays());
        assertEquals(30, summary.getForecastPoints().get(2).getHorizonDays());
        assertEquals(60, summary.getForecastPoints().get(3).getHorizonDays());
        assertEquals(90, summary.getForecastPoints().get(4).getHorizonDays());

        for (ForecastPointDto pt : summary.getForecastPoints()) {
            assertTrue(pt.getPredictedRateUsd() > 0, "Predicted rate must be positive");
            assertTrue(pt.getLowerBoundUsd() <= pt.getPredictedRateUsd(), "Lower bound must be <= predicted");
            assertTrue(pt.getUpperBoundUsd() >= pt.getPredictedRateUsd(), "Upper bound must be >= predicted");
            assertTrue(pt.getConfidence() >= 0 && pt.getConfidence() <= 100, "Confidence must be 0-100");
        }
    }

    @Test
    @DisplayName("Should handle empty observations gracefully without crashing")
    void testEmptyObservationsFallback() {
        when(observationRepository.findByOriginCountryIgnoreCaseAndDestinationPortIgnoreCaseAndVesselClassIgnoreCaseOrderByObservationDateAsc(
                anyString(), anyString(), anyString())).thenReturn(List.of());

        ForecastSummaryDto summary = forecastService.generateForecast("UnknownOrigin", "UnknownDest", "Panamax");

        assertNotNull(summary);
        assertTrue(summary.getCurrentRateUsd() > 0, "Fallback rate must be assigned");
        assertFalse(summary.getForecastPoints().isEmpty());
    }

    @Test
    @DisplayName("Volatility calculation should return valid range and avoid divide by zero")
    void testVolatilityCalculation() {
        List<Double> rates = List.of(24.0, 24.5, 24.2, 25.1, 24.8, 25.4, 25.0);
        double vol = forecastService.calculateVolatility(rates);
        assertTrue(vol >= 5.0 && vol <= 65.0, "Annualized volatility should be within realistic bounds");

        // Edge case: single rate or identical rates
        assertEquals(15.0, forecastService.calculateVolatility(List.of(25.0)));
        assertEquals(5.0, forecastService.calculateVolatility(List.of(25.0, 25.0, 25.0)));
    }
}
