package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "procurement_plans")
public class ProcurementPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "plan_reference", nullable = false, unique = true, length = 64)
    private String planReference;

    @Column(nullable = false, length = 64)
    private String commodity;

    @Column(name = "quantity_tons", nullable = false)
    private Double quantityTons;

    @Column(name = "origin_country", nullable = false, length = 64)
    private String originCountry;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "destination_port_id")
    private MaritimePort destinationPort;

    @Column(name = "target_laycan_start", nullable = false)
    private LocalDate targetLaycanStart;

    @Column(name = "target_laycan_end", nullable = false)
    private LocalDate targetLaycanEnd;

    @Column(name = "recommended_vessel_class", nullable = false, length = 32)
    private String recommendedVesselClass;

    @Column(name = "charter_decision", nullable = false, length = 32)
    private String charterDecision; // CHARTER_NOW, HOLD_WAIT, SPLIT_COA

    @Column(name = "contract_type", nullable = false, length = 32)
    private String contractType = "SPOT"; // SPOT, COA_3_VOYAGE, COA_6_VOYAGE

    @Column(name = "coa_discount_pct", nullable = false)
    private Double coaDiscountPct = 0.0;

    @Column(name = "coa_total_volume_tons", nullable = false)
    private Double coaTotalVolumeTons = 0.0;

    @Column(name = "fob_cargo_price_usd_ton", nullable = false)
    private Double fobCargoPriceUsdTon;

    @Column(name = "predicted_freight_usd_ton", nullable = false)
    private Double predictedFreightUsdTon;

    @Column(name = "bunker_adjustment_usd_ton", nullable = false)
    private Double bunkerAdjustmentUsdTon;

    @Column(name = "port_charges_usd_ton", nullable = false)
    private Double portChargesUsdTon;

    @Column(name = "demurrage_risk_usd_ton", nullable = false)
    private Double demurrageRiskUsdTon;

    @Column(name = "lighterage_required", nullable = false)
    private Boolean lighterageRequired = false;

    @Column(name = "lighterage_anchorage", length = 128)
    private String lighterageAnchorage;

    @Column(name = "lightered_quantity_tons", nullable = false)
    private Double lighteredQuantityTons = 0.0;

    @Column(name = "lighterage_cost_usd_ton", nullable = false)
    private Double lighterageCostUsdTon = 0.0;

    @Column(name = "total_landed_cost_usd_ton", nullable = false)
    private Double totalLandedCostUsdTon;

    @Column(name = "total_landed_cost_inr_ton", nullable = false)
    private Double totalLandedCostInrTon;

    @Column(name = "estimated_cost_savings_inr", nullable = false)
    private Double estimatedCostSavingsInr;

    @Column(name = "decision_rationale", columnDefinition = "TEXT", nullable = false)
    private String decisionRationale;

    @Column(name = "demurrage_risk_level", nullable = false, length = 32)
    private String demurrageRiskLevel = "LOW";

    @Column(name = "alternative_port_recommendation", length = 255)
    private String alternativePortRecommendation;

    @Column(name = "volatility_alert_level", nullable = false, length = 32)
    private String volatilityAlertLevel = "NORMAL";

    @Column(nullable = false, length = 32)
    private String status = "OPTIMIZED"; // OPTIMIZED, CHARTER_BOOKED, IN_TRANSIT, DELIVERED

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public ProcurementPlan() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlanReference() { return planReference; }
    public void setPlanReference(String planReference) { this.planReference = planReference; }

    public String getCommodity() { return commodity; }
    public void setCommodity(String commodity) { this.commodity = commodity; }

    public Double getQuantityTons() { return quantityTons; }
    public void setQuantityTons(Double quantityTons) { this.quantityTons = quantityTons; }

    public String getOriginCountry() { return originCountry; }
    public void setOriginCountry(String originCountry) { this.originCountry = originCountry; }

    public MaritimePort getDestinationPort() { return destinationPort; }
    public void setDestinationPort(MaritimePort destinationPort) { this.destinationPort = destinationPort; }

    public LocalDate getTargetLaycanStart() { return targetLaycanStart; }
    public void setTargetLaycanStart(LocalDate targetLaycanStart) { this.targetLaycanStart = targetLaycanStart; }

    public LocalDate getTargetLaycanEnd() { return targetLaycanEnd; }
    public void setTargetLaycanEnd(LocalDate targetLaycanEnd) { this.targetLaycanEnd = targetLaycanEnd; }

    public String getRecommendedVesselClass() { return recommendedVesselClass; }
    public void setRecommendedVesselClass(String recommendedVesselClass) { this.recommendedVesselClass = recommendedVesselClass; }

    public String getCharterDecision() { return charterDecision; }
    public void setCharterDecision(String charterDecision) { this.charterDecision = charterDecision; }

    public String getContractType() { return contractType; }
    public void setContractType(String contractType) { this.contractType = contractType; }

    public Double getCoaDiscountPct() { return coaDiscountPct; }
    public void setCoaDiscountPct(Double coaDiscountPct) { this.coaDiscountPct = coaDiscountPct; }

    public Double getCoaTotalVolumeTons() { return coaTotalVolumeTons; }
    public void setCoaTotalVolumeTons(Double coaTotalVolumeTons) { this.coaTotalVolumeTons = coaTotalVolumeTons; }

    public Double getFobCargoPriceUsdTon() { return fobCargoPriceUsdTon; }
    public void setFobCargoPriceUsdTon(Double fobCargoPriceUsdTon) { this.fobCargoPriceUsdTon = fobCargoPriceUsdTon; }

    public Double getPredictedFreightUsdTon() { return predictedFreightUsdTon; }
    public void setPredictedFreightUsdTon(Double predictedFreightUsdTon) { this.predictedFreightUsdTon = predictedFreightUsdTon; }

    public Double getBunkerAdjustmentUsdTon() { return bunkerAdjustmentUsdTon; }
    public void setBunkerAdjustmentUsdTon(Double bunkerAdjustmentUsdTon) { this.bunkerAdjustmentUsdTon = bunkerAdjustmentUsdTon; }

    public Double getPortChargesUsdTon() { return portChargesUsdTon; }
    public void setPortChargesUsdTon(Double portChargesUsdTon) { this.portChargesUsdTon = portChargesUsdTon; }

    public Double getDemurrageRiskUsdTon() { return demurrageRiskUsdTon; }
    public void setDemurrageRiskUsdTon(Double demurrageRiskUsdTon) { this.demurrageRiskUsdTon = demurrageRiskUsdTon; }

    public Boolean getLighterageRequired() { return lighterageRequired; }
    public void setLighterageRequired(Boolean lighterageRequired) { this.lighterageRequired = lighterageRequired; }

    public String getLighterageAnchorage() { return lighterageAnchorage; }
    public void setLighterageAnchorage(String lighterageAnchorage) { this.lighterageAnchorage = lighterageAnchorage; }

    public Double getLighteredQuantityTons() { return lighteredQuantityTons; }
    public void setLighteredQuantityTons(Double lighteredQuantityTons) { this.lighteredQuantityTons = lighteredQuantityTons; }

    public Double getLighterageCostUsdTon() { return lighterageCostUsdTon; }
    public void setLighterageCostUsdTon(Double lighterageCostUsdTon) { this.lighterageCostUsdTon = lighterageCostUsdTon; }

    public Double getTotalLandedCostUsdTon() { return totalLandedCostUsdTon; }
    public void setTotalLandedCostUsdTon(Double totalLandedCostUsdTon) { this.totalLandedCostUsdTon = totalLandedCostUsdTon; }

    public Double getTotalLandedCostInrTon() { return totalLandedCostInrTon; }
    public void setTotalLandedCostInrTon(Double totalLandedCostInrTon) { this.totalLandedCostInrTon = totalLandedCostInrTon; }

    public Double getEstimatedCostSavingsInr() { return estimatedCostSavingsInr; }
    public void setEstimatedCostSavingsInr(Double estimatedCostSavingsInr) { this.estimatedCostSavingsInr = estimatedCostSavingsInr; }

    public String getDecisionRationale() { return decisionRationale; }
    public void setDecisionRationale(String decisionRationale) { this.decisionRationale = decisionRationale; }

    public String getDemurrageRiskLevel() { return demurrageRiskLevel; }
    public void setDemurrageRiskLevel(String demurrageRiskLevel) { this.demurrageRiskLevel = demurrageRiskLevel; }

    public String getAlternativePortRecommendation() { return alternativePortRecommendation; }
    public void setAlternativePortRecommendation(String alternativePortRecommendation) { this.alternativePortRecommendation = alternativePortRecommendation; }

    public String getVolatilityAlertLevel() { return volatilityAlertLevel; }
    public void setVolatilityAlertLevel(String volatilityAlertLevel) { this.volatilityAlertLevel = volatilityAlertLevel; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
