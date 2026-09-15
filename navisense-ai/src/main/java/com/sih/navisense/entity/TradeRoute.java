package com.sih.navisense.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "trade_routes")
public class TradeRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String originCountry;

    @Column(nullable = false, length = 100)
    private String loadingPort;

    @Column(nullable = false, length = 100)
    private String destinationPort;

    @Column(nullable = false)
    private Double distanceNauticalMiles;

    @Column(nullable = false)
    private Double typicalVoyageDays;

    @Column(nullable = false)
    private Double riskFactor; // 0 - 100

    @Column(nullable = false)
    private Double seasonalityFactor; // baseline 1.0

    @Column(nullable = false)
    private Double baseFreightUsdPerTonne;

    public TradeRoute() {}

    public TradeRoute(Long id, String originCountry, String loadingPort, String destinationPort,
                      Double distanceNauticalMiles, Double typicalVoyageDays, Double riskFactor,
                      Double seasonalityFactor, Double baseFreightUsdPerTonne) {
        this.id = id;
        this.originCountry = originCountry;
        this.loadingPort = loadingPort;
        this.destinationPort = destinationPort;
        this.distanceNauticalMiles = distanceNauticalMiles;
        this.typicalVoyageDays = typicalVoyageDays;
        this.riskFactor = riskFactor;
        this.seasonalityFactor = seasonalityFactor;
        this.baseFreightUsdPerTonne = baseFreightUsdPerTonne;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public Double getDistanceNauticalMiles() { return distanceNauticalMiles; }
    public void setDistanceNauticalMiles(Double distanceNauticalMiles) { this.distanceNauticalMiles = distanceNauticalMiles; }

    public Double getTypicalVoyageDays() { return typicalVoyageDays; }
    public void setTypicalVoyageDays(Double typicalVoyageDays) { this.typicalVoyageDays = typicalVoyageDays; }

    public Double getRiskFactor() { return riskFactor; }
    public void setRiskFactor(Double riskFactor) { this.riskFactor = riskFactor; }

    public Double getSeasonalityFactor() { return seasonalityFactor; }
    public void setSeasonalityFactor(Double seasonalityFactor) { this.seasonalityFactor = seasonalityFactor; }

    public Double getBaseFreightUsdPerTonne() { return baseFreightUsdPerTonne; }
    public void setBaseFreightUsdPerTonne(Double baseFreightUsdPerTonne) { this.baseFreightUsdPerTonne = baseFreightUsdPerTonne; }
}
