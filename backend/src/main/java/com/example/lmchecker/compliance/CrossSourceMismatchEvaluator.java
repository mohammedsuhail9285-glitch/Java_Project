package com.example.lmchecker.compliance;

import com.example.lmchecker.ai.FieldNormalizer;
import com.example.lmchecker.entity.*;
import org.springframework.stereotype.Component;

@Component
public class CrossSourceMismatchEvaluator implements RuleEvaluator {

    private final FieldNormalizer fieldNormalizer;

    public CrossSourceMismatchEvaluator(FieldNormalizer fieldNormalizer) {
        this.fieldNormalizer = fieldNormalizer;
    }

    @Override
    public ValidationType getSupportedType() {
        return ValidationType.CROSS_SOURCE_MISMATCH;
    }

    @Override
    public RuleResult evaluate(Rule rule, ProductComplianceContext context) {
        Product product = context.getProduct();
        if (product == null) {
            return RuleResult.compliant(rule.getRuleId());
        }

        String fieldName = rule.getExpectedField() != null ? rule.getExpectedField().toUpperCase() : "MRP";
        ExtractedField extracted = context.getImageExtractedField(fieldName);
        if (extracted == null) {
            extracted = context.getExtractedField(fieldName);
        }

        if (extracted == null || extracted.getNormalizedValue() == null) {
            return RuleResult.compliant(rule.getRuleId());
        }

        if ("MRP".equals(fieldName) || "PRICE".equals(fieldName)) {
            return evaluatePriceMismatch(rule, product, extracted, context);
        } else if ("NET_QTY".equals(fieldName) || "QUANTITY".equals(fieldName)) {
            return evaluateQuantityMismatch(rule, product, extracted, context);
        } else if ("COUNTRY_OF_ORIGIN".equals(fieldName)) {
            return evaluateOriginMismatch(rule, product, extracted, context);
        }

        return RuleResult.compliant(rule.getRuleId());
    }

    private RuleResult evaluatePriceMismatch(Rule rule, Product product, ExtractedField extracted, ProductComplianceContext context) {
        Double listingPrice = product.getListingPrice();
        Double declaredMrp = product.getDeclaredMrp();

        if ((listingPrice == null || listingPrice == 0.0) && context.getListingExtractedField("MRP") != null) {
            listingPrice = fieldNormalizer.normalizePrice(context.getListingExtractedField("MRP").getNormalizedValue());
        }
        if ((declaredMrp == null || declaredMrp == 0.0) && context.getListingExtractedField("MRP") != null) {
            declaredMrp = fieldNormalizer.normalizePrice(context.getListingExtractedField("MRP").getNormalizedValue());
        }

        Double packageMrp = fieldNormalizer.normalizePrice(extracted.getNormalizedValue());

        if (packageMrp != null) {
            // Case 1: Overcharging - Selling price exceeds marked package MRP (LM Act Sec 36)
            if (listingPrice != null && listingPrice > (packageMrp + 0.01)) {
                String detected = String.format("Listing Price: ₹%.2f vs Package Label MRP: ₹%.2f (Overcharge: +₹%.2f)",
                        listingPrice, packageMrp, (listingPrice - packageMrp));
                return RuleResult.violation(
                        rule.getRuleId(),
                        Severity.CRITICAL,
                        detected,
                        "E-commerce listing price (₹" + listingPrice + ") must not exceed the Maximum Retail Price (₹" + packageMrp + ") declared on package label as per Legal Metrology Act Sec 36.",
                        96.0,
                        extracted.getBoundingBox(),
                        "Update the digital listing price immediately to not exceed the declared package MRP of ₹" + packageMrp + ".",
                        rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 6(1)(e) & LM Act Sec 36",
                        FieldSource.LISTING
                );
            }

            // Case 2: Divergence - Marketplace declared MRP differs from physical package label MRP (Rule 6(1)(e) / LM008)
            Double catalogMrp = declaredMrp != null ? declaredMrp : listingPrice;
            if (catalogMrp != null && Math.abs(catalogMrp - packageMrp) > 0.01) {
                String detected = String.format("Marketplace Declared MRP: ₹%.2f vs Physical Package MRP: ₹%.2f (Discrepancy: ₹%.2f)",
                        catalogMrp, packageMrp, Math.abs(catalogMrp - packageMrp));
                return RuleResult.violation(
                        rule.getRuleId(),
                        Severity.MAJOR,
                        detected,
                        "Marketplace declared MRP (₹" + catalogMrp + ") diverges from the Maximum Retail Price (₹" + packageMrp + ") marked on the physical package label under LMPC Rule 6(1)(e).",
                        95.0,
                        extracted.getBoundingBox(),
                        "Reconcile digital marketplace declared MRP with the physical commodity package declaration.",
                        rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 6(1)(e)",
                        FieldSource.LISTING
                );
            }
        }
        return RuleResult.compliant(rule.getRuleId());
    }

