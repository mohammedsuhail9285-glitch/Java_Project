package com.sih.navisense.dto;

public class RouteHeatmapDto {
    private String originCountry;
    private String loadingPort;
    private String destinationPort;
    private double currentRateUsd;
    private double forecastRateUsd;
    private double distanceNm;
    private double congestionIndex;
    private double volatility;
    private double routeRiskScore;
    private String attractiveness; // ATTRACTIVE, NEUTRAL, EXPENSIVE, HIGH_RISK
    private String badgeClass;

    public RouteHeatmapDto() {}

    public RouteHeatmapDto(String originCountry, String loadingPort, String destinationPort,
                           double currentRateUsd, double forecastRateUsd, double distanceNm,
                           double congestionIndex, double volatility, double routeRiskScore,
                           String attractiveness, String badgeClass) {
        this.originCountry = originCountry;
        this.loadingPort = loadingPort;
        this.destinationPort = destinationPort;
        this.currentRateUsd = currentRateUsd;
        this.forecastRateUsd = forecastRateUsd;
        this.distanceNm = distanceNm;
        this.congestionIndex = congestionIndex;
        this.volatility = volatility;
        this.routeRiskScore = routeRiskScore;
        this.attractiveness = attractiveness;
        this.badgeClass = badgeClass;
    }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public double getCurrentRateUsd() { return currentRateUsd; }
    public void setCurrentRateUsd(double currentRateUsd) { this.currentRateUsd = currentRateUsd; }

    public double getForecastRateUsd() { return forecastRateUsd; }
    public void setForecastRateUsd(double forecastRateUsd) { this.forecastRateUsd = forecastRateUsd; }

    public double getDistanceNm() { return distanceNm; }
    public void setDistanceNm(double distanceNm) { this.distanceNm = distanceNm; }

    public double getCongestionIndex() { return congestionIndex; }
    public void setCongestionIndex(double congestionIndex) { this.congestionIndex = congestionIndex; }

    public double getVolatility() { return volatility; }
    public void setVolatility(double volatility) { this.volatility = volatility; }

    public double getRouteRiskScore() { return routeRiskScore; }
    public void setRouteRiskScore(double routeRiskScore) { this.routeRiskScore = routeRiskScore; }

    public String getAttractiveness() { return attractiveness; }
    public void setAttractiveness(String attractiveness) { this.attractiveness = attractiveness; }

    public String getBadgeClass() { return badgeClass; }
    public void setBadgeClass(String badgeClass) { this.badgeClass = badgeClass; }
}
