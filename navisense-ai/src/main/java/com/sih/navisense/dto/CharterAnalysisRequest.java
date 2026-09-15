package com.sih.navisense.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CharterAnalysisRequest {

    @NotBlank(message = "Cargo type is required")
    private String cargoType = "Coking Coal";

    @NotNull(message = "Cargo quantity is required")
    @DecimalMin(value = "1000.0", message = "Cargo quantity must be at least 1,000 tonnes")
    @DecimalMax(value = "500000.0", message = "Cargo quantity cannot exceed 500,000 tonnes")
    private Double quantityTonnes = 150000.0;

    @NotBlank(message = "Origin country is required")
    private String originCountry = "Australia";

    @NotBlank(message = "Loading port is required")
    private String loadingPort = "Hay Point";

    @NotBlank(message = "Destination port is required")
    private String destinationPort = "Paradip";

    @NotNull(message = "Desired shipment date is required")
    private LocalDate shipmentDate = LocalDate.now().plusDays(14);

    @NotNull(message = "Contract duration is required")
    @Min(value = 1, message = "Duration must be at least 1 month")
    @Max(value = 36, message = "Duration cannot exceed 36 months")
    private Integer contractDurationMonths = 3;

    private String preferredVessel; // Optional

    @DecimalMin(value = "0.0", message = "Budget must be positive")
    private Double budgetInr; // Optional budget in INR (e.g. ₹ Crores * 1e7)

    @DecimalMin(value = "0.0", message = "Expected freight rate must be positive")
    private Double expectedFreightRateUsd; // Optional expected freight rate in USD/t

    public CharterAnalysisRequest() {}

    public String getCargoType() { return cargoType; }
    public void setCargoType(String cargoType) { this.cargoType = cargoType; }

    public Double getQuantityTonnes() { return quantityTonnes; }
    public void setQuantityTonnes(Double quantityTonnes) { this.quantityTonnes = quantityTonnes; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public String getLoadingPort() { return loadingPort; }
    public void setLoadingPort(String loadingPort) { this.loadingPort = loadingPort; }

    public String getDestinationPort() { return destinationPort; }
    public void setDestinationPort(String destinationPort) { this.destinationPort = destinationPort; }

    public LocalDate getShipmentDate() { return shipmentDate; }
    public void setShipmentDate(LocalDate shipmentDate) { this.shipmentDate = shipmentDate; }

    public Integer getContractDurationMonths() { return contractDurationMonths; }
    public void setContractDurationMonths(Integer contractDurationMonths) { this.contractDurationMonths = contractDurationMonths; }

    public String getPreferredVessel() { return preferredVessel; }
    public void setPreferredVessel(String preferredVessel) { this.preferredVessel = preferredVessel; }

    public Double getBudgetInr() { return budgetInr; }
    public void setBudgetInr(Double budgetInr) { this.budgetInr = budgetInr; }

    public Double getExpectedFreightRateUsd() { return expectedFreightRateUsd; }
    public void setExpectedFreightRateUsd(Double expectedFreightRateUsd) { this.expectedFreightRateUsd = expectedFreightRateUsd; }
}
