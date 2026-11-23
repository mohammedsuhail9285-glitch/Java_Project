package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "evidences")
public class Evidence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "violation_id")
    private Long violationId;

    @Column(name = "original_image_path", length = 512)
    private String originalImagePath;

    @Column(name = "cropped_image_path", length = 512)
    private String croppedImagePath;

    @Column(name = "ocr_text", columnDefinition = "TEXT")
    private String ocrText;

    @Column(name = "source_url_or_dataset", length = 512)
    private String sourceUrlOrDataset;

    @Column(name = "bounding_box", length = 128)
    private String boundingBox;

    @Column(name = "detection_timestamp", nullable = false)
    private LocalDateTime detectionTimestamp;

    public Evidence() {}

    public Evidence(Long id, Long violationId, String originalImagePath, String croppedImagePath, String ocrText, String sourceUrlOrDataset, String boundingBox, LocalDateTime detectionTimestamp) {
        this.id = id;
        this.violationId = violationId;
        this.originalImagePath = originalImagePath;
        this.croppedImagePath = croppedImagePath;
        this.ocrText = ocrText;
        this.sourceUrlOrDataset = sourceUrlOrDataset;
        this.boundingBox = boundingBox;
        this.detectionTimestamp = detectionTimestamp;
    }

    @PrePersist
    protected void onCreate() {
        if (detectionTimestamp == null) {
            detectionTimestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getViolationId() { return violationId; }
    public void setViolationId(Long violationId) { this.violationId = violationId; }

    public String getOriginalImagePath() { return originalImagePath; }
    public void setOriginalImagePath(String originalImagePath) { this.originalImagePath = originalImagePath; }

    public String getCroppedImagePath() { return croppedImagePath; }
    public void setCroppedImagePath(String croppedImagePath) { this.croppedImagePath = croppedImagePath; }

    public String getOcrText() { return ocrText; }
    public void setOcrText(String ocrText) { this.ocrText = ocrText; }

    public String getSourceUrlOrDataset() { return sourceUrlOrDataset; }
    public void setSourceUrlOrDataset(String sourceUrlOrDataset) { this.sourceUrlOrDataset = sourceUrlOrDataset; }

    public String getBoundingBox() { return boundingBox; }
    public void setBoundingBox(String boundingBox) { this.boundingBox = boundingBox; }

    public LocalDateTime getDetectionTimestamp() { return detectionTimestamp; }
    public void setDetectionTimestamp(LocalDateTime detectionTimestamp) { this.detectionTimestamp = detectionTimestamp; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long violationId;
        private String originalImagePath;
        private String croppedImagePath;
        private String ocrText;
        private String sourceUrlOrDataset;
        private String boundingBox;
        private LocalDateTime detectionTimestamp;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder violationId(Long violationId) { this.violationId = violationId; return this; }
        public Builder originalImagePath(String originalImagePath) { this.originalImagePath = originalImagePath; return this; }
        public Builder croppedImagePath(String croppedImagePath) { this.croppedImagePath = croppedImagePath; return this; }
        public Builder ocrText(String ocrText) { this.ocrText = ocrText; return this; }
        public Builder sourceUrlOrDataset(String sourceUrlOrDataset) { this.sourceUrlOrDataset = sourceUrlOrDataset; return this; }
        public Builder boundingBox(String boundingBox) { this.boundingBox = boundingBox; return this; }
        public Builder detectionTimestamp(LocalDateTime detectionTimestamp) { this.detectionTimestamp = detectionTimestamp; return this; }

        public Evidence build() {
            return new Evidence(id, violationId, originalImagePath, croppedImagePath, ocrText, sourceUrlOrDataset, boundingBox, detectionTimestamp);
        }
    }
}
