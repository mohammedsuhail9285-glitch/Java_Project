package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otp_codes", indexes = {
    @Index(name = "idx_otp_codes_identifier", columnList = "identifier"),
    @Index(name = "idx_otp_codes_created_at", columnList = "created_at")
})
public class OtpCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String identifier;

    @Column(name = "hashed_code", nullable = false, length = 255)
    private String hashedCode;

    @Column(nullable = false, length = 32)
    private String purpose = "LOGIN";

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private boolean consumed = false;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public OtpCode() {}

    public OtpCode(String identifier, String hashedCode, String purpose, LocalDateTime expiresAt) {
        this.identifier = identifier;
        this.hashedCode = hashedCode;
        this.purpose = purpose;
        this.expiresAt = expiresAt;
        this.attempts = 0;
        this.consumed = false;
        this.createdAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getHashedCode() { return hashedCode; }
    public void setHashedCode(String hashedCode) { this.hashedCode = hashedCode; }

    public String getPurpose() { return purpose; }
    public void setPurpose(String purpose) { this.purpose = purpose; }

    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }

    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }

    public boolean isConsumed() { return consumed; }
    public void setConsumed(boolean consumed) { this.consumed = consumed; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
