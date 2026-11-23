package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.ComplianceCheck;
import com.example.lmchecker.entity.ExtractedField;
import com.example.lmchecker.entity.RiskLevel;
import com.example.lmchecker.entity.Severity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Component
public class ComplianceScorer {

    private static final Set<String> MANDATORY_STATUTORY_FIELDS = Set.of(
            "MRP", "NET_QTY", "MANUFACTURER", "CONSUMER_CARE", "COUNTRY_OF_ORIGIN", "MFG_DATE"
    );

    public static class ScoreComputation {
        private double overallScore;
        private double mandatoryFieldCompletionPct;
        private int criticalCount;
        private int majorCount;
        private int minorCount;
        private RiskLevel riskLevel;
        private String summaryNotes;

        public double getOverallScore() { return overallScore; }
        public double getMandatoryFieldCompletionPct() { return mandatoryFieldCompletionPct; }
        public int getCriticalCount() { return criticalCount; }
        public int getMajorCount() { return majorCount; }
        public int getMinorCount() { return minorCount; }
        public RiskLevel getRiskLevel() { return riskLevel; }
        public String getSummaryNotes() { return summaryNotes; }
    }

    public ScoreComputation computeScore(List<RuleResult> ruleResults, ProductComplianceContext context) {
        ScoreComputation sc = new ScoreComputation();

        int critical = 0;
        int major = 0;
        int minor = 0;

        for (RuleResult r : ruleResults) {
            if (!r.isCompliant() && r.getSeverity() != null) {
                switch (r.getSeverity()) {
                    case CRITICAL: critical++; break;
                    case MAJOR: major++; break;
                    case MINOR: minor++; break;
                    default: break;
                }
            }
        }

        sc.criticalCount = critical;
        sc.majorCount = major;
        sc.minorCount = minor;

        // Base score starts at 100, deduction based on weighted severities
        double score = 100.0 - (critical * 35.0) - (major * 20.0) - (minor * 10.0);
        if (score < 0.0) score = 0.0;
        sc.overallScore = Math.round(score * 10.0) / 10.0;

        // Mandatory field completion percentage
        int presentMandatory = 0;
        for (String field : MANDATORY_STATUTORY_FIELDS) {
            if (context.hasExtractedField(field)) {
                presentMandatory++;
            }
        }
        sc.mandatoryFieldCompletionPct = Math.round(((double) presentMandatory / MANDATORY_STATUTORY_FIELDS.size()) * 1000.0) / 10.0;

        // Determine Risk Level
        if (critical > 0 || sc.overallScore < 50.0) {
            sc.riskLevel = RiskLevel.CRITICAL;
        } else if (major > 0 || sc.overallScore < 75.0) {
            sc.riskLevel = RiskLevel.HIGH;
        } else if (minor > 0 || sc.overallScore < 90.0) {
            sc.riskLevel = RiskLevel.MEDIUM;
        } else {
            sc.riskLevel = RiskLevel.LOW;
        }

        // Generate non-determination summary notes
        StringBuilder notes = new StringBuilder();
        if (critical > 0 || major > 0 || minor > 0) {
            notes.append(String.format("Automated evaluation detected %d potential issue(s) (%d critical, %d major, %d minor). ",
                    (critical + major + minor), critical, major, minor));
            notes.append(String.format("Statutory field declaration completeness is at %.1f%%. ", sc.mandatoryFieldCompletionPct));
            notes.append("System flags product as 'Requires Review' / 'Potential Non-Compliance' for regulator verification.");
        } else {
            notes.append(String.format("Passed all automated compliance verification checks (Score: %.1f/100, Completeness: %.1f%%). ",
                    sc.overallScore, sc.mandatoryFieldCompletionPct));
            notes.append("No prima facie statutory deviations observed.");
        }

        sc.summaryNotes = notes.toString();
        return sc;
    }
}
