package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.Product;
import com.example.lmchecker.entity.Rule;
import com.example.lmchecker.entity.ValidationType;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListingOmissionEvaluator implements RuleEvaluator {

    @Override
    public ValidationType getSupportedType() {
        return ValidationType.LISTING_OMISSION;
    }

    @Override
    public RuleResult evaluate(Rule rule, ProductComplianceContext context) {
        Product product = context.getProduct();
        if (product == null) {
            return RuleResult.compliant(rule.getRuleId());
        }

        List<String> missingDisclosures = new ArrayList<>();

        if (product.getListingPrice() == null || product.getListingPrice() <= 0) {
            missingDisclosures.add("Retail Sale Price (MRP)");
        }
        if (product.getDescription() == null || product.getDescription().trim().isEmpty()) {
            missingDisclosures.add("Net Quantity & Specifications");
        }
        if (product.getCountryOfOrigin() == null || product.getCountryOfOrigin().trim().isEmpty()) {
            missingDisclosures.add("Country of Origin");
        }

        if (!missingDisclosures.isEmpty()) {
            String detected = "Missing digital disclosures on marketplace listing: " + String.join(", ", missingDisclosures);
            return RuleResult.violation(
                    rule.getRuleId(),
                    rule.getSeverity(),
                    detected,
                    "E-commerce entities must display all mandatory declarations on digital listings prior to consumer purchase.",
                    95.0,
                    null,
                    "Update the marketplace catalog listing to clearly present: " + String.join(", ", missingDisclosures) + ".",
                    rule.getSource() != null ? rule.getSource() : "Consumer Protection (E-Commerce) Rules 2020 & LMPC Rule 6",
                    FieldSource.LISTING
            );
        }

        return RuleResult.compliant(rule.getRuleId());
    }
}
