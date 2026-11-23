package com.example.lmchecker.dto;

import com.example.lmchecker.entity.RiskLevel;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ProductDetailDto {
    private Long id;
    private String name;
    private Long brandId;
    private String brandName;
    private Long sellerId;
    private String sellerName;
    private Long categoryId;
    private String categoryName;
    private Double declaredMrp;
    private Double listingPrice;
    private String description;
    private String countryOfOrigin;
    private String productUrl;
    private String ingestionSource;
    private String status;
    private LocalDateTime createdAt;

    // Associated Entities
    private List<String> imageUrls;
    private Map<String, Object> listingFields;
    private List<ExtractedFieldDto> extractedFields;
    private ComplianceCheckSummaryDto complianceCheck;
    private List<ViolationDto> violations;

    public ProductDetailDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getBrandId() { return brandId; }
    public void setBrandId(Long brandId) { this.brandId = brandId; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public Long getSellerId() { return sellerId; }
    public void setSellerId(Long sellerId) { this.sellerId = sellerId; }

    public String getSellerName() { return sellerName; }
    public void setSellerName(String sellerName) { this.sellerName = sellerName; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public Double getDeclaredMrp() { return declaredMrp; }
    public void setDeclaredMrp(Double declaredMrp) { this.declaredMrp = declaredMrp; }

    public Double getListingPrice() { return listingPrice; }
    public void setListingPrice(Double listingPrice) { this.listingPrice = listingPrice; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCountryOfOrigin() { return countryOfOrigin; }
    public void setCountryOfOrigin(String countryOfOrigin) { this.countryOfOrigin = countryOfOrigin; }

    public String getProductUrl() { return productUrl; }
    public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

    public String getIngestionSource() { return ingestionSource; }
    public void setIngestionSource(String ingestionSource) { this.ingestionSource = ingestionSource; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }

    public Map<String, Object> getListingFields() { return listingFields; }
    public void setListingFields(Map<String, Object> listingFields) { this.listingFields = listingFields; }

    public List<ExtractedFieldDto> getExtractedFields() { return extractedFields; }
    public void setExtractedFields(List<ExtractedFieldDto> extractedFields) { this.extractedFields = extractedFields; }

    public ComplianceCheckSummaryDto getComplianceCheck() { return complianceCheck; }
    public void setComplianceCheck(ComplianceCheckSummaryDto complianceCheck) { this.complianceCheck = complianceCheck; }

    public List<ViolationDto> getViolations() { return violations; }
    public void setViolations(List<ViolationDto> violations) { this.violations = violations; }

    public static class ExtractedFieldDto {
        private Long id;
        private String fieldType;
        private String rawValue;
        private String normalizedValue;
        private String unit;
        private String source;
        private Double confidence;
        private String boundingBox;

        public ExtractedFieldDto() {}
        public ExtractedFieldDto(Long id, String fieldType, String rawValue, String normalizedValue, String unit, String source, Double confidence, String boundingBox) {
            this.id = id;
            this.fieldType = fieldType;
            this.rawValue = rawValue;
            this.normalizedValue = normalizedValue;
            this.unit = unit;
            this.source = source;
            this.confidence = confidence;
            this.boundingBox = boundingBox;
        }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFieldType() { return fieldType; }
        public void setFieldType(String fieldType) { this.fieldType = fieldType; }
        public String getRawValue() { return rawValue; }
        public void setRawValue(String rawValue) { this.rawValue = rawValue; }
        public String getNormalizedValue() { return normalizedValue; }
        public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }
        public String getUnit() { return unit; }
        public void setUnit(String unit) { this.unit = unit; }
        public String getSource() { return source; }
        public void setSource(String source) { this.source = source; }
        public Double getConfidence() { return confidence; }
        public void setConfidence(Double confidence) { this.confidence = confidence; }
        public String getBoundingBox() { return boundingBox; }
        public void setBoundingBox(String boundingBox) { this.boundingBox = boundingBox; }
    }

    public static class ComplianceCheckSummaryDto {
        private Double overallScore;
        private Double mandatoryFieldCompletionPct;
        private Integer criticalCount;
        private Integer majorCount;
        private Integer minorCount;
        private Double ocrConfidence;
        private Double aiConfidence;
        private Double ruleEngineConfidence;
        private RiskLevel riskLevel;
        private String summaryNotes;

        public ComplianceCheckSummaryDto() {}
        public ComplianceCheckSummaryDto(Double overallScore, Double mandatoryFieldCompletionPct, Integer criticalCount, Integer majorCount, Integer minorCount, Double ocrConfidence, Double aiConfidence, Double ruleEngineConfidence, RiskLevel riskLevel, String summaryNotes) {
            this.overallScore = overallScore;
            this.mandatoryFieldCompletionPct = mandatoryFieldCompletionPct;
            this.criticalCount = criticalCount;
            this.majorCount = majorCount;
            this.minorCount = minorCount;
            this.ocrConfidence = ocrConfidence;
            this.aiConfidence = aiConfidence;
            this.ruleEngineConfidence = ruleEngineConfidence;
            this.riskLevel = riskLevel;
            this.summaryNotes = summaryNotes;
        }

        public Double getOverallScore() { return overallScore; }
        public void setOverallScore(Double overallScore) { this.overallScore = overallScore; }
        public Double getMandatoryFieldCompletionPct() { return mandatoryFieldCompletionPct; }
        public void setMandatoryFieldCompletionPct(Double mandatoryFieldCompletionPct) { this.mandatoryFieldCompletionPct = mandatoryFieldCompletionPct; }
        public Integer getCriticalCount() { return criticalCount; }
        public void setCriticalCount(Integer criticalCount) { this.criticalCount = criticalCount; }
        public Integer getMajorCount() { return majorCount; }
        public void setMajorCount(Integer majorCount) { this.majorCount = majorCount; }
        public Integer getMinorCount() { return minorCount; }
        public void setMinorCount(Integer minorCount) { this.minorCount = minorCount; }
        public Double getOcrConfidence() { return ocrConfidence; }
        public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }
        public Double getAiConfidence() { return aiConfidence; }
        public void setAiConfidence(Double aiConfidence) { this.aiConfidence = aiConfidence; }
        public Double getRuleEngineConfidence() { return ruleEngineConfidence; }
        public void setRuleEngineConfidence(Double ruleEngineConfidence) { this.ruleEngineConfidence = ruleEngineConfidence; }
        public RiskLevel getRiskLevel() { return riskLevel; }
        public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
        public String getSummaryNotes() { return summaryNotes; }
        public void setSummaryNotes(String summaryNotes) { this.summaryNotes = summaryNotes; }
    }

    public static class ViolationDto {
        private Long id;
        private String violationId;
        private String ruleId;
        private String ruleName;
        private String severity;
        private String detectedValue;
        private String expectedCondition;
        private String legalSource;
        private String remediationMessage;
        private String status;
        private Double ocrConfidence;
        private Double detectionConfidence;
        private String originalImagePath;
        private String croppedImagePath;
        private String ocrText;
        private String boundingBox;
        private Long reviewerId;
        private String reviewerComment;

        public ViolationDto() {}

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getViolationId() { return violationId; }
        public void setViolationId(String violationId) { this.violationId = violationId; }
        public String getRuleId() { return ruleId; }
        public void setRuleId(String ruleId) { this.ruleId = ruleId; }
        public String getRuleName() { return ruleName; }
        public void setRuleName(String ruleName) { this.ruleName = ruleName; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
        public String getDetectedValue() { return detectedValue; }
        public void setDetectedValue(String detectedValue) { this.detectedValue = detectedValue; }
        public String getExpectedCondition() { return expectedCondition; }
        public void setExpectedCondition(String expectedCondition) { this.expectedCondition = expectedCondition; }
        public String getLegalSource() { return legalSource; }
        public void setLegalSource(String legalSource) { this.legalSource = legalSource; }
        public String getRemediationMessage() { return remediationMessage; }
        public void setRemediationMessage(String remediationMessage) { this.remediationMessage = remediationMessage; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public Double getOcrConfidence() { return ocrConfidence; }
        public void setOcrConfidence(Double ocrConfidence) { this.ocrConfidence = ocrConfidence; }
        public Double getDetectionConfidence() { return detectionConfidence; }
        public void setDetectionConfidence(Double detectionConfidence) { this.detectionConfidence = detectionConfidence; }
        public String getOriginalImagePath() { return originalImagePath; }
        public void setOriginalImagePath(String originalImagePath) { this.originalImagePath = originalImagePath; }
        public String getCroppedImagePath() { return croppedImagePath; }
        public void setCroppedImagePath(String croppedImagePath) { this.croppedImagePath = croppedImagePath; }
        public String getOcrText() { return ocrText; }
        public void setOcrText(String ocrText) { this.ocrText = ocrText; }
        public String getBoundingBox() { return boundingBox; }
        public void setBoundingBox(String boundingBox) { this.boundingBox = boundingBox; }
        public Long getReviewerId() { return reviewerId; }
        public void setReviewerId(Long reviewerId) { this.reviewerId = reviewerId; }
        public String getReviewerComment() { return reviewerComment; }
        public void setReviewerComment(String reviewerComment) { this.reviewerComment = reviewerComment; }
    }
}
