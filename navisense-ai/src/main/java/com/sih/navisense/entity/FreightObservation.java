package com.sih.navisense.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "freight_observations", indexes = {
    @Index(name = "idx_route_vessel_date", columnList = "originCountry,destinationPort,vesselClass,observationDate")
})
public class FreightObservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate observationDate;

    @Column(nullable = false, length = 100)
    private String originCountry;

    @Column(nullable = false, length = 100)
    private String loadingPort;

    @Column(nullable = false, length = 100)
    private String destinationPort;

    @Column(nullable = false, length = 50)
    private String vesselClass;

    @Column(nullable = false)
    private Double ratePerTonneUsd;

    private Double marketIndex;
    private Double fuelIndex;
    private Double demandIndex;
    private Double supplyIndex;
    private Double congestionIndex;

    public FreightObservation() {}

    public FreightObservation(Long id, LocalDate observationDate, String originCountry,
                              String loadingPort, String destinationPort, String vesselClass,
                              Double ratePerTonneUsd, Double marketIndex, Double fuelIndex,
                              Double demandIndex, Double supplyIndex, Double congestionIndex) {
        this.id = id;
        this.observationDate = observationDate;
        this.originCountry = originCountry;
        this.loadingPort = loadingPort;
        this.destinationPort = destinationPort;
        this.vesselClass = vesselClass;
        this.ratePerTonneUsd = ratePerTonneUsd;
        this.marketIndex = marketIndex;
        this.fuelIndex = fuelIndex;
        this.demandIndex = demandIndex;
        this.supplyIndex = supplyIndex;
        this.congestionIndex = congestionIndex;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getObservationDate() { return observationDate; }
    public void setObservationDate(LocalDate observationDate) { this.observationDate = observationDate; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public String getVesselClass() { return vesselClass; }
    public void setVesselClass(String vesselClass) { this.vesselClass = vesselClass; }

    public Double getRatePerTonneUsd() { return ratePerTonneUsd; }
    public void setRatePerTonneUsd(Double ratePerTonneUsd) { this.ratePerTonneUsd = ratePerTonneUsd; }

    public Double getMarketIndex() { return marketIndex; }
    public void setMarketIndex(Double marketIndex) { this.marketIndex = marketIndex; }

    public Double getFuelIndex() { return fuelIndex; }
    public void setFuelIndex(Double fuelIndex) { this.fuelIndex = fuelIndex; }

    public Double getDemandIndex() { return demandIndex; }
    public void setDemandIndex(Double demandIndex) { this.demandIndex = demandIndex; }

    public Double getSupplyIndex() { return supplyIndex; }
    public void setSupplyIndex(Double supplyIndex) { this.supplyIndex = supplyIndex; }

    public Double getCongestionIndex() { return congestionIndex; }
    public void setCongestionIndex(Double congestionIndex) { this.congestionIndex = congestionIndex; }
}
