package com.sih.navisense.dto;

public class AlertDto {
    private String id;
    private String title;
    private String message;
    private String severity; // INFO, WARNING, DANGER, SUCCESS
    private String badgeClass;
    private String category; // FREIGHT, PORT, VESSEL, RISK
    private String timestamp;

    public AlertDto() {}

    public AlertDto(String id, String title, String message, String severity, String badgeClass, String category, String timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.badgeClass = badgeClass;
        this.category = category;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getBadgeClass() { return badgeClass; }
    public void setBadgeClass(String badgeClass) { this.badgeClass = badgeClass; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
