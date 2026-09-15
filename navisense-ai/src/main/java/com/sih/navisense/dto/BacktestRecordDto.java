package com.sih.navisense.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BacktestRecordDto {
    private LocalDate decisionDate;
    private String route;
    private String vesselClass;
    private double quantityTonnes;
    private double spotRateUsd;
    private double recommendedRateUsd;
    private BigDecimal actualSpotCostInr;
    private String actualSpotCostInrFormatted;
    private BigDecimal navisenseCostInr;
    private String navisenseCostInrFormatted;
    private BigDecimal savingsInr;
    private String savingsInrFormatted;
    private double savingsPct;
    private String decisionMade;

    public BacktestRecordDto() {}

    public LocalDate getDecisionDate() { return decisionDate; }
    public void setDecisionDate(LocalDate decisionDate) { this.decisionDate = decisionDate; }

    public String getRoute() { return route; }
    public void setRoute(String route) { this.route = route; }

    public String getVesselClass() { return vesselClass; }
    public void setVesselClass(String vesselClass) { this.vesselClass = vesselClass; }

    public double getQuantityTonnes() { return quantityTonnes; }
    public void setQuantityTonnes(double quantityTonnes) { this.quantityTonnes = quantityTonnes; }

    public double getSpotRateUsd() { return spotRateUsd; }
    public void setSpotRateUsd(double spotRateUsd) { this.spotRateUsd = spotRateUsd; }

    public double getRecommendedRateUsd() { return recommendedRateUsd; }
    public void setRecommendedRateUsd(double recommendedRateUsd) { this.recommendedRateUsd = recommendedRateUsd; }

    public BigDecimal getActualSpotCostInr() { return actualSpotCostInr; }
    public void setActualSpotCostInr(BigDecimal actualSpotCostInr) { this.actualSpotCostInr = actualSpotCostInr; }

    public String getActualSpotCostInrFormatted() { return actualSpotCostInrFormatted; }
    public void setActualSpotCostInrFormatted(String actualSpotCostInrFormatted) { this.actualSpotCostInrFormatted = actualSpotCostInrFormatted; }

    public BigDecimal getNavisenseCostInr() { return navisenseCostInr; }
    public void setNavisenseCostInr(BigDecimal navisenseCostInr) { this.navisenseCostInr = navisenseCostInr; }

    public String getNavisenseCostInrFormatted() { return navisenseCostInrFormatted; }
    public void setNavisenseCostInrFormatted(String navisenseCostInrFormatted) { this.navisenseCostInrFormatted = navisenseCostInrFormatted; }

    public BigDecimal getSavingsInr() { return savingsInr; }
    public void setSavingsInr(BigDecimal savingsInr) { this.savingsInr = savingsInr; }

    public String getSavingsInrFormatted() { return savingsInrFormatted; }
    public void setSavingsInrFormatted(String savingsInrFormatted) { this.savingsInrFormatted = savingsInrFormatted; }

    public double getSavingsPct() { return savingsPct; }
    public void setSavingsPct(double savingsPct) { this.savingsPct = savingsPct; }

    public String getDecisionMade() { return decisionMade; }
    public void setDecisionMade(String decisionMade) { this.decisionMade = decisionMade; }
}
