package com.example.lmchecker.ai;

import com.example.lmchecker.ocr.BoundingBoxDto;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ExtractedFieldDto {
    @JsonProperty("field_type")
    private String fieldType;

    @JsonProperty("raw_value")
    private String rawValue;

    @JsonProperty("normalized_value")
    private String normalizedValue;

    private String unit;
    private double confidence;
    private BoundingBoxDto bbox;
    private String source; // "IMAGE" or "LISTING"

    public ExtractedFieldDto() {
    }

    public ExtractedFieldDto(String fieldType, String rawValue, String normalizedValue, String unit,
                             double confidence, BoundingBoxDto bbox, String source) {
        this.fieldType = fieldType;
        this.rawValue = rawValue;
        this.normalizedValue = normalizedValue;
        this.unit = unit;
        this.confidence = confidence;
        this.bbox = bbox;
        this.source = source;
    }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public String getRawValue() { return rawValue; }
    public void setRawValue(String rawValue) { this.rawValue = rawValue; }

    public String getNormalizedValue() { return normalizedValue; }
    public void setNormalizedValue(String normalizedValue) { this.normalizedValue = normalizedValue; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public BoundingBoxDto getBbox() { return bbox; }
    public void setBbox(BoundingBoxDto bbox) { this.bbox = bbox; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
