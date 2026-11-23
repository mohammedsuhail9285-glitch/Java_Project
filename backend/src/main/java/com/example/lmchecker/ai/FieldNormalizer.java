package com.example.lmchecker.ai;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class FieldNormalizer {

    private static final Pattern PRICE_NUMBER_PATTERN = Pattern.compile("([0-9]+(?:\\.[0-9]{1,2})?)");
    private static final Pattern QTY_UNIT_PATTERN = Pattern.compile("([0-9]+(?:\\.[0-9]+)?)\\s*(kg|g|gm|gms|grams|l|ltr|litre|litres|ml|m|cm|pcs|pieces|piece|units|count)?", Pattern.CASE_INSENSITIVE);
    private static final Pattern DATE_PATTERN = Pattern.compile("([0-9]{1,2}[/-][0-9]{4}|[0-9]{4})");

    public Double normalizePrice(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        Matcher m = PRICE_NUMBER_PATTERN.matcher(raw.replace(",", ""));
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1));
            } catch (NumberFormatException ignored) {}
        }
        return null;
    }

    public static class NormalizedQuantity {
        private final Double numericValue;
        private final String standardUnit;
        private final Double valueInBaseUnit; // e.g. grams or milliliters

        public NormalizedQuantity(Double numericValue, String standardUnit, Double valueInBaseUnit) {
            this.numericValue = numericValue;
            this.standardUnit = standardUnit;
            this.valueInBaseUnit = valueInBaseUnit;
        }

        public Double getNumericValue() { return numericValue; }
        public String getStandardUnit() { return standardUnit; }
        public Double getValueInBaseUnit() { return valueInBaseUnit; }
    }

    public NormalizedQuantity normalizeQuantity(String raw) {
        if (raw == null || raw.trim().isEmpty()) return null;
        Matcher m = QTY_UNIT_PATTERN.matcher(raw.toLowerCase(Locale.ROOT));
        if (m.find()) {
            try {
                double val = Double.parseDouble(m.group(1));
                String unit = m.group(2) != null ? m.group(2).toLowerCase() : "";
                String stdUnit = null;
                double baseVal = val;

                if (unit.matches("kg|kilogram|kilograms")) {
                    stdUnit = "kg";
                    baseVal = val * 1000.0; // grams
                } else if (unit.matches("g|gm|gms|grams|gram")) {
                    stdUnit = "g";
                    baseVal = val;
                } else if (unit.matches("l|ltr|litre|litres|liter")) {
                    stdUnit = "L";
                    baseVal = val * 1000.0; // ml
                } else if (unit.matches("ml|millilitre|milliliter")) {
                    stdUnit = "ml";
                    baseVal = val;
                } else if (unit.matches("pcs|piece|pieces|units|count|unit")) {
                    stdUnit = "piece";
                    baseVal = val;
                } else {
                    stdUnit = unit.isEmpty() ? null : unit;
                }


                return new NormalizedQuantity(val, stdUnit, baseVal);
            } catch (Exception ignored) {}
        }
        return null;
    }

    public String normalizeDate(String raw) {
        if (raw == null) return null;
        Matcher m = DATE_PATTERN.matcher(raw);
        if (m.find()) {
            return m.group(1).replace("-", "/");
        }
        return raw.trim();
    }

    public double calculateSimilarity(String s1, String s2) {
        if (s1 == null && s2 == null) return 1.0;
        if (s1 == null || s2 == null) return 0.0;
        String clean1 = s1.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        String clean2 = s2.trim().toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        if (clean1.equals(clean2)) return 1.0;
        if (clean1.contains(clean2) || clean2.contains(clean1)) return 0.85;

        // Levenshtein distance based ratio
        int maxLen = Math.max(clean1.length(), clean2.length());
        if (maxLen == 0) return 1.0;
        int distance = levenshteinDistance(clean1, clean2);
        return 1.0 - ((double) distance / maxLen);
    }

    private int levenshteinDistance(String a, String b) {
        int[] costs = new int[b.length() + 1];
        for (int j = 0; j < costs.length; j++) costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]),
                        a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
