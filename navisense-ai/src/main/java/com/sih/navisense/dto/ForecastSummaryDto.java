package com.sih.navisense.dto;

import java.util.ArrayList;
import java.util.List;

public class ForecastSummaryDto {
    private String originCountry;
    private String destinationPort;
    private String vesselClass;
    private double currentRateUsd;
    private String primaryTrend;
    private double annualizedVolatility;
    private String recommendedCharterWindow;
    private List<ForecastPointDto> forecastPoints = new ArrayList<>();
    private List<HistoricalDataPointDto> historicalPoints = new ArrayList<>();
    private EvaluationMetricsDto hybridModelMetrics;
    private EvaluationMetricsDto naiveBaselineMetrics;

    public ForecastSummaryDto() {}

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public String getVesselClass() { return vesselClass; }
    public void setVesselClass(String vesselClass) { this.vesselClass = vesselClass; }

    public double getCurrentRateUsd() { return currentRateUsd; }
    public void setCurrentRateUsd(double currentRateUsd) { this.currentRateUsd = currentRateUsd; }

    public String getPrimaryTrend() { return primaryTrend; }
    public void setPrimaryTrend(String primaryTrend) { this.primaryTrend = primaryTrend; }

    public double getAnnualizedVolatility() { return annualizedVolatility; }
    public void setAnnualizedVolatility(double annualizedVolatility) { this.annualizedVolatility = annualizedVolatility; }

    public String getRecommendedCharterWindow() { return recommendedCharterWindow; }
    public void setRecommendedCharterWindow(String recommendedCharterWindow) { this.recommendedCharterWindow = recommendedCharterWindow; }

    public List<ForecastPointDto> getForecastPoints() { return forecastPoints; }
    public void setForecastPoints(List<ForecastPointDto> forecastPoints) { this.forecastPoints = forecastPoints; }

    public List<HistoricalDataPointDto> getHistoricalPoints() { return historicalPoints; }
    public void setHistoricalPoints(List<HistoricalDataPointDto> historicalPoints) { this.historicalPoints = historicalPoints; }

    public EvaluationMetricsDto getHybridModelMetrics() { return hybridModelMetrics; }
    public void setHybridModelMetrics(EvaluationMetricsDto hybridModelMetrics) { this.hybridModelMetrics = hybridModelMetrics; }

    public EvaluationMetricsDto getNaiveBaselineMetrics() { return naiveBaselineMetrics; }
    public void setNaiveBaselineMetrics(EvaluationMetricsDto naiveBaselineMetrics) { this.naiveBaselineMetrics = naiveBaselineMetrics; }
}
