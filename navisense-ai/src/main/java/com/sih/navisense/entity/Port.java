package com.sih.navisense.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ports")
public class Port {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, length = 100)
    private String country;

    @Column(nullable = false, unique = true, length = 10)
    private String portCode;

    @Column(nullable = false)
    private Double maximumDraft;

    @Column(nullable = false)
    private Double maximumLOA;

    @Column(nullable = false)
    private Double maximumBeam;

    @Column(nullable = false)
    private Double handlingRateTonnesPerHour;

    @Column(nullable = false)
    private Double averageCongestionScore;

    @Column(nullable = false)
    private Double averageTurnaroundHours;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false, length = 30)
    private String portType; // "DESTINATION" or "LOADING"

    public Port() {}

    public Port(Long id, String name, String country, String portCode, Double maximumDraft,
                Double maximumLOA, Double maximumBeam, Double handlingRateTonnesPerHour,
                Double averageCongestionScore, Double averageTurnaroundHours,
                Double latitude, Double longitude, String portType) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.portCode = portCode;
        this.maximumDraft = maximumDraft;
        this.maximumLOA = maximumLOA;
        this.maximumBeam = maximumBeam;
        this.handlingRateTonnesPerHour = handlingRateTonnesPerHour;
        this.averageCongestionScore = averageCongestionScore;
        this.averageTurnaroundHours = averageTurnaroundHours;
        this.latitude = latitude;
        this.longitude = longitude;
        this.portType = portType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getPortCode() { return portCode; }
    public void setPortCode(String portCode) { this.portCode = portCode; }

    public Double getMaximumDraft() { return maximumDraft; }
    public void setMaximumDraft(Double maximumDraft) { this.maximumDraft = maximumDraft; }

    public Double getMaximumLOA() { return maximumLOA; }
    public void setMaximumLOA(Double maximumLOA) { this.maximumLOA = maximumLOA; }

    public Double getMaximumBeam() { return maximumBeam; }
    public void setMaximumBeam(Double maximumBeam) { this.maximumBeam = maximumBeam; }

    public Double getHandlingRateTonnesPerHour() { return handlingRateTonnesPerHour; }
    public void setHandlingRateTonnesPerHour(Double handlingRateTonnesPerHour) { this.handlingRateTonnesPerHour = handlingRateTonnesPerHour; }

    public Double getAverageCongestionScore() { return averageCongestionScore; }
    public void setAverageCongestionScore(Double averageCongestionScore) { this.averageCongestionScore = averageCongestionScore; }

    public Double getAverageTurnaroundHours() { return averageTurnaroundHours; }
    public void setAverageTurnaroundHours(Double averageTurnaroundHours) { this.averageTurnaroundHours = averageTurnaroundHours; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getPortType() { return portType; }
    public void setPortType(String portType) { this.portType = portType; }
}
