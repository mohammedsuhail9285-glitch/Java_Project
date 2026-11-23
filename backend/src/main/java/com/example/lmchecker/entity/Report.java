package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "report_name", length = 255)
    private String reportName;

    @Column(name = "requested_by")
    private Long requestedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReportFormat format;

    @Column(columnDefinition = "TEXT")
    private String filters;

    @Column(name = "file_path", length = 512)
    private String filePath;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public Report() {}

    public Report(Long id, String reportName, Long requestedBy, ReportFormat format, String filters, String filePath, LocalDateTime generatedAt) {
        this.id = id;
        this.reportName = reportName;
        this.requestedBy = requestedBy;
        this.format = format;
        this.filters = filters;
        this.filePath = filePath;
        this.generatedAt = generatedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getReportName() { return reportName; }
    public void setReportName(String reportName) { this.reportName = reportName; }

    public Long getRequestedBy() { return requestedBy; }
    public void setRequestedBy(Long requestedBy) { this.requestedBy = requestedBy; }

    public ReportFormat getFormat() { return format; }
    public void setFormat(ReportFormat format) { this.format = format; }

    public String getFilters() { return filters; }
    public void setFilters(String filters) { this.filters = filters; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String reportName;
        private Long requestedBy;
        private ReportFormat format;
        private String filters;
        private String filePath;
        private LocalDateTime generatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder reportName(String reportName) { this.reportName = reportName; return this; }
        public Builder requestedBy(Long requestedBy) { this.requestedBy = requestedBy; return this; }
        public Builder format(ReportFormat format) { this.format = format; return this; }
        public Builder filters(String filters) { this.filters = filters; return this; }
        public Builder filePath(String filePath) { this.filePath = filePath; return this; }
        public Builder generatedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; return this; }

        public Report build() {
            return new Report(id, reportName, requestedBy, format, filters, filePath, generatedAt);
        }
    }
}
