package com.sih.navisense.dto;

import java.time.LocalDate;

public class ForecastPointDto {
    private LocalDate forecastDate;
    private int horizonDays; // 7, 14, 30, 60, 90
    private double predictedRateUsd;
    private double lowerBoundUsd;
    private double upperBoundUsd;
    private String trend; // UPWARD, DOWNWARD, STABLE
    private double volatility; // percentage
    private int confidence; // 0 - 100

    public ForecastPointDto() {}

    public ForecastPointDto(LocalDate forecastDate, int horizonDays, double predictedRateUsd,
                            double lowerBoundUsd, double upperBoundUsd, String trend,
                            double volatility, int confidence) {
        this.forecastDate = forecastDate;
        this.horizonDays = horizonDays;
        this.predictedRateUsd = predictedRateUsd;
        this.lowerBoundUsd = lowerBoundUsd;
        this.upperBoundUsd = upperBoundUsd;
        this.trend = trend;
        this.volatility = volatility;
        this.confidence = confidence;
    }

    public LocalDate getForecastDate() { return forecastDate; }
    public void setForecastDate(LocalDate forecastDate) { this.forecastDate = forecastDate; }

    public int getHorizonDays() { return horizonDays; }
    public void setHorizonDays(int horizonDays) { this.horizonDays = horizonDays; }

    public double getPredictedRateUsd() { return predictedRateUsd; }
    public void setPredictedRateUsd(double predictedRateUsd) { this.predictedRateUsd = predictedRateUsd; }

    public double getLowerBoundUsd() { return lowerBoundUsd; }
    public void setLowerBoundUsd(double lowerBoundUsd) { this.lowerBoundUsd = lowerBoundUsd; }

    public double getUpperBoundUsd() { return upperBoundUsd; }
    public void setUpperBoundUsd(double upperBoundUsd) { this.upperBoundUsd = upperBoundUsd; }

    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }

    public double getVolatility() { return volatility; }
    public void setVolatility(double volatility) { this.volatility = volatility; }

    public int getConfidence() { return confidence; }
    public void setConfidence(int confidence) { this.confidence = confidence; }
}
