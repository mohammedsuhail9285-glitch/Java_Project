package com.sih.navisense;

import com.sih.navisense.dto.CharterAnalysisRequest;
import com.sih.navisense.dto.CharterDecisionResponse;
import com.sih.navisense.service.CharterIntelligenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CharterIntelligenceServiceTest {

    @Autowired
    private CharterIntelligenceService charterIntelligenceService;

    @Test
    @DisplayName("End-to-End Charter Analysis should produce complete explainable decision")
    void testAnalyzeCharterEndToEnd() {
        CharterAnalysisRequest req = new CharterAnalysisRequest();
        req.setCargoType("Coking Coal");
        req.setQuantityTonnes(150000.0);
        req.setOriginCountry("Australia");
        req.setLoadingPort("Hay Point");
        req.setDestinationPort("Paradip");
        req.setShipmentDate(LocalDate.now().plusDays(14));
        req.setContractDurationMonths(3);

        CharterDecisionResponse res = charterIntelligenceService.analyzeCharter(req);

        assertNotNull(res);
        assertNotNull(res.getFingerprint(), "Must have decision fingerprint");
        assertTrue(res.getFingerprint().startsWith("NAV-26006-"));

        assertNotNull(res.getPrimaryDecision(), "Must have primary decision");
        assertEquals("Panamax", res.getRecommendedVessel(), "Panamax should be selected for 150kt Paradip parcel");
        assertEquals("PASSED", res.getPortCompatibilityStatus());

        assertTrue(res.getTotalCostInr().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(res.getPotentialSavingsInr().compareTo(BigDecimal.ZERO) > 0);
        assertNotNull(res.getTotalCostInrFormatted());
        assertNotNull(res.getPotentialSavingsInrFormatted());

        // Confidence and Risk
        assertTrue(res.getDecisionConfidence() >= 60 && res.getDecisionConfidence() <= 100);
        assertNotNull(res.getRiskLevel());

        // Explainability
        assertNotNull(res.getRationale());
        assertFalse(res.getRationale().isBlank());
        assertTrue(res.getRationale().contains("Panamax"));

        // Factors
        assertFalse(res.getPositiveFactors().isEmpty(), "Must have positive contribution factors");

        // Vessel rankings
        assertEquals(4, res.getVesselRankings().size());
        assertTrue(res.getVesselRankings().stream().anyMatch(v -> "Capesize".equals(v.getVesselName()) && !v.isCompatible()));
    }
}
