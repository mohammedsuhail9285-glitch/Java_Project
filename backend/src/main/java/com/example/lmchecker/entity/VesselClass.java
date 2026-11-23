package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vessel_classes")
public class VesselClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "class_code", nullable = false, unique = true, length = 32)
    private String classCode;

    @Column(name = "class_name", nullable = false, length = 64)
    private String className;

    @Column(name = "min_dwt", nullable = false)
    private Double minDwt;

    @Column(name = "max_dwt", nullable = false)
    private Double maxDwt;

    @Column(name = "max_draft_meters", nullable = false)
    private Double maxDraftMeters;

    @Column(name = "typical_loa_meters", nullable = false)
    private Double typicalLoaMeters = 225.0;

    @Column(name = "typical_beam_meters", nullable = false)
    private Double typicalBeamMeters = 32.2;

    @Column(name = "ballast_draft_meters", nullable = false)
    private Double ballastDraftMeters = 8.5;

    @Column(name = "fuel_consumption_tpd", nullable = false)
    private Double fuelConsumptionTpd;

    @Column(name = "daily_operating_cost_usd", nullable = false)
    private Double dailyOperatingCostUsd;

    @Column(name = "speed_knots", nullable = false)
    private Double speedKnots = 13.5;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public VesselClass() {}

    public VesselClass(Long id, String classCode, String className, Double minDwt, Double maxDwt,
                       Double maxDraftMeters, Double typicalLoaMeters, Double typicalBeamMeters,
                       Double ballastDraftMeters, Double fuelConsumptionTpd, Double dailyOperatingCostUsd,
                       Double speedKnots, LocalDateTime createdAt) {
        this.id = id;
        this.classCode = classCode;
        this.className = className;
        this.minDwt = minDwt;
        this.maxDwt = maxDwt;
        this.maxDraftMeters = maxDraftMeters;
        this.typicalLoaMeters = typicalLoaMeters != null ? typicalLoaMeters : 225.0;
        this.typicalBeamMeters = typicalBeamMeters != null ? typicalBeamMeters : 32.2;
        this.ballastDraftMeters = ballastDraftMeters != null ? ballastDraftMeters : 8.5;
        this.fuelConsumptionTpd = fuelConsumptionTpd;
        this.dailyOperatingCostUsd = dailyOperatingCostUsd;
        this.speedKnots = speedKnots != null ? speedKnots : 13.5;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClassCode() { return classCode; }
    public void setClassCode(String classCode) { this.classCode = classCode; }

    public String getClassName() { return className; }
    public void setClassName(String className) { this.className = className; }

    public Double getMinDwt() { return minDwt; }
    public void setMinDwt(Double minDwt) { this.minDwt = minDwt; }

    public Double getMaxDwt() { return maxDwt; }
    public void setMaxDwt(Double maxDwt) { this.maxDwt = maxDwt; }

    public Double getMaxDraftMeters() { return maxDraftMeters; }
    public void setMaxDraftMeters(Double maxDraftMeters) { this.maxDraftMeters = maxDraftMeters; }

    public Double getTypicalLoaMeters() { return typicalLoaMeters; }
    public void setTypicalLoaMeters(Double typicalLoaMeters) { this.typicalLoaMeters = typicalLoaMeters; }

    public Double getTypicalBeamMeters() { return typicalBeamMeters; }
    public void setTypicalBeamMeters(Double typicalBeamMeters) { this.typicalBeamMeters = typicalBeamMeters; }

    public Double getBallastDraftMeters() { return ballastDraftMeters; }
    public void setBallastDraftMeters(Double ballastDraftMeters) { this.ballastDraftMeters = ballastDraftMeters; }

    public Double getFuelConsumptionTpd() { return fuelConsumptionTpd; }
    public void setFuelConsumptionTpd(Double fuelConsumptionTpd) { this.fuelConsumptionTpd = fuelConsumptionTpd; }

    public Double getDailyOperatingCostUsd() { return dailyOperatingCostUsd; }
    public void setDailyOperatingCostUsd(Double dailyOperatingCostUsd) { this.dailyOperatingCostUsd = dailyOperatingCostUsd; }

    public Double getSpeedKnots() { return speedKnots; }
    public void setSpeedKnots(Double speedKnots) { this.speedKnots = speedKnots; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
