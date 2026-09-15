package com.sih.navisense.dto;

public class SimulationRequestDto {
    private String originCountry = "Australia";
    private String loadingPort = "Hay Point";
    private String destinationPort = "Paradip";
    private double cargoQuantityTonnes = 150000.0;
    private String vesselClass = "Panamax";
    private double freightChangePct = 10.0; // e.g. +10%
    private double fuelPriceChangePct = 0.0;
    private double congestionChangePct = 25.0; // e.g. +25%
    private int delayDays = 5;

    public SimulationRequestDto() {}

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public double getCargoQuantityTonnes() { return cargoQuantityTonnes; }
    public void setCargoQuantityTonnes(double cargoQuantityTonnes) { this.cargoQuantityTonnes = cargoQuantityTonnes; }

    public String getVesselClass() { return vesselClass; }
    public void setVesselClass(String vesselClass) { this.vesselClass = vesselClass; }

    public double getFreightChangePct() { return freightChangePct; }
    public void setFreightChangePct(double freightChangePct) { this.freightChangePct = freightChangePct; }

    public double getFuelPriceChangePct() { return fuelPriceChangePct; }
    public void setFuelPriceChangePct(double fuelPriceChangePct) { this.fuelPriceChangePct = fuelPriceChangePct; }

    public double getCongestionChangePct() { return congestionChangePct; }
    public void setCongestionChangePct(double congestionChangePct) { this.congestionChangePct = congestionChangePct; }

    public int getDelayDays() { return delayDays; }
    public void setDelayDays(int delayDays) { this.delayDays = delayDays; }
}
