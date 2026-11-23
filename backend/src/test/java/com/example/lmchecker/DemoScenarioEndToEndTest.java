package com.example.lmchecker;

import com.example.lmchecker.compliance.*;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("dev")
@Transactional
public class DemoScenarioEndToEndTest {

    @Autowired
    private RuleExecutionEngine ruleExecutionEngine;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ViolationRepository violationRepository;

    private String loadDemoListingText(String filename) throws IOException {
        File file = new File("../demo-data/" + filename);
        if (!file.exists()) {
            file = new File("demo-data/" + filename);
        }
        assertTrue(file.exists(), "Demo file should exist: " + filename);
        return Files.readString(file.toPath());
    }

    @Test
    @DisplayName("Scenario 1: Fully Compliant Atta Label + Listing -> PASSED_AUTOMATED_CHECKS")
    void testScenario1_CompliantAtta() throws Exception {
        Product product = Product.builder()
                .name("Swarna Sharbati Whole Wheat Atta 5kg")
                .declaredMrp(275.0)
                .listingPrice(275.0)
                .countryOfOrigin("India")
                .description("100% whole wheat stone-ground atta, net quantity 5 kg, manufactured by Swarna Milling Pvt Ltd")
                .status("PENDING_SCAN")
                .build();
        product = productRepository.save(product);

        List<ExtractedField> fields = new ArrayList<>();
        // Packaging label fields (FieldSource.IMAGE)
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MRP).rawValue("Rs. 275.00").normalizedValue("275.0").unit("INR").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.NET_QTY).rawValue("5 kg").normalizedValue("5000").unit("g").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MANUFACTURER).rawValue("Swarna Milling Pvt Ltd, Pune - 411001").normalizedValue("Swarna Milling Pvt Ltd").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.COUNTRY_OF_ORIGIN).rawValue("India").normalizedValue("India").source(FieldSource.IMAGE).confidence(0.98).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MFG_DATE).rawValue("10/2026").normalizedValue("2026-10-01").source(FieldSource.IMAGE).confidence(0.90).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.CONSUMER_CARE).rawValue("care@swarna.in").normalizedValue("care@swarna.in").source(FieldSource.IMAGE).confidence(0.92).build());

        // Digital listing fields (FieldSource.LISTING)
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MRP).rawValue("Rs. 275.00").normalizedValue("275.0").unit("INR").source(FieldSource.LISTING).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.NET_QTY).rawValue("5 kg").normalizedValue("5000").unit("g").source(FieldSource.LISTING).confidence(0.95).build());

        ProductComplianceContext context = new ProductComplianceContext(product, null, null, null, fields);
        ComplianceCheck check = ruleExecutionEngine.evaluateProduct(context, 1L);

        assertNotNull(check);
        assertEquals(0, check.getCriticalCount(), "Compliant atta should have zero critical violations");
        assertEquals(0, check.getMajorCount(), "Compliant atta should have zero major violations");
        assertTrue(check.getOverallScore() >= 90.0, "Score should be >= 90");
        assertEquals("PASSED_AUTOMATED_CHECKS", product.getStatus(), "Status should be PASSED_AUTOMATED_CHECKS");
    }

    @Test
    @DisplayName("Scenario 2: Missing Mandatory MRP on Package -> Triggers LM003 Violation")
    void testScenario2_MissingMrpDetergent() throws Exception {
        Product product = Product.builder()
                .name("EcoSpark Laundry Powder 500g")
                .declaredMrp(null)
                .listingPrice(140.0)
                .countryOfOrigin("India")
                .description("Eco-friendly enzymatic laundry powder, net weight 500 g, manufactured by EcoSpark")
                .status("PENDING_SCAN")
                .build();
        product = productRepository.save(product);

        List<ExtractedField> fields = new ArrayList<>();
        // Packaging label: missing MRP declaration!
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.NET_QTY).rawValue("500 g").normalizedValue("500").unit("g").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MANUFACTURER).rawValue("EcoSpark Clean Solutions Ltd, Vadodara").normalizedValue("EcoSpark Clean Solutions Ltd").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.COUNTRY_OF_ORIGIN).rawValue("India").normalizedValue("India").source(FieldSource.IMAGE).confidence(0.98).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MFG_DATE).rawValue("09/2026").normalizedValue("2026-09-01").source(FieldSource.IMAGE).confidence(0.90).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.CONSUMER_CARE).rawValue("support@ecospark.in").normalizedValue("support@ecospark.in").source(FieldSource.IMAGE).confidence(0.92).build());

        // Digital listing may mention price Rs 140, but packaging lacks it
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MRP).rawValue("Rs. 140.00").normalizedValue("140.0").unit("INR").source(FieldSource.LISTING).confidence(0.95).build());

        ProductComplianceContext context = new ProductComplianceContext(product, null, null, null, fields);
        ComplianceCheck check = ruleExecutionEngine.evaluateProduct(context, 2L);

        assertNotNull(check);
        assertTrue(check.getCriticalCount() >= 1, "Should flag missing mandatory declaration as critical");
        assertEquals("POTENTIAL_NON_COMPLIANT", product.getStatus(), "Status should be POTENTIAL_NON_COMPLIANT");

        // Verify Rule LM003 is recorded in violations
        List<Violation> violations = violationRepository.findByProductId(product.getId());
        boolean hasLm003 = violations.stream().anyMatch(v -> "LM003".equals(v.getRuleId()));
        assertTrue(hasLm003, "Rule LM003 (MRP Presence) must be violated");
    }

    @Test
    @DisplayName("Scenario 3: Price Mismatch (Package MRP 299 vs Listing 350) -> Triggers LM008 Overcharge Violation")
    void testScenario3_PriceMismatchHoney() throws Exception {
        Product product = Product.builder()
                .name("PureDrops Himalayan Organic Raw Honey 500g")
                .declaredMrp(299.0)
                .listingPrice(350.0)
                .countryOfOrigin("India")
                .description("Pure raw unprocessed wildflower honey, net weight 500 g, packed by Himalayan Valley Apiaries")
                .status("PENDING_SCAN")
                .build();
        product = productRepository.save(product);

        List<ExtractedField> fields = new ArrayList<>();
        // Packaging label: marked MRP Rs. 299.00
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MRP).rawValue("Rs. 299.00").normalizedValue("299.0").unit("INR").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.NET_QTY).rawValue("500 g").normalizedValue("500").unit("g").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MANUFACTURER).rawValue("Himalayan Valley Apiaries, Dehradun").normalizedValue("Himalayan Valley Apiaries").source(FieldSource.IMAGE).confidence(0.95).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.COUNTRY_OF_ORIGIN).rawValue("India").normalizedValue("India").source(FieldSource.IMAGE).confidence(0.98).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MFG_DATE).rawValue("08/2026").normalizedValue("2026-08-01").source(FieldSource.IMAGE).confidence(0.90).build());
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.CONSUMER_CARE).rawValue("care@himalayanvalley.org").normalizedValue("care@himalayanvalley.org").source(FieldSource.IMAGE).confidence(0.92).build());

        // Digital listing: inflated price Rs. 350.00
        fields.add(ExtractedField.builder().productId(product.getId()).fieldType(FieldType.MRP).rawValue("Rs. 350.00").normalizedValue("350.0").unit("INR").source(FieldSource.LISTING).confidence(0.95).build());

        ProductComplianceContext context = new ProductComplianceContext(product, null, null, null, fields);
        ComplianceCheck check = ruleExecutionEngine.evaluateProduct(context, 3L);

        assertNotNull(check);
        assertTrue(check.getCriticalCount() >= 1, "Should flag overcharging as critical");
        assertEquals("POTENTIAL_NON_COMPLIANT", product.getStatus(), "Status should be POTENTIAL_NON_COMPLIANT");

        // Verify Rule LM008 is recorded in violations
        List<Violation> violations = violationRepository.findByProductId(product.getId());
        boolean hasLm008 = violations.stream().anyMatch(v -> "LM008".equals(v.getRuleId()));
        assertTrue(hasLm008, "Rule LM008 (Retail Price Mismatch / Overcharge) must be violated");
    }
}
