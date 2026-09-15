package com.sih.navisense;

import com.sih.navisense.provider.DemoCongestionDataProvider;
import com.sih.navisense.provider.DemoMarketDataProvider;
import com.sih.navisense.service.RiskAssessmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RiskAssessmentServiceTest {

    private RiskAssessmentService riskAssessmentService;

    @BeforeEach
    void setUp() {
        DemoMarketDataProvider marketDataProvider = new DemoMarketDataProvider(620.0);
        DemoCongestionDataProvider congestionDataProvider = new DemoCongestionDataProvider();

        riskAssessmentService = new RiskAssessmentService(
                marketDataProvider,
                congestionDataProvider,
                0.25, 0.25, 0.15, 0.15, 0.10, 0.10
        );
    }

    @Test
    @DisplayName("Risk score should be bounded between 0 and 100 with documented category")
    void testEvaluateRisk() {
        RiskAssessmentService.RiskAssessmentResult res = riskAssessmentService.evaluateRisk(
                "Paradip", 30.0, 18.4, true, 95.0, 14
        );

        assertNotNull(res);
        assertTrue(res.getOverallScore() >= 0 && res.getOverallScore() <= 100);
        assertTrue(List.of("LOW", "MEDIUM", "HIGH", "CRITICAL").contains(res.getRiskCategory()));
        assertNotNull(res.getPrimaryReason());
        assertNotNull(res.getMitigationAdvice());
    }

    @Test
    @DisplayName("Incompatible vessel or high volatility should elevate risk score")
    void testHighRiskConditions() {
        RiskAssessmentService.RiskAssessmentResult highRisk = riskAssessmentService.evaluateRisk(
                "Haldia", 50.0, 35.0, false, 50.0, 60
        );

        assertTrue(highRisk.getOverallScore() > 60, "Severe conditions must elevate risk score");
        assertTrue("HIGH".equals(highRisk.getRiskCategory()) || "CRITICAL".equals(highRisk.getRiskCategory()));
    }
}
