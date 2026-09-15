package com.sih.navisense.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BacktestSummaryDto {
    private int totalVoyagesAnalyzed;
    private BigDecimal totalSpotSpendInr;
    private String totalSpotSpendInrFormatted;
    private BigDecimal totalNavisenseSpendInr;
    private String totalNavisenseSpendInrFormatted;
    private BigDecimal cumulativeSavingsInr;
    private String cumulativeSavingsInrFormatted;
    private double overallSavingsPercentage;
    private double hitRatioPercentage; // % of times NaviSense beat naive spot rate
    private List<BacktestRecordDto> records = new ArrayList<>();

    public BacktestSummaryDto() {}

    public int getTotalVoyagesAnalyzed() { return totalVoyagesAnalyzed; }
    public void setTotalVoyagesAnalyzed(int totalVoyagesAnalyzed) { this.totalVoyagesAnalyzed = totalVoyagesAnalyzed; }

    public BigDecimal getTotalSpotSpendInr() { return totalSpotSpendInr; }
    public void setTotalSpotSpendInr(BigDecimal totalSpotSpendInr) { this.totalSpotSpendInr = totalSpotSpendInr; }

    public String getTotalSpotSpendInrFormatted() { return totalSpotSpendInrFormatted; }
    public void setTotalSpotSpendInrFormatted(String totalSpotSpendInrFormatted) { this.totalSpotSpendInrFormatted = totalSpotSpendInrFormatted; }

    public BigDecimal getTotalNavisenseSpendInr() { return totalNavisenseSpendInr; }
    public void setTotalNavisenseSpendInr(BigDecimal totalNavisenseSpendInr) { this.totalNavisenseSpendInr = totalNavisenseSpendInr; }

    public String getTotalNavisenseSpendInrFormatted() { return totalNavisenseSpendInrFormatted; }
    public void setTotalNavisenseSpendInrFormatted(String totalNavisenseSpendInrFormatted) { this.totalNavisenseSpendInrFormatted = totalNavisenseSpendInrFormatted; }

    public BigDecimal getCumulativeSavingsInr() { return cumulativeSavingsInr; }
    public void setCumulativeSavingsInr(BigDecimal cumulativeSavingsInr) { this.cumulativeSavingsInr = cumulativeSavingsInr; }

    public String getCumulativeSavingsInrFormatted() { return cumulativeSavingsInrFormatted; }
    public void setCumulativeSavingsInrFormatted(String cumulativeSavingsInrFormatted) { this.cumulativeSavingsInrFormatted = cumulativeSavingsInrFormatted; }

    public double getOverallSavingsPercentage() { return overallSavingsPercentage; }
    public void setOverallSavingsPercentage(double overallSavingsPercentage) { this.overallSavingsPercentage = overallSavingsPercentage; }

    public double getHitRatioPercentage() { return hitRatioPercentage; }
    public void setHitRatioPercentage(double hitRatioPercentage) { this.hitRatioPercentage = hitRatioPercentage; }

    public List<BacktestRecordDto> getRecords() { return records; }
    public void setRecords(List<BacktestRecordDto> records) { this.records = records; }
}
