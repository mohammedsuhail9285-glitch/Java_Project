package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "maritime_ports")
public class MaritimePort {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "port_code", nullable = false, unique = true, length = 16)
    private String portCode;

    @Column(name = "port_name", nullable = false, length = 128)
    private String portName;

    @Column(nullable = false, length = 64)
    private String country = "India";

    @Column(nullable = false, length = 32)
    private String coast = "East Coast";

    @Column(name = "max_draft_meters", nullable = false)
    private Double maxDraftMeters;

    @Column(name = "max_dwt", nullable = false)
    private Double maxDwt;

    @Column(name = "max_loa_meters", nullable = false)
    private Double maxLoaMeters = 280.0;

    @Column(name = "max_beam_meters", nullable = false)
    private Double maxBeamMeters = 45.0;

    @Column(name = "discharge_rate_tpd", nullable = false)
    private Double dischargeRateTpd;

    @Column(name = "average_berth_wait_days", nullable = false)
    private Double averageBerthWaitDays = 2.5;

    @Column(name = "congestion_index", nullable = false)
    private Double congestionIndex = 0.35;

    @Column(name = "port_tariff_per_ton", nullable = false)
    private Double portTariffPerTon;

    @Column(name = "tide_restriction_notes", nullable = false)
    private String tideRestrictionNotes = "Standard semi-diurnal tides";

    @Column(name = "lighterage_supported", nullable = false)
    private Boolean lighterageSupported = false;

    @Column(name = "lighterage_zone", length = 128)
    private String lighterageZone;

    @Column(name = "lighterage_tariff_per_ton", nullable = false)
    private Double lighterageTariffPerTon = 0.0;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public MaritimePort() {}

    public MaritimePort(Long id, String portCode, String portName, String country, String coast,
                        Double maxDraftMeters, Double maxDwt, Double maxLoaMeters, Double maxBeamMeters,
                        Double dischargeRateTpd, Double averageBerthWaitDays, Double congestionIndex,
                        Double portTariffPerTon, String tideRestrictionNotes, Boolean lighterageSupported,
                        String lighterageZone, Double lighterageTariffPerTon, LocalDateTime createdAt) {
        this.id = id;
        this.portCode = portCode;
        this.portName = portName;
        this.country = country;
        this.coast = coast;
        this.maxDraftMeters = maxDraftMeters;
        this.maxDwt = maxDwt;
        this.maxLoaMeters = maxLoaMeters != null ? maxLoaMeters : 280.0;
        this.maxBeamMeters = maxBeamMeters != null ? maxBeamMeters : 45.0;
        this.dischargeRateTpd = dischargeRateTpd;
        this.averageBerthWaitDays = averageBerthWaitDays != null ? averageBerthWaitDays : 2.5;
        this.congestionIndex = congestionIndex != null ? congestionIndex : 0.35;
        this.portTariffPerTon = portTariffPerTon;
        this.tideRestrictionNotes = tideRestrictionNotes != null ? tideRestrictionNotes : "Standard semi-diurnal tides";
        this.lighterageSupported = lighterageSupported != null ? lighterageSupported : false;
        this.lighterageZone = lighterageZone;
        this.lighterageTariffPerTon = lighterageTariffPerTon != null ? lighterageTariffPerTon : 0.0;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPortCode() { return portCode; }
    public void setPortCode(String portCode) { this.portCode = portCode; }

    public String getPortName() { return portName; }
    public void setPortName(String portName) { this.portName = portName; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCoast() { return coast; }
    public void setCoast(String coast) { this.coast = coast; }

    public Double getMaxDraftMeters() { return maxDraftMeters; }
    public void setMaxDraftMeters(Double maxDraftMeters) { this.maxDraftMeters = maxDraftMeters; }

    public Double getMaxDwt() { return maxDwt; }
    public void setMaxDwt(Double maxDwt) { this.maxDwt = maxDwt; }

    public Double getMaxLoaMeters() { return maxLoaMeters; }
    public void setMaxLoaMeters(Double maxLoaMeters) { this.maxLoaMeters = maxLoaMeters; }

    public Double getMaxBeamMeters() { return maxBeamMeters; }
    public void setMaxBeamMeters(Double maxBeamMeters) { this.maxBeamMeters = maxBeamMeters; }

    public Double getDischargeRateTpd() { return dischargeRateTpd; }
    public void setDischargeRateTpd(Double dischargeRateTpd) { this.dischargeRateTpd = dischargeRateTpd; }

    public Double getAverageBerthWaitDays() { return averageBerthWaitDays; }
    public void setAverageBerthWaitDays(Double averageBerthWaitDays) { this.averageBerthWaitDays = averageBerthWaitDays; }

    public Double getCongestionIndex() { return congestionIndex; }
    public void setCongestionIndex(Double congestionIndex) { this.congestionIndex = congestionIndex; }

    public Double getPortTariffPerTon() { return portTariffPerTon; }
    public void setPortTariffPerTon(Double portTariffPerTon) { this.portTariffPerTon = portTariffPerTon; }

    public String getTideRestrictionNotes() { return tideRestrictionNotes; }
    public void setTideRestrictionNotes(String tideRestrictionNotes) { this.tideRestrictionNotes = tideRestrictionNotes; }

    public Boolean getLighterageSupported() { return lighterageSupported; }
    public void setLighterageSupported(Boolean lighterageSupported) { this.lighterageSupported = lighterageSupported; }

    public String getLighterageZone() { return lighterageZone; }
    public void setLighterageZone(String lighterageZone) { this.lighterageZone = lighterageZone; }

    public Double getLighterageTariffPerTon() { return lighterageTariffPerTon; }
    public void setLighterageTariffPerTon(Double lighterageTariffPerTon) { this.lighterageTariffPerTon = lighterageTariffPerTon; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
