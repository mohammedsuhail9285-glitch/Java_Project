package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.ExtractedField;
import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.Rule;
import com.example.lmchecker.entity.ValidationType;
import org.springframework.stereotype.Component;

@Component
public class DatePresenceEvaluator implements RuleEvaluator {

    @Override
    public ValidationType getSupportedType() {
        return ValidationType.DATE_PRESENCE;
    }

    @Override
    public RuleResult evaluate(Rule rule, ProductComplianceContext context) {
        ExtractedField dateField = context.getExtractedField("MFG_DATE");
        if (dateField != null && dateField.getRawValue() != null && !dateField.getRawValue().trim().isEmpty()) {
            return RuleResult.compliant(rule.getRuleId());
        }

        String rawText = context.getRawOCRText();
        if (rawText != null && !rawText.isBlank()) {
            java.util.regex.Pattern datePattern = java.util.regex.Pattern.compile(
                "(?i)\\b(?:mfg\\s*date|date\\s*of\\s*(?:mfg|packing|pkd)|pkd\\s*date|best\\s*before)\\b|\\b(?:0?[1-9]|1[0-2])[/-](?:20)?\\d{2}\\b"
            );
            if (datePattern.matcher(rawText).find()) {
                return RuleResult.compliant(rule.getRuleId());
            }
        }

        return RuleResult.violation(
                rule.getRuleId(),
                rule.getSeverity(),
                "Month and Year of manufacture/packing not detected on package label",
                "Every package must bear the month and year in which the commodity is manufactured or pre-packed as per Rule 6(1)(d).",
                93.0,
                null,
                "Prominently print the month and year of manufacture or pre-packing (e.g. '09/2023' or 'Sep 2023') on the primary display panel.",
                rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 6(1)(d)",
                FieldSource.IMAGE
        );
    }
}
