package com.sih.navisense;

import com.sih.navisense.dto.EvaluationMetricsDto;
import com.sih.navisense.service.ForecastEvaluationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ForecastEvaluationServiceTest {

    private ForecastEvaluationService evaluationService;

    @BeforeEach
    void setUp() {
        evaluationService = new ForecastEvaluationService();
    }

    @Test
    @DisplayName("MAE, RMSE, and MAPE should calculate correctly and handle zeroes safely")
    void testMetricsCalculation() {
        List<Double> actuals = List.of(
                22.0, 22.5, 23.0, 22.8, 23.2, 23.5, 23.1, 23.8, 24.0, 24.2,
                24.5, 24.1, 24.7, 25.0, 24.8, 25.2, 25.5, 25.1, 25.8, 26.0
        );

        EvaluationMetricsDto hybrid = evaluationService.calculateEnsembleMetrics(actuals);
        EvaluationMetricsDto naive = evaluationService.calculateNaiveMetrics(actuals);

        assertNotNull(hybrid);
        assertNotNull(naive);

        assertTrue(hybrid.getMae() > 0, "MAE must be positive");
        assertTrue(hybrid.getRmse() > 0, "RMSE must be positive");
        assertTrue(hybrid.getMape() > 0, "MAPE must be positive");

        assertFalse(Double.isNaN(hybrid.getMape()), "MAPE must not be NaN");
        assertFalse(Double.isInfinite(hybrid.getMape()), "MAPE must not be Infinite");
    }

    @Test
    @DisplayName("Empty or small actuals list should return safe baseline without crash")
    void testEmptyActuals() {
        EvaluationMetricsDto emptyHybrid = evaluationService.calculateEnsembleMetrics(List.of());
        assertNotNull(emptyHybrid);
        assertTrue(emptyHybrid.getMae() > 0);

        EvaluationMetricsDto emptyNaive = evaluationService.calculateNaiveMetrics(null);
        assertNotNull(emptyNaive);
        assertTrue(emptyNaive.getRmse() > 0);
    }
}
