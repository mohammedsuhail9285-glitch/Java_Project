package com.sih.navisense.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "vessel_types")
public class VesselType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // Handysize, Supramax, Panamax, Capesize

    @Column(nullable = false)
    private Double minDwt;

    @Column(nullable = false)
    private Double maxDwt;

    @Column(nullable = false)
    private Double typicalCapacity; // tonnes

    @Column(nullable = false)
    private Double draft; // meters

    @Column(nullable = false)
    private Double loa; // length overall in meters

    @Column(nullable = false)
    private Double beam; // beam in meters

    @Column(nullable = false)
    private Double speedKnots;

    @Column(nullable = false)
    private Double fuelConsumptionPerDay; // tonnes/day

    @Column(nullable = false)
    private Double charterCostFactor;

    @Column(nullable = false)
    private Double dailyHireBaseUsd;

    public VesselType() {}

    public VesselType(Long id, String name, Double minDwt, Double maxDwt, Double typicalCapacity,
                      Double draft, Double loa, Double beam, Double speedKnots,
                      Double fuelConsumptionPerDay, Double charterCostFactor, Double dailyHireBaseUsd) {
        this.id = id;
        this.name = name;
        this.minDwt = minDwt;
        this.maxDwt = maxDwt;
        this.typicalCapacity = typicalCapacity;
        this.draft = draft;
        this.loa = loa;
        this.beam = beam;
        this.speedKnots = speedKnots;
        this.fuelConsumptionPerDay = fuelConsumptionPerDay;
        this.charterCostFactor = charterCostFactor;
        this.dailyHireBaseUsd = dailyHireBaseUsd;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Double getMinDwt() { return minDwt; }
    public void setMinDwt(Double minDwt) { this.minDwt = minDwt; }

    public Double getMaxDwt() { return maxDwt; }
    public void setMaxDwt(Double maxDwt) { this.maxDwt = maxDwt; }

    public Double getTypicalCapacity() { return typicalCapacity; }
    public void setTypicalCapacity(Double typicalCapacity) { this.typicalCapacity = typicalCapacity; }

    public Double getDraft() { return draft; }
    public void setDraft(Double draft) { this.draft = draft; }

    public Double getLoa() { return loa; }
    public void setLoa(Double loa) { this.loa = loa; }

    public Double getBeam() { return beam; }
    public void setBeam(Double beam) { this.beam = beam; }

    public Double getSpeedKnots() { return speedKnots; }
    public void setSpeedKnots(Double speedKnots) { this.speedKnots = speedKnots; }

    public Double getFuelConsumptionPerDay() { return fuelConsumptionPerDay; }
    public void setFuelConsumptionPerDay(Double fuelConsumptionPerDay) { this.fuelConsumptionPerDay = fuelConsumptionPerDay; }

    public Double getCharterCostFactor() { return charterCostFactor; }
    public void setCharterCostFactor(Double charterCostFactor) { this.charterCostFactor = charterCostFactor; }

    public Double getDailyHireBaseUsd() { return dailyHireBaseUsd; }
    public void setDailyHireBaseUsd(Double dailyHireBaseUsd) { this.dailyHireBaseUsd = dailyHireBaseUsd; }
}
