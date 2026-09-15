package com.sih.navisense.dto;

import java.time.LocalDate;

public class HistoricalDataPointDto {
    private LocalDate date;
    private double rateUsd;

    public HistoricalDataPointDto() {}

    public HistoricalDataPointDto(LocalDate date, double rateUsd) {
        this.date = date;
        this.rateUsd = rateUsd;
    }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public double getRateUsd() { return rateUsd; }
    public void setRateUsd(double rateUsd) { this.rateUsd = rateUsd; }
}
