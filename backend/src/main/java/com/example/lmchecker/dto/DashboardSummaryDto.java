package com.example.lmchecker.dto;

import java.util.List;
import java.util.Map;

public class DashboardSummaryDto {
    private long totalProducts;
    private long compliantProducts;
    private long potentialNonCompliantProducts;
    private long requiresReviewProducts;
    private long pendingScanProducts;
    private double overallComplianceRate;

    private long totalViolations;
    private long criticalViolations;
    private long majorViolations;
    private long minorViolations;

    private long totalScans;
    private long activeScans;

    private List<CategoryRiskDto> categoryRisks;
    private List<SellerRiskDto> sellerRisks;
    private List<TrendDataPointDto> complianceTrends;
    private Map<String, Long> violationDistribution;

    private String statutoryDisclaimer = "Regulatory Decision Support: Findings indicate potential non-compliance and do not constitute a final legal determination under Legal Metrology Act, 2009.";

    public DashboardSummaryDto() {}

    public long getTotalProducts() { return totalProducts; }
    public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }

    public long getCompliantProducts() { return compliantProducts; }
    public void setCompliantProducts(long compliantProducts) { this.compliantProducts = compliantProducts; }

    public long getPotentialNonCompliantProducts() { return potentialNonCompliantProducts; }
    public void setPotentialNonCompliantProducts(long potentialNonCompliantProducts) { this.potentialNonCompliantProducts = potentialNonCompliantProducts; }

    public long getRequiresReviewProducts() { return requiresReviewProducts; }
    public void setRequiresReviewProducts(long requiresReviewProducts) { this.requiresReviewProducts = requiresReviewProducts; }

    public long getPendingScanProducts() { return pendingScanProducts; }
    public void setPendingScanProducts(long pendingScanProducts) { this.pendingScanProducts = pendingScanProducts; }

    public double getOverallComplianceRate() { return overallComplianceRate; }
    public void setOverallComplianceRate(double overallComplianceRate) { this.overallComplianceRate = overallComplianceRate; }

    public long getTotalViolations() { return totalViolations; }
    public void setTotalViolations(long totalViolations) { this.totalViolations = totalViolations; }

    public long getCriticalViolations() { return criticalViolations; }
    public void setCriticalViolations(long criticalViolations) { this.criticalViolations = criticalViolations; }

    public long getMajorViolations() { return majorViolations; }
    public void setMajorViolations(long majorViolations) { this.majorViolations = majorViolations; }

    public long getMinorViolations() { return minorViolations; }
    public void setMinorViolations(long minorViolations) { this.minorViolations = minorViolations; }

    public long getTotalScans() { return totalScans; }
    public void setTotalScans(long totalScans) { this.totalScans = totalScans; }

    public long getActiveScans() { return activeScans; }
    public void setActiveScans(long activeScans) { this.activeScans = activeScans; }

    public List<CategoryRiskDto> getCategoryRisks() { return categoryRisks; }
    public void setCategoryRisks(List<CategoryRiskDto> categoryRisks) { this.categoryRisks = categoryRisks; }

    public List<SellerRiskDto> getSellerRisks() { return sellerRisks; }
    public void setSellerRisks(List<SellerRiskDto> sellerRisks) { this.sellerRisks = sellerRisks; }

    public List<TrendDataPointDto> getComplianceTrends() { return complianceTrends; }
    public void setComplianceTrends(List<TrendDataPointDto> complianceTrends) { this.complianceTrends = complianceTrends; }

    public Map<String, Long> getViolationDistribution() { return violationDistribution; }
    public void setViolationDistribution(Map<String, Long> violationDistribution) { this.violationDistribution = violationDistribution; }

    public String getStatutoryDisclaimer() { return statutoryDisclaimer; }
    public void setStatutoryDisclaimer(String statutoryDisclaimer) { this.statutoryDisclaimer = statutoryDisclaimer; }

    public static class CategoryRiskDto {
        private String category;
        private long totalProducts;
        private long nonCompliantCount;
        private double complianceRate;
        private String riskLevel;

        public CategoryRiskDto() {}
        public CategoryRiskDto(String category, long totalProducts, long nonCompliantCount, double complianceRate, String riskLevel) {
            this.category = category;
            this.totalProducts = totalProducts;
            this.nonCompliantCount = nonCompliantCount;
            this.complianceRate = complianceRate;
            this.riskLevel = riskLevel;
        }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        public long getNonCompliantCount() { return nonCompliantCount; }
        public void setNonCompliantCount(long nonCompliantCount) { this.nonCompliantCount = nonCompliantCount; }
        public double getComplianceRate() { return complianceRate; }
        public void setComplianceRate(double complianceRate) { this.complianceRate = complianceRate; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    }

    public static class SellerRiskDto {
        private String sellerName;
        private String platform;
        private long totalProducts;
        private long violationCount;
        private double complianceRate;
        private String riskLevel;

        public SellerRiskDto() {}
        public SellerRiskDto(String sellerName, String platform, long totalProducts, long violationCount, double complianceRate, String riskLevel) {
            this.sellerName = sellerName;
            this.platform = platform;
            this.totalProducts = totalProducts;
            this.violationCount = violationCount;
            this.complianceRate = complianceRate;
            this.riskLevel = riskLevel;
        }

        public String getSellerName() { return sellerName; }
        public void setSellerName(String sellerName) { this.sellerName = sellerName; }
        public String getPlatform() { return platform; }
        public void setPlatform(String platform) { this.platform = platform; }
        public long getTotalProducts() { return totalProducts; }
        public void setTotalProducts(long totalProducts) { this.totalProducts = totalProducts; }
        public long getViolationCount() { return violationCount; }
        public void setViolationCount(long violationCount) { this.violationCount = violationCount; }
        public double getComplianceRate() { return complianceRate; }
        public void setComplianceRate(double complianceRate) { this.complianceRate = complianceRate; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    }

    public static class TrendDataPointDto {
        private String date;
        private double complianceRate;
        private long potentialViolations;

        public TrendDataPointDto() {}
        public TrendDataPointDto(String date, double complianceRate, long potentialViolations) {
            this.date = date;
            this.complianceRate = complianceRate;
            this.potentialViolations = potentialViolations;
        }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public double getComplianceRate() { return complianceRate; }
        public void setComplianceRate(double complianceRate) { this.complianceRate = complianceRate; }
        public long getPotentialViolations() { return potentialViolations; }
        public void setPotentialViolations(long potentialViolations) { this.potentialViolations = potentialViolations; }
    }
}
