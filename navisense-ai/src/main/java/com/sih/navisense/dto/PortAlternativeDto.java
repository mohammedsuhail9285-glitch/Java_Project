package com.sih.navisense.dto;

import java.math.BigDecimal;

public class PortAlternativeDto {
    private String portName;
    private double congestionIndex;
    private double maxDraft;
    private boolean accommodatesSelectedVessel;
    private BigDecimal expectedCostInr;
    private String expectedCostInrFormatted;
    private String deltaDescription;
    private String comparisonVerdict; // BETTER, COMPARABLE, HIGHER_COST

    public PortAlternativeDto() {}

    public PortAlternativeDto(String portName, double congestionIndex, double maxDraft,
                              boolean accommodatesSelectedVessel, BigDecimal expectedCostInr,
                              String expectedCostInrFormatted, String deltaDescription,
                              String comparisonVerdict) {
        this.portName = portName;
        this.congestionIndex = congestionIndex;
        this.maxDraft = maxDraft;
        this.accommodatesSelectedVessel = accommodatesSelectedVessel;
        this.expectedCostInr = expectedCostInr;
        this.expectedCostInrFormatted = expectedCostInrFormatted;
        this.deltaDescription = deltaDescription;
        this.comparisonVerdict = comparisonVerdict;
    }

    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }

    public double getCongestionIndex() { return congestionIndex; }
    public void setCongestionIndex(double congestionIndex) { this.congestionIndex = congestionIndex; }

    public double getMaxDraft() { return maxDraft; }
    public void setMaxDraft(Double maxDraft) { this.maxDraft = maxDraft; }

    public boolean isAccommodatesSelectedVessel() { return accommodatesSelectedVessel; }
    public void setAccommodatesSelectedVessel(boolean accommodatesSelectedVessel) { this.accommodatesSelectedVessel = accommodatesSelectedVessel; }

    public BigDecimal getExpectedCostInr() { return expectedCostInr; }
    public void setExpectedCostInr(BigDecimal expectedCostInr) { this.expectedCostInr = expectedCostInr; }

    public String getExpectedCostInrFormatted() { return expectedCostInrFormatted; }
    public void setExpectedCostInrFormatted(String expectedCostInrFormatted) { this.expectedCostInrFormatted = expectedCostInrFormatted; }

    public String getDeltaDescription() { return deltaDescription; }
    public void setDeltaDescription(String deltaDescription) { this.deltaDescription = deltaDescription; }

    public String getComparisonVerdict() { return comparisonVerdict; }
    public void setComparisonVerdict(String comparisonVerdict) { this.comparisonVerdict = comparisonVerdict; }
}
