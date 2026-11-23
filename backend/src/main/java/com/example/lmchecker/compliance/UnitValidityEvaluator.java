package com.example.lmchecker.compliance;

import com.example.lmchecker.ai.FieldNormalizer;
import com.example.lmchecker.entity.ExtractedField;
import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.Rule;
import com.example.lmchecker.entity.ValidationType;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Set;

@Component
public class UnitValidityEvaluator implements RuleEvaluator {

    private static final Set<String> APPROVED_STANDARD_UNITS = Set.of(
            "kg", "g", "l", "ml", "m", "cm", "mm", "piece", "unit", "units", "pcs"
    );

    private final FieldNormalizer fieldNormalizer;

    public UnitValidityEvaluator(FieldNormalizer fieldNormalizer) {
        this.fieldNormalizer = fieldNormalizer;
    }

    @Override
    public ValidationType getSupportedType() {
        return ValidationType.UNIT_VALIDITY;
    }

    @Override
    public RuleResult evaluate(Rule rule, ProductComplianceContext context) {
        ExtractedField qtyField = context.getExtractedField("NET_QTY");
        if (qtyField == null || qtyField.getRawValue() == null) {
            // Field presence will catch missing field, unit validity applies when field exists
            return RuleResult.compliant(rule.getRuleId());
        }

        // Check if unit field is explicitly set and non-standard
        if (qtyField.getUnit() != null && !qtyField.getUnit().trim().isEmpty()) {
            String u = qtyField.getUnit().toLowerCase(Locale.ROOT).trim();
            if (!APPROVED_STANDARD_UNITS.contains(u)) {
                return RuleResult.violation(
                        rule.getRuleId(),
                        rule.getSeverity(),
                        "Non-standard or missing unit in: '" + qtyField.getRawValue() + "' (Unit: " + qtyField.getUnit() + ")",
                        "Net quantity must use standard SI/metric units (kg, g, L, ml, piece) as mandated by Rule 11 & Schedule II.",
                        91.0,
                        qtyField.getBoundingBox(),
                        "Replace non-standard terms with legally sanctioned metric units (e.g., 'kg' instead of '" + qtyField.getUnit() + "').",
                        rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 11 & Schedule II",
                        FieldSource.IMAGE
                );
            }
        }

        FieldNormalizer.NormalizedQuantity nq = fieldNormalizer.normalizeQuantity(qtyField.getRawValue());
        if (nq == null || nq.getStandardUnit() == null) {
            return RuleResult.violation(
                    rule.getRuleId(),
                    rule.getSeverity(),
                    "Non-standard or missing unit in: '" + qtyField.getRawValue() + "'",
                    "Net quantity must use standard SI/metric units (kg, g, L, ml, piece) as mandated by Rule 11 & Schedule II.",
                    91.0,
                    qtyField.getBoundingBox(),
                    "Replace non-standard terms with legally sanctioned metric units (e.g., 'kg' instead of 'pouch/pkt').",
                    rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 11 & Schedule II",
                    FieldSource.IMAGE
            );
        }

        String unit = nq.getStandardUnit().toLowerCase();
        if (!APPROVED_STANDARD_UNITS.contains(unit)) {
            return RuleResult.violation(
                    rule.getRuleId(),
                    rule.getSeverity(),
                    "Unsanctioned unit: '" + unit + "' in '" + qtyField.getRawValue() + "'",
                    "Net quantity must conform strictly to legal metrology metric standards.",
                    90.0,
                    qtyField.getBoundingBox(),
                    "Express declaration strictly in grams, kilograms, liters, milliliters, meters, or numbers.",
                    rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 11",
                    FieldSource.IMAGE
            );
        }

        return RuleResult.compliant(rule.getRuleId());
    }
}
