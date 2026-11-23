package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "audit_logs",
    indexes = {
        @Index(name = "idx_audit_logs_timestamp", columnList = "timestamp"),
        @Index(name = "idx_audit_logs_user_id", columnList = "user_id")
    }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(length = 64)
    private String username;

    @Column(nullable = false, length = 64)
    private String action;

    @Column(nullable = false, length = 64)
    private String entity;

    @Column(name = "entity_id", length = 64)
    private String entityId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "ip_address", length = 64)
    private String ipAddress;

    @Column(name = "old_value", columnDefinition = "TEXT")
    private String oldValue;

    @Column(name = "new_value", columnDefinition = "TEXT")
    private String newValue;

    public AuditLog() {}

    public AuditLog(Long id, Long userId, String username, String action, String entity, String entityId, LocalDateTime timestamp, String ipAddress, String oldValue, String newValue) {
        this.id = id;
        this.userId = userId;
        this.username = username;
        this.action = action;
        this.entity = entity;
        this.entityId = entityId;
        this.timestamp = timestamp;
        this.ipAddress = ipAddress;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    @PrePersist
    protected void onCreate() {
        if (timestamp == null) {
            timestamp = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntity() { return entity; }
    public void setEntity(String entity) { this.entity = entity; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Long userId;
        private String username;
        private String action;
        private String entity;
        private String entityId;
        private LocalDateTime timestamp;
        private String ipAddress;
        private String oldValue;
        private String newValue;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.userId = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder action(String action) { this.action = action; return this; }
        public Builder entity(String entity) { this.entity = entity; return this; }
        public Builder entityId(String entityId) { this.entityId = entityId; return this; }
        public Builder timestamp(LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
        public Builder ipAddress(String ipAddress) { this.ipAddress = ipAddress; return this; }
        public Builder oldValue(String oldValue) { this.oldValue = oldValue; return this; }
        public Builder newValue(String newValue) { this.newValue = newValue; return this; }

        public AuditLog build() {
            return new AuditLog(id, userId, username, action, entity, entityId, timestamp, ipAddress, oldValue, newValue);
        }
    }
}
