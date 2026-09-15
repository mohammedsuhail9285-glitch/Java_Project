package com.sih.navisense.dto;

public class FactorContributionDto {
    private String factor;
    private int contribution; // e.g. +28, -12
    private String impactType; // POSITIVE, NEGATIVE
    private String explanation;

    public FactorContributionDto() {}

    public FactorContributionDto(String factor, int contribution, String impactType, String explanation) {
        this.factor = factor;
        this.contribution = contribution;
        this.impactType = impactType;
        this.explanation = explanation;
    }

    public String getFactor() { return factor; }
    public void setFactor(String factor) { this.factor = factor; }

    public int getContribution() { return contribution; }
    public void setContribution(int contribution) { this.contribution = contribution; }

    public String getImpactType() { return impactType; }
    public void setImpactType(String impactType) { this.impactType = impactType; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }
}
