package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_forecasts")
public class FreightForecast {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "trade_route_id")
    private TradeRoute tradeRoute;

    @Column(name = "vessel_class_code", nullable = false, length = 32)
    private String vesselClassCode;

    @Column(name = "current_spot_rate", nullable = false)
    private Double currentSpotRate;

    @Column(name = "forecast_7d", nullable = false)
    private Double forecast7d;

    @Column(name = "forecast_14d", nullable = false)
    private Double forecast14d;

    @Column(name = "forecast_30d", nullable = false)
    private Double forecast30d;

    @Column(name = "lower_bound_14d", nullable = false)
    private Double lowerBound14d;

    @Column(name = "upper_bound_14d", nullable = false)
    private Double upperBound14d;

    @Column(name = "trend_direction", nullable = false, length = 16)
    private String trendDirection; // BULLISH, BEARISH, STABLE

    @Column(name = "volatility_index", nullable = false, length = 16)
    private String volatilityIndex; // LOW, MODERATE, HIGH

    @Column(name = "confidence_score", nullable = false)
    private Double confidenceScore;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FreightForecast() {}

    public FreightForecast(Long id, TradeRoute tradeRoute, String vesselClassCode, Double currentSpotRate,
                           Double forecast7d, Double forecast14d, Double forecast30d, Double lowerBound14d,
                           Double upperBound14d, String trendDirection, String volatilityIndex,
                           Double confidenceScore, LocalDateTime createdAt) {
        this.id = id;
        this.tradeRoute = tradeRoute;
        this.vesselClassCode = vesselClassCode;
        this.currentSpotRate = currentSpotRate;
        this.forecast7d = forecast7d;
        this.forecast14d = forecast14d;
        this.forecast30d = forecast30d;
        this.lowerBound14d = lowerBound14d;
        this.upperBound14d = upperBound14d;
        this.trendDirection = trendDirection;
        this.volatilityIndex = volatilityIndex;
        this.confidenceScore = confidenceScore;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TradeRoute getTradeRoute() { return tradeRoute; }
    public void setTradeRoute(TradeRoute tradeRoute) { this.tradeRoute = tradeRoute; }

    public String getVesselClassCode() { return vesselClassCode; }
    public void setVesselClassCode(String vesselClassCode) { this.vesselClassCode = vesselClassCode; }

    public Double getCurrentSpotRate() { return currentSpotRate; }
    public void setCurrentSpotRate(Double currentSpotRate) { this.currentSpotRate = currentSpotRate; }

    public Double getForecast7d() { return forecast7d; }
    public void setForecast7d(Double forecast7d) { this.forecast7d = forecast7d; }

    public Double getForecast14d() { return forecast14d; }
    public void setForecast14d(Double forecast14d) { this.forecast14d = forecast14d; }

    public Double getForecast30d() { return forecast30d; }
    public void setForecast30d(Double forecast30d) { this.forecast30d = forecast30d; }

    public Double getLowerBound14d() { return lowerBound14d; }
    public void setLowerBound14d(Double lowerBound14d) { this.lowerBound14d = lowerBound14d; }

    public Double getUpperBound14d() { return upperBound14d; }
    public void setUpperBound14d(Double upperBound14d) { this.upperBound14d = upperBound14d; }

    public String getTrendDirection() { return trendDirection; }
    public void setTrendDirection(String trendDirection) { this.trendDirection = trendDirection; }

    public String getVolatilityIndex() { return volatilityIndex; }
    public void setVolatilityIndex(String volatilityIndex) { this.volatilityIndex = volatilityIndex; }

    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

