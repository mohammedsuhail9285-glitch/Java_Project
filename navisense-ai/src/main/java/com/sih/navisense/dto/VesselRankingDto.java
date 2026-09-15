package com.sih.navisense.dto;

import java.math.BigDecimal;

public class VesselRankingDto {
    private int rank;
    private String vesselName;
    private int score; // 0 - 100
    private boolean compatible;
    private String rejectionReason;
    private int voyagesRequired;
    private double capacityUtilization;
    private double estimatedCostPerTonneUsd;
    private BigDecimal totalCostInr;
    private String totalCostInrFormatted;
    private double voyageDays;
    private double handlingDays;
    private double idleRiskScore;
    private double relativeCo2Index;

    public VesselRankingDto() {}

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }

    public String getVesselName() { return vesselName; }
    public void setVesselName(String vesselName) { this.vesselName = vesselName; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isCompatible() { return compatible; }
    public void setCompatible(boolean compatible) { this.compatible = compatible; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public int getVoyagesRequired() { return voyagesRequired; }
    public void setVoyagesRequired(int voyagesRequired) { this.voyagesRequired = voyagesRequired; }

    public double getCapacityUtilization() { return capacityUtilization; }
    public void setCapacityUtilization(double capacityUtilization) { this.capacityUtilization = capacityUtilization; }

    public double getEstimatedCostPerTonneUsd() { return estimatedCostPerTonneUsd; }
    public void setEstimatedCostPerTonneUsd(double estimatedCostPerTonneUsd) { this.estimatedCostPerTonneUsd = estimatedCostPerTonneUsd; }

    public BigDecimal getTotalCostInr() { return totalCostInr; }
    public void setTotalCostInr(BigDecimal totalCostInr) { this.totalCostInr = totalCostInr; }

    public String getTotalCostInrFormatted() { return totalCostInrFormatted; }
    public void setTotalCostInrFormatted(String totalCostInrFormatted) { this.totalCostInrFormatted = totalCostInrFormatted; }

    public double getVoyageDays() { return voyageDays; }
    public void setVoyageDays(double voyageDays) { this.voyageDays = voyageDays; }

    public double getHandlingDays() { return handlingDays; }
    public void setHandlingDays(double handlingDays) { this.handlingDays = handlingDays; }

    public double getIdleRiskScore() { return idleRiskScore; }
    public void setIdleRiskScore(double idleRiskScore) { this.idleRiskScore = idleRiskScore; }

    public double getRelativeCo2Index() { return relativeCo2Index; }
    public void setRelativeCo2Index(double relativeCo2Index) { this.relativeCo2Index = relativeCo2Index; }
}
