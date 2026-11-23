package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_market_data")
public class FreightMarketData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(nullable = false)
    private Integer bdi;

    @Column(nullable = false)
    private Integer bci;

    @Column(nullable = false)
    private Integer bpi;

    @Column(nullable = false)
    private Integer bsi;

    @Column(name = "bunker_vlsfo_usd", nullable = false)
    private Double bunkerVlsfoUsd;

    @Column(name = "usd_inr_rate", nullable = false)
    private Double usdInrRate = 83.5;

    @Column(name = "sentiment_score", nullable = false)
    private Double sentimentScore = 0.52;

    @Column(name = "data_source", nullable = false, length = 64)
    private String dataSource = "HISTORICAL_BALTIC_INDEX";

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public FreightMarketData() {}

    public FreightMarketData(Long id, LocalDate recordDate, Integer bdi, Integer bci, Integer bpi,
                             Integer bsi, Double bunkerVlsfoUsd, Double usdInrRate, Double sentimentScore,
                             String dataSource, LocalDateTime createdAt) {
        this.id = id;
        this.recordDate = recordDate;
        this.bdi = bdi;
        this.bci = bci;
        this.bpi = bpi;
        this.bsi = bsi;
        this.bunkerVlsfoUsd = bunkerVlsfoUsd;
        this.usdInrRate = usdInrRate != null ? usdInrRate : 83.5;
        this.sentimentScore = sentimentScore != null ? sentimentScore : 0.52;
        this.dataSource = dataSource != null ? dataSource : "HISTORICAL_BALTIC_INDEX";
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public Integer getBdi() { return bdi; }
    public void setBdi(Integer bdi) { this.bdi = bdi; }

    public Integer getBci() { return bci; }
    public void setBci(Integer bci) { this.bci = bci; }

    public Integer getBpi() { return bpi; }
    public void setBpi(Integer bpi) { this.bpi = bpi; }

    public Integer getBsi() { return bsi; }
    public void setBsi(Integer bsi) { this.bsi = bsi; }

    public Double getBunkerVlsfoUsd() { return bunkerVlsfoUsd; }
    public void setBunkerVlsfoUsd(Double bunkerVlsfoUsd) { this.bunkerVlsfoUsd = bunkerVlsfoUsd; }

    public Double getUsdInrRate() { return usdInrRate; }
    public void setUsdInrRate(Double usdInrRate) { this.usdInrRate = usdInrRate; }

    public Double getSentimentScore() { return sentimentScore; }
    public void setSentimentScore(Double sentimentScore) { this.sentimentScore = sentimentScore; }

    public String getDataSource() { return dataSource; }
    public void setDataSource(String dataSource) { this.dataSource = dataSource; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
