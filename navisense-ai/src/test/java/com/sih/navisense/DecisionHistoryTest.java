package com.sih.navisense;

import com.sih.navisense.dto.CharterAnalysisRequest;
import com.sih.navisense.dto.CharterDecisionResponse;
import com.sih.navisense.entity.CharterDecisionRecord;
import com.sih.navisense.repository.CharterDecisionRecordRepository;
import com.sih.navisense.service.CharterIntelligenceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DecisionHistoryTest {

    @Autowired
    private CharterIntelligenceService charterService;

    @Autowired
    private CharterDecisionRecordRepository repository;

    @Test
    @DisplayName("Verify that analyzing charter creates traceable persistent decision record with budget compliance")
    void testDecisionCreationAndPersistence() {
        CharterAnalysisRequest req = new CharterAnalysisRequest();
        req.setCargoType("Coking Coal");
        req.setQuantityTonnes(150000.0);
        req.setOriginCountry("Australia");
        req.setLoadingPort("Hay Point");
        req.setDestinationPort("Paradip");
        req.setPreferredVessel("Panamax");
        req.setBudgetInr(600000000.0); // ₹60 Cr budget
        req.setExpectedFreightRateUsd(40.0);

        CharterDecisionResponse response = charterService.analyzeCharter(req);

        assertThat(response).isNotNull();
        assertThat(response.getFingerprint()).startsWith("NAV-26006-");
        assertThat(response.getBudgetStatus()).isEqualTo("WITHIN_BUDGET");
        assertThat(response.getBudgetVarianceInr()).isNotNull();

        // Check repository persistence
        Optional<CharterDecisionRecord> recordOpt = repository.findByFingerprint(response.getFingerprint());
        assertThat(recordOpt).isPresent();
        CharterDecisionRecord record = recordOpt.get();
        assertThat(record.getCargoType()).isEqualTo("Coking Coal");
        assertThat(record.getDestinationPort()).isEqualTo("Paradip");
        assertThat(record.getRecommendedVessel()).isEqualTo("Panamax");
        assertThat(record.getBudgetInr()).isNotNull();

        // Check retrieval by fingerprint
        Optional<CharterDecisionResponse> retrieved = charterService.getDecisionByFingerprint(response.getFingerprint());
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getRecommendedVessel()).isEqualTo("Panamax");
    }

    @Test
    @DisplayName("Verify decision record repository returns list with valid sorting and filtering")
    void testDecisionRecordListing() {
        List<CharterDecisionRecord> all = repository.findAll();
        assertThat(all).isNotEmpty();
        for (CharterDecisionRecord rec : all) {
            assertThat(rec.getFingerprint()).isNotEmpty();
            assertThat(rec.getTotalCostInr()).isNotNull();
            assertThat(rec.getPotentialSavingsInr()).isNotNull();
        }
    }
}
