package com.example.lmchecker.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OCRLineDto {
    private String text;
    private double confidence;
    private BoundingBoxDto bbox;

    public OCRLineDto() {
    }

    public OCRLineDto(String text, double confidence, BoundingBoxDto bbox) {
        this.text = text;
        this.confidence = confidence;
        this.bbox = bbox;
    }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public double getConfidence() { return confidence; }
    public void setConfidence(double confidence) { this.confidence = confidence; }

    public BoundingBoxDto getBbox() { return bbox; }
    public void setBbox(BoundingBoxDto bbox) { this.bbox = bbox; }
}
