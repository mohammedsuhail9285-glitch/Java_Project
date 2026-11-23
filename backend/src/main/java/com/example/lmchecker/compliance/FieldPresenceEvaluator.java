package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.ExtractedField;
import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.Rule;
import com.example.lmchecker.entity.ValidationType;
import org.springframework.stereotype.Component;

@Component
public class FieldPresenceEvaluator implements RuleEvaluator {

    @Override
    public ValidationType getSupportedType() {
        return ValidationType.FIELD_PRESENCE;
    }

    @Override
    public RuleResult evaluate(Rule rule, ProductComplianceContext context) {
        String expectedField = rule.getExpectedField();
        if (expectedField == null || expectedField.trim().isEmpty()) {
            return RuleResult.compliant(rule.getRuleId());
        }

        ExtractedField field = context.hasPackageImage()
                ? context.getImageExtractedField(expectedField)
                : context.getExtractedField(expectedField);

        if (field == null || field.getRawValue() == null || field.getRawValue().trim().isEmpty()) {
            // Check raw OCR text as a secondary check before declaring missing
            String rawText = context.getRawOCRText().toLowerCase();
            boolean foundInRaw = checkRawTextSecondary(expectedField, rawText);

            if (!foundInRaw) {
                return RuleResult.violation(
                        rule.getRuleId(),
                        rule.getSeverity(),
                        "Field absent on package image",
                        "Mandatory statutory declaration of '" + expectedField + "' must be conspicuously displayed on the Principal Display Panel as per LMPC Rules.",
                        92.0,
                        null,
                        rule.getRemediationMessage() != null ? rule.getRemediationMessage() :
                                "Ensure '" + expectedField + "' is clearly printed on the primary package label in legible font size matching Schedule II.",
                        rule.getSource() != null ? rule.getSource() : "LMPC Rules 2011, Rule 6(1)",
                        FieldSource.IMAGE
                );
            }
        }

        // Passed presence check
        return RuleResult.compliant(rule.getRuleId());
    }

    private static final java.util.regex.Pattern MRP_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:mrp|maximum\\s*retail\\s*price|rsp)\\b\\s*[:\\-]?\\s*(?:rs\\.?|inr|₹)?\\s*[0-9]+");
    private static final java.util.regex.Pattern NET_QTY_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:net\\s*(?:qty|quantity|wt|weight|vol|volume))\\b\\s*[:\\-]?\\s*[0-9]+(?:\\.[0-9]+)?\\s*(?:kg|g|gm|gms|l|ml|m|cm|pcs|pieces|units)");
    private static final java.util.regex.Pattern MFG_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:mfg\\s*(?:by|pkd|&)|manufactured\\s*by|packed\\s*by|marketed\\s*by|imported\\s*by)\\b\\s*[:\\-]?\\s*[A-Za-z0-9]");
    private static final java.util.regex.Pattern CARE_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:consumer\\s*care|customer\\s*care|care\\s*cell|helpline)\\b|\\b1800[- ]?\\d{3}[- ]?\\d{4}\\b|\\b[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\\b");
    private static final java.util.regex.Pattern ORIGIN_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:country\\s*of\\s*origin|made\\s*in\\s*[a-z]+|origin\\s*:\\s*[a-z]+)\\b");
    private static final java.util.regex.Pattern DATE_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:mfg\\s*date|date\\s*of\\s*(?:mfg|packing|pkd)|pkd\\s*date|best\\s*before)\\b|\\b(?:0?[1-9]|1[0-2])[/-](?:20)?\\d{2}\\b");
    private static final java.util.regex.Pattern USP_RAW_PATTERN =
            java.util.regex.Pattern.compile("(?i)\\b(?:unit\\s*sale\\s*price|usp)\\b\\s*[:\\-]?\\s*(?:rs\\.?|inr|₹)?\\s*[0-9]+");

    private boolean checkRawTextSecondary(String fieldName, String rawText) {
        if (rawText == null || rawText.isBlank()) return false;
        String f = fieldName.toUpperCase();
        switch (f) {
            case "MRP":
                return MRP_RAW_PATTERN.matcher(rawText).find();
            case "NET_QTY":
                return NET_QTY_RAW_PATTERN.matcher(rawText).find();
            case "MANUFACTURER":
                return MFG_RAW_PATTERN.matcher(rawText).find();
            case "CONSUMER_CARE":
                return CARE_RAW_PATTERN.matcher(rawText).find();
            case "COUNTRY_OF_ORIGIN":
                return ORIGIN_RAW_PATTERN.matcher(rawText).find();
            case "MFG_DATE":
                return DATE_RAW_PATTERN.matcher(rawText).find();
            case "UNIT_SALE_PRICE":
                return USP_RAW_PATTERN.matcher(rawText).find();
            default:
                return false;
        }
    }
}
