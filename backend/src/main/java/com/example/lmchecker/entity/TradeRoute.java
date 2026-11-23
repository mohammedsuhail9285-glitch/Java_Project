package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trade_routes")
public class TradeRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "route_code", nullable = false, unique = true, length = 64)
    private String routeCode;

    @Column(name = "origin_port", nullable = false, length = 64)
    private String originPort;

    @Column(name = "origin_country", nullable = false, length = 64)
    private String originCountry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_port_id")
    private MaritimePort destinationPort;

    @Column(nullable = false, length = 64)
    private String commodity;

    @Column(name = "distance_nautical_miles", nullable = false)
    private Double distanceNauticalMiles;

    @Column(name = "benchmark_rate_usd_per_ton", nullable = false)
    private Double benchmarkRateUsdPerTon;

    @Column(name = "origin_max_draft_meters", nullable = false)
    private Double originMaxDraftMeters = 18.5;

    @Column(name = "origin_max_loa_meters", nullable = false)
    private Double originMaxLoaMeters = 310.0;

    @Column(name = "loading_rate_tpd", nullable = false)
    private Double loadingRateTpd = 35000.0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public TradeRoute() {}

    public TradeRoute(Long id, String routeCode, String originPort, String originCountry,
                      MaritimePort destinationPort, String commodity, Double distanceNauticalMiles,
                      Double benchmarkRateUsdPerTon, Double originMaxDraftMeters, Double originMaxLoaMeters,
                      Double loadingRateTpd, LocalDateTime createdAt) {
        this.id = id;
        this.routeCode = routeCode;
        this.originPort = originPort;
        this.originCountry = originCountry;
        this.destinationPort = destinationPort;
        this.commodity = commodity;
        this.distanceNauticalMiles = distanceNauticalMiles;
        this.benchmarkRateUsdPerTon = benchmarkRateUsdPerTon;
        this.originMaxDraftMeters = originMaxDraftMeters != null ? originMaxDraftMeters : 18.5;
        this.originMaxLoaMeters = originMaxLoaMeters != null ? originMaxLoaMeters : 310.0;
        this.loadingRateTpd = loadingRateTpd != null ? loadingRateTpd : 35000.0;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRouteCode() { return routeCode; }
    public void setRouteCode(String routeCode) { this.routeCode = routeCode; }

    public String getOriginPort() { return originPort; }
    public void setOriginPort(String originPort) { this.originPort = originPort; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public MaritimePort getDestinationPort() { return destinationPort; }
    public void setDestinationPort(MaritimePort destinationPort) { this.destinationPort = destinationPort; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public Double getDistanceNauticalMiles() { return distanceNauticalMiles; }
    public void setDistanceNauticalMiles(Double distanceNauticalMiles) { this.distanceNauticalMiles = distanceNauticalMiles; }

    public Double getBenchmarkRateUsdPerTon() { return benchmarkRateUsdPerTon; }
    public void setBenchmarkRateUsdPerTon(Double benchmarkRateUsdPerTon) { this.benchmarkRateUsdPerTon = benchmarkRateUsdPerTon; }

    public Double getOriginMaxDraftMeters() { return originMaxDraftMeters; }
    public void setOriginMaxDraftMeters(Double originMaxDraftMeters) { this.originMaxDraftMeters = originMaxDraftMeters; }

    public Double getOriginMaxLoaMeters() { return originMaxLoaMeters; }
    public void setOriginMaxLoaMeters(Double originMaxLoaMeters) { this.originMaxLoaMeters = originMaxLoaMeters; }

    public Double getLoadingRateTpd() { return loadingRateTpd; }
    public void setLoadingRateTpd(Double loadingRateTpd) { this.loadingRateTpd = loadingRateTpd; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
