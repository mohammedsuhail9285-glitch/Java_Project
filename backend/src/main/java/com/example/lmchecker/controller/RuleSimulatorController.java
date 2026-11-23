package com.example.lmchecker.controller;

import com.example.lmchecker.compliance.*;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/rules/simulator")
public class RuleSimulatorController {

    private final ProductRepository productRepository;
    private final ExtractedFieldRepository extractedFieldRepository;
    private final ProductListingRepository productListingRepository;
    private final RuleRegistry ruleRegistry;
    private final FieldPresenceEvaluator fieldPresenceEvaluator;

    public RuleSimulatorController(ProductRepository productRepository,
                                   ExtractedFieldRepository extractedFieldRepository,
                                   ProductListingRepository productListingRepository,
                                   RuleRegistry ruleRegistry,
                                   FieldPresenceEvaluator fieldPresenceEvaluator) {
        this.productRepository = productRepository;
        this.extractedFieldRepository = extractedFieldRepository;
        this.productListingRepository = productListingRepository;
        this.ruleRegistry = ruleRegistry;
        this.fieldPresenceEvaluator = fieldPresenceEvaluator;
    }

    public static class SimulationRequest {
        private Rule rule;
        private Long productId; // optional single product, or null for sample batch
        private Integer sampleSize = 10;

        public Rule getRule() { return rule; }
        public void setRule(Rule rule) { this.rule = rule; }
        public Long getProductId() { return productId; }
        public void setProductId(Long productId) { this.productId = productId; }
        public Integer getSampleSize() { return sampleSize; }
        public void setSampleSize(Integer sampleSize) { this.sampleSize = sampleSize; }
    }

    public static class SimulationOutcomeDto {
        private Long productId;
        private String productName;
        private boolean flaggedPotentialViolation;
        private String detectedValue;
        private String expectedCondition;
        private String remediationMessage;

        public SimulationOutcomeDto() {}
        public SimulationOutcomeDto(Long productId, String productName, boolean flaggedPotentialViolation, String detectedValue, String expectedCondition, String remediationMessage) {
            this.productId = productId;
            this.productName = productName;
            this.flaggedPotentialViolation = flaggedPotentialViolation;
            this.detectedValue = detectedValue;
            this.expectedCondition = expectedCondition;
            this.remediationMessage = remediationMessage;
        }

        public Long getProductId() { return productId; }
        public String getProductName() { return productName; }
        public boolean isFlaggedPotentialViolation() { return flaggedPotentialViolation; }
        public String getDetectedValue() { return detectedValue; }
        public String getExpectedCondition() { return expectedCondition; }
        public String getRemediationMessage() { return remediationMessage; }
    }

    @PostMapping("/run")
    public ResponseEntity<Map<String, Object>> runSimulation(@RequestBody SimulationRequest request) {
        Rule rule = request.getRule();
        if (rule == null) {
            rule = Rule.builder()
                    .ruleId("DRAFT-01")
                    .ruleName("Draft Rule Simulation")
                    .validationType(ValidationType.FIELD_PRESENCE)
                    .expectedField("MRP")
                    .severity(Severity.MAJOR)
                    .build();
        }

        List<Product> products = new ArrayList<>();
        if (request.getProductId() != null) {
            productRepository.findById(request.getProductId()).ifPresent(products::add);
        } else {
            products = productRepository.findAll();
            int limit = request.getSampleSize() != null ? Math.min(request.getSampleSize(), products.size()) : 10;
            products = products.subList(0, limit);
        }

        RuleEvaluator evaluator = ruleRegistry.getEvaluator(rule.getValidationType());
        if (evaluator == null) evaluator = fieldPresenceEvaluator;

        List<SimulationOutcomeDto> outcomes = new ArrayList<>();
        int flaggedCount = 0;

        for (Product p : products) {
            List<ExtractedField> fields = extractedFieldRepository.findByProductId(p.getId());
            ProductListing listing = productListingRepository.findFirstByProductIdOrderByScrapedAtDesc(p.getId()).orElse(null);

            ProductComplianceContext ctx = new ProductComplianceContext(p, listing, null, null, fields);
            RuleResult res = evaluator.evaluate(rule, ctx);

            boolean isViolation = res != null && !res.isCompliant();
            if (isViolation) flaggedCount++;

            outcomes.add(new SimulationOutcomeDto(
                    p.getId(),
                    p.getName(),
                    isViolation,
                    res != null ? res.getDetectedValue() : "Compliant",
                    res != null ? res.getExpectedCondition() : "None",
                    res != null ? res.getRemediationMessage() : "Compliant"
            ));
        }

        Map<String, Object> response = new HashMap<>();
        response.put("testedCount", products.size());
        response.put("flaggedCount", flaggedCount);
        response.put("flaggedRatePct", products.size() > 0 ? Math.round(((double) flaggedCount / products.size()) * 1000.0) / 10.0 : 0.0);
        response.put("ruleTested", rule.getRuleId() + " (" + rule.getRuleName() + ")");
        response.put("outcomes", outcomes);

        return ResponseEntity.ok(response);
    }
}