    private RuleResult evaluateQuantityMismatch(Rule rule, Product product, ExtractedField extracted, ProductComplianceContext context) {
        String desc = product.getDescription();
        String name = product.getName();
        String textToSearch = (name != null ? name : "") + " " + (desc != null ? desc : "");
        if (context.getListingExtractedField("NET_QTY") != null) {
            textToSearch += " " + context.getListingExtractedField("NET_QTY").getRawValue();
        }

        FieldNormalizer.NormalizedQuantity listNorm = fieldNormalizer.normalizeQuantity(textToSearch);
        FieldNormalizer.NormalizedQuantity pkgNorm = fieldNormalizer.normalizeQuantity(extracted.getRawValue());

        if (listNorm != null && pkgNorm != null) {
            if (Math.abs(listNorm.getValueInBaseUnit() - pkgNorm.getValueInBaseUnit()) > 1.0) {
                String detected = String.format("Listing declared: %s (%s) vs Physical package declared: %s (%s)",
                        listNorm.getNumericValue() + " " + listNorm.getStandardUnit(), listNorm.getStandardUnit(),
                        extracted.getRawValue(), pkgNorm.getStandardUnit());
                return RuleResult.violation(
                        rule.getRuleId(),
                        Severity.MAJOR,
                        detected,
                        "Listing net quantity must match the net quantity marked on the package label.",
                        94.0,
                        extracted.getBoundingBox(),
                        "Reconcile listing product description to match the package label net quantity (" + extracted.getRawValue() + ").",
                        rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 6(1)(c)",
                        FieldSource.LISTING
                );
            }
        }
        return RuleResult.compliant(rule.getRuleId());
    }

    private RuleResult evaluateOriginMismatch(Rule rule, Product product, ExtractedField extracted, ProductComplianceContext context) {
        String listingOrigin = product.getCountryOfOrigin();
        if ((listingOrigin == null || listingOrigin.isBlank()) && context.getListingExtractedField("COUNTRY_OF_ORIGIN") != null) {
            listingOrigin = context.getListingExtractedField("COUNTRY_OF_ORIGIN").getNormalizedValue();
        }
        String pkgOrigin = extracted.getNormalizedValue();

        if (listingOrigin != null && pkgOrigin != null) {
            double sim = fieldNormalizer.calculateSimilarity(listingOrigin, pkgOrigin);
            if (sim < 0.6) {
                String detected = String.format("Listing Country of Origin: '%s' vs Package Label Country: '%s'",
                        listingOrigin, pkgOrigin);
                return RuleResult.violation(
                        rule.getRuleId(),
                        Severity.MAJOR,
                        detected,
                        "Country of origin declared on the e-commerce marketplace must match the package physical label.",
                        90.0,
                        extracted.getBoundingBox(),
                        "Ensure Country of Origin on digital listing accurately reflects physical manufacturing origin.",
                        rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 6(1)(n)",
                        FieldSource.LISTING
                );
            }
        }
        return RuleResult.compliant(rule.getRuleId());
    }
}
