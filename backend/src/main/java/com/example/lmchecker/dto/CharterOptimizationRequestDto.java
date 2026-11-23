package com.example.lmchecker.dto;

import java.time.LocalDate;

public class CharterOptimizationRequestDto {

    private String commodity;
    private Double quantityTons;
    private String originCountry;
    private String originPortName;
    private String destinationPortCode;
    private LocalDate targetLaycanStart;
    private LocalDate targetLaycanEnd;
    private Double fobPriceUsd;
    private String contractType = "SPOT"; // SPOT, COA_3_VOYAGE, COA_6_VOYAGE

    public CharterOptimizationRequestDto() {}

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public Double getQuantityTons() { return quantityTons; }
    public void setQuantityTons(Double quantityTons) { this.quantityTons = quantityTons; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getOriginPortName() { return originPortName; }
    public void setOriginPortName(String originPortName) { this.originPortName = originPortName; }

    public String getDestinationPortCode() { return destinationPortCode; }
    public void setDestinationPortCode(String destinationPortCode) { this.destinationPortCode = destinationPortCode; }

    public LocalDate getTargetLaycanStart() { return targetLaycanStart; }
    public void setTargetLaycanStart(LocalDate targetLaycanStart) { this.targetLaycanStart = targetLaycanStart; }

    public LocalDate getTargetLaycanEnd() { return targetLaycanEnd; }
    public void setTargetLaycanEnd(LocalDate targetLaycanEnd) { this.targetLaycanEnd = targetLaycanEnd; }

    public Double getFobPriceUsd() { return fobPriceUsd; }
    public void setFobPriceUsd(Double fobPriceUsd) { this.fobPriceUsd = fobPriceUsd; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }
}
