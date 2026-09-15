package com.sih.navisense.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

public class CurrencyFormatter {

    private static final DecimalFormat TWO_DEC = new DecimalFormat("#,##0.00");
    private static final BigDecimal CRORE = new BigDecimal("10000000"); // 1 Crore = 10 Million
    private static final BigDecimal LAKH = new BigDecimal("100000");    // 1 Lakh = 100 Thousand

    public static String formatInrCroresOrLakhs(BigDecimal amount) {
        if (amount == null) return "₹0.00";

        BigDecimal absVal = amount.abs();
        if (absVal.compareTo(CRORE) >= 0) {
            BigDecimal inCr = amount.divide(CRORE, 2, RoundingMode.HALF_UP);
            return "₹" + inCr.toPlainString() + " Cr";
        } else if (absVal.compareTo(LAKH) >= 0) {
            BigDecimal inLakh = amount.divide(LAKH, 2, RoundingMode.HALF_UP);
            return "₹" + inLakh.toPlainString() + " Lakh";
        } else {
            return "₹" + TWO_DEC.format(amount);
        }
    }

    public static String formatInr(BigDecimal amount) {
        return formatInrCroresOrLakhs(amount);
    }

    public static String formatInr(double amount) {
        return formatInrCroresOrLakhs(BigDecimal.valueOf(amount));
    }

    public static String formatUsd(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return "$" + TWO_DEC.format(amount);
    }

    public static String formatUsd(double amount) {
        return "$" + TWO_DEC.format(amount);
    }
}
