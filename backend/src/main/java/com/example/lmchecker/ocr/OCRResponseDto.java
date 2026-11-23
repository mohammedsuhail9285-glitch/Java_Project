package com.example.lmchecker.ocr;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

public class OCRResponseDto {
    @JsonProperty("raw_text")
    private String rawText;
    private String language;
    private List<OCRLineDto> lines = new ArrayList<>();
    @JsonProperty("avg_confidence")
    private double avgConfidence;
    private String engine;
    @JsonProperty("processing_time_ms")
    private double processingTimeMs;
    @JsonProperty("is_synthetic")
    private boolean synthetic;

    public OCRResponseDto() {
    }

    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public List<OCRLineDto> getLines() { return lines; }
    public void setLines(List<OCRLineDto> lines) { this.lines = lines; }

    public double getAvgConfidence() { return avgConfidence; }
    public void setAvgConfidence(double avgConfidence) { this.avgConfidence = avgConfidence; }

    public String getEngine() { return engine; }
    public void setEngine(String engine) { this.engine = engine; }

    public double getProcessingTimeMs() { return processingTimeMs; }
    public void setProcessingTimeMs(double processingTimeMs) { this.processingTimeMs = processingTimeMs; }

    public boolean isSynthetic() { return synthetic; }
    public void setSynthetic(boolean synthetic) { this.synthetic = synthetic; }
}
