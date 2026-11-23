package com.example.lmchecker.compliance;

import com.example.lmchecker.entity.FieldSource;
import com.example.lmchecker.entity.Severity;

public class RuleResult {
    private String ruleId;
    private boolean compliant;
    private Severity severity;
    private String detectedValue;
    private String expectedCondition;
    private double confidence;
    private String boundingBox;
    private String remediationMessage;
    private String legalCitation;
    private FieldSource source = FieldSource.IMAGE;

    public RuleResult() {}

    public static RuleResult compliant(String ruleId) {
        RuleResult r = new RuleResult();
        r.setRuleId(ruleId);
        r.setCompliant(true);
        r.setConfidence(95.0);
        return r;
    }

    public static RuleResult violation(String ruleId, Severity severity, String detectedValue,
                                      String expectedCondition, double confidence,
                                      String boundingBox, String remediationMessage,
                                      String legalCitation, FieldSource source) {
        RuleResult r = new RuleResult();
        r.setRuleId(ruleId);
        r.setCompliant(false);
        r.setSeverity(severity);
        r.setDetectedValue(detectedValue);
        r.setExpectedCondition(expectedCondition);
        r.setConfidence(confidence);
        r.setBoundingBox(boundingBox);
        r.setRemediationMessage(remediationMessage);
        r.setLegalCitation(legalCitation);
        r.setSource(source != null ? source : FieldSource.IMAGE);
        return r;
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public boolean isCompliant() { return compliant; }
    public void setCompliant(boolean compliant) { this.compliant = compliant; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public String getDetectedValue() { return detectedValue; }
    public void setDetectedValue(String detectedValue) { this.detectedValue = detectedValue; }

    public String getExpectedCondition() { return expectedCondition; }
    public void setExpectedCondition(String expectedCondition) { this.expectedCondition = expectedCondition; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public String getBoundingBox() { return boundingBox; }
    public void setBoundingBox(String boundingBox) { this.boundingBox = boundingBox; }

    public String getRemediationMessage() { return remediationMessage; }
    public void setRemediationMessage(String remediationMessage) { this.remediationMessage = remediationMessage; }

    public String getLegalCitation() { return legalCitation; }
    public void setLegalCitation(String legalCitation) { this.legalCitation = legalCitation; }

    public FieldSource getSource() { return source; }
    public void setSource(FieldSource source) { this.source = source; }
}
