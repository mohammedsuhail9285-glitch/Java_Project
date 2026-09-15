package com.sih.navisense;

import com.sih.navisense.dto.ContractOptionDto;
import com.sih.navisense.service.ContractStrategyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ContractStrategyServiceTest {

    private ContractStrategyService strategyService;

    @BeforeEach
    void setUp() {
        strategyService = new ContractStrategyService(0.045, 0.080);
    }

    @Test
    @DisplayName("Should evaluate 4 contract strategies and recommend Short-Term COA in upward market")
    void testEvaluateStrategiesUpward() {
        BigDecimal baseSpendInr = new BigDecimal("400000000"); // 40 Cr

        List<ContractOptionDto> options = strategyService.evaluateStrategies(baseSpendInr, 18.5, "UPWARD", 55);

        assertEquals(4, options.size(), "Should evaluate Spot, Short-Term, Medium-Term, and Hybrid");

        ContractOptionDto recommended = options.stream().filter(ContractOptionDto::isRecommended).findFirst().orElse(null);
        assertNotNull(recommended, "One contract strategy must be recommended");
        assertEquals("Short-Term Multi-Voyage Contract", recommended.getStrategyName());
        assertTrue(recommended.getPotentialSavingInr().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    @DisplayName("Should recommend Hybrid strategy when freight trend is downward")
    void testEvaluateStrategiesDownward() {
        BigDecimal baseSpendInr = new BigDecimal("400000000"); // 40 Cr

        List<ContractOptionDto> options = strategyService.evaluateStrategies(baseSpendInr, 16.0, "DOWNWARD", 45);

        ContractOptionDto recommended = options.stream().filter(ContractOptionDto::isRecommended).findFirst().orElse(null);
        assertNotNull(recommended);
        assertTrue(recommended.getStrategyName().contains("Hybrid"));
    }
}
