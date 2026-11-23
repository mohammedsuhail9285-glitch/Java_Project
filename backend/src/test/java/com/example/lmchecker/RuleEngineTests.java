package com.example.lmchecker;

import com.example.lmchecker.ai.FieldNormalizer;
import com.example.lmchecker.compliance.*;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class RuleEngineTests {

    @Autowired
    private FieldNormalizer fieldNormalizer;

    @Autowired
    private FieldPresenceEvaluator fieldPresenceEvaluator;

    @Autowired
    private CrossSourceMismatchEvaluator crossSourceMismatchEvaluator;

    @Autowired
    private UnitValidityEvaluator unitValidityEvaluator;

    @Autowired
    private DatePresenceEvaluator datePresenceEvaluator;

    @Autowired
    private ListingOmissionEvaluator listingOmissionEvaluator;

    @Autowired
    private ComplianceScorer complianceScorer;

    @Autowired
    private RuleExecutionEngine ruleExecutionEngine;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private ViolationRepository violationRepository;

    @Test
    @DisplayName("FieldPresenceEvaluator: Passes when statutory field is present, fails when absent")
    void testFieldPresence() {
        Rule rule = Rule.builder()
                .ruleId("LM001")
                .ruleName("MRP Declaration")
                .severity(Severity.CRITICAL)
                .validationType(ValidationType.FIELD_PRESENCE)
                .expectedField("MRP")
                .build();

        Product product = Product.builder().name("Test Atta").build();

        // Context 1: Field present
        ExtractedField mrpField = ExtractedField.builder()
                .fieldType(FieldType.MRP)
                .rawValue("Rs. 245.00")
                .normalizedValue("245.0")
                .build();
        ProductComplianceContext ctxWithMrp = new ProductComplianceContext(
                product, null, null, null, List.of(mrpField)
        );

        RuleResult res1 = fieldPresenceEvaluator.evaluate(rule, ctxWithMrp);
        assertTrue(res1.isCompliant(), "Should be compliant when MRP is present");

        // Context 2: Field missing
        ProductComplianceContext ctxWithoutMrp = new ProductComplianceContext(
                product, null, null, null, List.of()
        );
        RuleResult res2 = fieldPresenceEvaluator.evaluate(rule, ctxWithoutMrp);
        assertFalse(res2.isCompliant(), "Should flag potential non-compliance when MRP is missing");
        assertEquals(Severity.CRITICAL, res2.getSeverity());
    }

    @Test
    @DisplayName("CrossSourceMismatch: Flags critical violation when listing price exceeds package MRP")
    void testPriceOverchargeMismatch() {
        Rule rule = Rule.builder()
                .ruleId("LM008")
                .ruleName("Price Discrepancy")
                .severity(Severity.CRITICAL)
                .validationType(ValidationType.CROSS_SOURCE_MISMATCH)
                .expectedField("MRP")
                .build();

        // Listing price ₹300, but package MRP is ₹250
        Product product = Product.builder()
                .name("Overpriced Rice")
                .listingPrice(300.0)
                .build();

        ExtractedField mrpField = ExtractedField.builder()
                .fieldType(FieldType.MRP)
                .rawValue("Rs. 250.00")
                .normalizedValue("250.0")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(
                product, null, null, null, List.of(mrpField)
        );

        RuleResult res = crossSourceMismatchEvaluator.evaluate(rule, ctx);
        assertFalse(res.isCompliant(), "Should flag violation when listing price exceeds MRP");
        assertEquals(Severity.CRITICAL, res.getSeverity());
        assertTrue(res.getDetectedValue().contains("Listing Price: ₹300.00 vs Package Label MRP: ₹250.00"));
    }

    @Test
    @DisplayName("UnitValidityEvaluator: Flags non-standard metric unit")
    void testUnitValidity() {
        Rule rule = Rule.builder()
                .ruleId("LM002")
                .ruleName("Net Quantity Unit Validity")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.UNIT_VALIDITY)
                .expectedField("NET_QTY")
                .build();

        Product product = Product.builder().name("Spice Powder").build();

        // Non-standard unit like "box" or invalid quantity format
        ExtractedField invalidQty = ExtractedField.builder()
                .fieldType(FieldType.NET_QTY)
                .rawValue("1 pouch")
                .normalizedValue("1")
                .unit("pouch")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(
                product, null, null, null, List.of(invalidQty)
        );

        RuleResult res = unitValidityEvaluator.evaluate(rule, ctx);
        assertFalse(res.isCompliant(), "Should flag potential non-compliance for non-standard unit");
    }

    @Test
    @DisplayName("RuleExecutionEngine: End-to-end evaluation updates product status to POTENTIAL_NON_COMPLIANT")
    void testEndToEndRuleExecution() {
        Product product = Product.builder()
                .name("Organic Pulses 1kg")
                .listingPrice(299.0)
                .description("High protein organic pulses pack")
                .status("PENDING_SCAN")
                .createdAt(LocalDateTime.now())
                .build();
        product = productRepository.save(product);

        ExtractedField netQty = ExtractedField.builder()
                .productId(product.getId())
                .fieldType(FieldType.NET_QTY)
                .rawValue("1 kg")
                .normalizedValue("1")
                .unit("kg")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(
                product, null, null, null, List.of(netQty)
        );

        ComplianceCheck check = ruleExecutionEngine.evaluateProduct(ctx, 101L);

        assertNotNull(check);
        assertNotNull(check.getId());
        assertTrue(check.getOverallScore() < 90.0, "Score should reflect missing statutory declarations");
        assertTrue(check.getCriticalCount() > 0 || check.getMajorCount() > 0);

        // Product status must be updated adhering to non-determination copy
        Product updated = productRepository.findById(product.getId()).orElseThrow();
        assertEquals("POTENTIAL_NON_COMPLIANT", updated.getStatus());

        // Violations must be persisted
        List<Violation> vios = violationRepository.findByProductId(product.getId());
        assertFalse(vios.isEmpty(), "Violations must be generated for missing mandatory rules");
    }

    @Test
    @DisplayName("ListingOmissionEvaluator: Flags violation when Retail Sale Price (MRP) is missing from listing")
    void testListingOmissionMissingMrp() {
        Rule rule = Rule.builder()
                .ruleId("LM010")
                .ruleName("Listing Omission")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.LISTING_OMISSION)
                .build();

        Product product = Product.builder()
                .name("Test Honey")
                .listingPrice(null)
                .description("Pure Himalayan forest honey 500 g")
                .countryOfOrigin("India")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(product, null, null, null, List.of());
        RuleResult result = listingOmissionEvaluator.evaluate(rule, ctx);

        assertFalse(result.isCompliant(), "Should fail when listing price/MRP is missing");
        assertTrue(result.getDetectedValue().contains("Retail Sale Price (MRP)"));
    }

    @Test
    @DisplayName("ListingOmissionEvaluator: Flags violation when Net Quantity / Description is omitted from listing")
    void testListingOmissionMissingNetQuantity() {
        Rule rule = Rule.builder()
                .ruleId("LM010")
                .ruleName("Listing Omission")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.LISTING_OMISSION)
                .build();

        Product product = Product.builder()
                .name("Test Atta")
                .listingPrice(320.0)
                .description(null) // missing description containing net quantity
                .countryOfOrigin("India")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(product, null, null, null, List.of());
        RuleResult result = listingOmissionEvaluator.evaluate(rule, ctx);

        assertFalse(result.isCompliant(), "Should fail when description/net quantity is missing");
        assertTrue(result.getDetectedValue().contains("Net Quantity"));
    }

    @Test
    @DisplayName("ListingOmissionEvaluator: Fully compliant listing passes validation")
    void testListingOmissionFullyCompliant() {
        Rule rule = Rule.builder()
                .ruleId("LM010")
                .ruleName("Listing Omission")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.LISTING_OMISSION)
                .build();

        Product product = Product.builder()
                .name("Organic Wheat Flour 5kg")
                .listingPrice(320.0)
                .description("Net Quantity: 5 kg | 100% stone ground whole wheat flour")
                .countryOfOrigin("India")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(product, null, null, null, List.of());
        RuleResult result = listingOmissionEvaluator.evaluate(rule, ctx);

        assertTrue(result.isCompliant(), "Should pass when all required listing disclosures are present");
    }

    @Test
    @DisplayName("DatePresenceEvaluator: Flags violation when manufacture/packing date is missing from label")
    void testDatePresenceMissingManufactureDate() {
        Rule rule = Rule.builder()
                .ruleId("LM009")
                .ruleName("Date of Manufacture")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.DATE_PRESENCE)
                .expectedField("MFG_DATE")
                .build();

        Product product = Product.builder().name("Organic Tea").build();

        // No date field extracted and no date in OCR text
        OCRResult ocr = OCRResult.builder().rawText("PURE ORGANIC TEA\nNet Qty: 250 g\nMRP: Rs. 150.00").build();
        ProductComplianceContext ctx = new ProductComplianceContext(product, null, null, ocr, List.of());

        RuleResult result = datePresenceEvaluator.evaluate(rule, ctx);
        assertFalse(result.isCompliant(), "Should flag violation when manufacture date is missing");
        assertEquals(Severity.MAJOR, result.getSeverity());
    }

    @Test
    @DisplayName("DatePresenceEvaluator: Passes when manufacture date is extracted or found in raw text")
    void testDatePresencePresent() {
        Rule rule = Rule.builder()
                .ruleId("LM009")
                .ruleName("Date of Manufacture")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.DATE_PRESENCE)
                .expectedField("MFG_DATE")
                .build();

        Product product = Product.builder().name("Organic Tea").build();

        // 1. Present in extracted fields
        ExtractedField dateField = ExtractedField.builder()
                .fieldType(FieldType.MFG_DATE)
                .rawValue("08/2026")
                .normalizedValue("08/2026")
                .build();

        ProductComplianceContext ctxWithField = new ProductComplianceContext(product, null, null, null, List.of(dateField));
        assertTrue(datePresenceEvaluator.evaluate(rule, ctxWithField).isCompliant(), "Should pass when date field is extracted");

        // 2. Present in raw OCR text fallback
        OCRResult ocr = OCRResult.builder().rawText("Net Qty: 500 g\nMfg Date: 07/2026\nMRP Rs. 200").build();
        ProductComplianceContext ctxWithOcr = new ProductComplianceContext(product, null, null, ocr, List.of());
        assertTrue(datePresenceEvaluator.evaluate(rule, ctxWithOcr).isCompliant(), "Should pass when date is detected in OCR raw text");
    }

    @Test
    @DisplayName("CrossSourceMismatchEvaluator: Flags violation when listing MRP differs from physical package label MRP")
    void testListingMrpDifferentFromLabelMrp() {
        Rule rule = Rule.builder()
                .ruleId("LM008")
                .ruleName("Price Mismatch")
                .severity(Severity.MAJOR)
                .validationType(ValidationType.CROSS_SOURCE_MISMATCH)
                .expectedField("MRP")
                .build();

        // Listing declared MRP is ₹350, but label is ₹300
        Product product = Product.builder()
                .name("Almond Milk 1L")
                .declaredMrp(350.0)
                .listingPrice(290.0)
                .build();

        ExtractedField labelMrp = ExtractedField.builder()
                .fieldType(FieldType.MRP)
                .rawValue("MRP Rs. 300.00")
                .normalizedValue("300.0")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(product, null, null, null, List.of(labelMrp));
        RuleResult result = crossSourceMismatchEvaluator.evaluate(rule, ctx);

        assertFalse(result.isCompliant(), "Should flag mismatch when marketplace declared MRP differs from label MRP");
        assertTrue(result.getDetectedValue().contains("Marketplace Declared MRP: ₹350.00 vs Physical Package MRP: ₹300.00"));
    }

    @Test
    @DisplayName("CrossSourceMismatchEvaluator: Normalizes '500 g' vs '500 gm' and passes without false positive")
    void testQuantityGramUnitNormalization() {
        Rule rule = Rule.builder()
                .ruleId("LM007")
                .ruleName("Net Quantity Parity")
                .severity(Severity.CRITICAL)
                .validationType(ValidationType.CROSS_SOURCE_MISMATCH)
                .expectedField("NET_QTY")
                .build();

        // Listing declares "500 g"
        Product product = Product.builder()
                .name("Organic Honey 500 g")
                .description("Pure natural honey in 500 g jar")
                .build();

        // Label OCR extracted "500 gm"
        ExtractedField labelQty = ExtractedField.builder()
                .fieldType(FieldType.NET_QTY)
                .rawValue("500 gm")
                .normalizedValue("500")
                .unit("gm")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(product, null, null, null, List.of(labelQty));
        RuleResult result = crossSourceMismatchEvaluator.evaluate(rule, ctx);

        assertTrue(result.isCompliant(), "'500 g' on listing and '500 gm' on label must normalize to identical base units (500 grams)");
    }

    @Test
    @DisplayName("Comprehensive Evaluation: Fully compliant product passes all evaluators")
    void testFullyCompliantProduct() {
        Product product = Product.builder()
                .name("Standard Whole Wheat Atta 5kg")
                .declaredMrp(320.0)
                .listingPrice(320.0)
                .description("Net Quantity: 5 kg | 100% stone ground whole wheat flour")
                .countryOfOrigin("India")
                .build();

        ExtractedField labelMrp = ExtractedField.builder()
                .fieldType(FieldType.MRP)
                .rawValue("MRP Rs. 320.00 (Incl. of all taxes)")
                .normalizedValue("320.0")
                .build();

        ExtractedField labelQty = ExtractedField.builder()
                .fieldType(FieldType.NET_QTY)
                .rawValue("5 kg")
                .normalizedValue("5")
                .unit("kg")
                .build();

        ExtractedField dateField = ExtractedField.builder()
                .fieldType(FieldType.MFG_DATE)
                .rawValue("08/2026")
                .normalizedValue("08/2026")
                .build();

        ExtractedField originField = ExtractedField.builder()
                .fieldType(FieldType.COUNTRY_OF_ORIGIN)
                .rawValue("Country of Origin: India")
                .normalizedValue("India")
                .build();

        ProductComplianceContext ctx = new ProductComplianceContext(
                product, null, null, null,
                List.of(labelMrp, labelQty, dateField, originField)
        );

        // 1. Listing Omission Check
        Rule listingRule = Rule.builder().ruleId("LM010").validationType(ValidationType.LISTING_OMISSION).build();
        assertTrue(listingOmissionEvaluator.evaluate(listingRule, ctx).isCompliant(), "Listing omission check must pass");

        // 2. Date Presence Check
        Rule dateRule = Rule.builder().ruleId("LM009").validationType(ValidationType.DATE_PRESENCE).expectedField("MFG_DATE").build();
        assertTrue(datePresenceEvaluator.evaluate(dateRule, ctx).isCompliant(), "Date presence check must pass");

        // 3. Cross-Source MRP Check
        Rule priceMismatchRule = Rule.builder().ruleId("LM008").validationType(ValidationType.CROSS_SOURCE_MISMATCH).expectedField("MRP").build();
        assertTrue(crossSourceMismatchEvaluator.evaluate(priceMismatchRule, ctx).isCompliant(), "Price mismatch check must pass");

        // 4. Cross-Source Net Qty Check
        Rule qtyMismatchRule = Rule.builder().ruleId("LM007").validationType(ValidationType.CROSS_SOURCE_MISMATCH).expectedField("NET_QTY").build();
        assertTrue(crossSourceMismatchEvaluator.evaluate(qtyMismatchRule, ctx).isCompliant(), "Qty mismatch check must pass");

        // 5. Cross-Source Country of Origin Check
        Rule originMismatchRule = Rule.builder().ruleId("LM004").validationType(ValidationType.CROSS_SOURCE_MISMATCH).expectedField("COUNTRY_OF_ORIGIN").build();
        assertTrue(crossSourceMismatchEvaluator.evaluate(originMismatchRule, ctx).isCompliant(), "Origin mismatch check must pass");
    }
}
