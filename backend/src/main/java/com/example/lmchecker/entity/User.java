package com.example.lmchecker.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String username;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(nullable = false, unique = true, length = 128)
    private String email;

    @Column(name = "full_name", length = 128)
    private String fullName;

    @Column(name = "phone_number", length = 32)
    private String phoneNumber;

    @Column(name = "provider_id", length = 128)
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider", nullable = false, length = 32)
    private AuthProvider authProvider = AuthProvider.LOCAL;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "account_status", nullable = false, length = 32)
    private String accountStatus = "ACTIVE";

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "locked_until")
    private LocalDateTime lockedUntil;

    @Column(nullable = false)
    private boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User() {}

    public User(Long id, String username, String passwordHash, String email, String fullName, Role role, boolean enabled, LocalDateTime createdAt) {
        this(id, username, passwordHash, email, fullName, null, null, role, AuthProvider.LOCAL, false, "ACTIVE", 0, null, enabled, createdAt, null);
    }

    public User(Long id, String username, String passwordHash, String email, String fullName, String phoneNumber, Role role, AuthProvider authProvider, boolean enabled, LocalDateTime createdAt) {
        this(id, username, passwordHash, email, fullName, phoneNumber, null, role, authProvider, false, "ACTIVE", 0, null, enabled, createdAt, null);
    }

    public User(Long id, String username, String passwordHash, String email, String fullName, String phoneNumber, String providerId, Role role, AuthProvider authProvider, boolean emailVerified, String accountStatus, int failedLoginAttempts, LocalDateTime lockedUntil, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.providerId = providerId;
        this.role = role != null ? role : Role.VIEWER;
        this.authProvider = authProvider != null ? authProvider : AuthProvider.LOCAL;
        this.emailVerified = emailVerified;
        this.accountStatus = accountStatus != null ? accountStatus : "ACTIVE";
        this.failedLoginAttempts = failedLoginAttempts;
        this.lockedUntil = lockedUntil;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public boolean isAccountNonLocked() {
        if (lockedUntil == null) return true;
        return LocalDateTime.now().isAfter(lockedUntil);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public AuthProvider getAuthProvider() { return authProvider; }
    public void setAuthProvider(AuthProvider authProvider) { this.authProvider = authProvider; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public LocalDateTime getLockedUntil() { return lockedUntil; }
    public void setLockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String username;
        private String passwordHash;
        private String email;
        private String fullName;
        private String phoneNumber;
        private String providerId;
        private Role role = Role.VIEWER;
        private AuthProvider authProvider = AuthProvider.LOCAL;
        private boolean emailVerified = false;
        private String accountStatus = "ACTIVE";
        private int failedLoginAttempts = 0;
        private LocalDateTime lockedUntil;
        private boolean enabled = true;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder passwordHash(String passwordHash) { this.passwordHash = passwordHash; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder providerId(String providerId) { this.providerId = providerId; return this; }
        public Builder role(Role role) { this.role = role; return this; }
        public Builder authProvider(AuthProvider authProvider) { this.authProvider = authProvider; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public Builder accountStatus(String accountStatus) { this.accountStatus = accountStatus; return this; }
        public Builder failedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; return this; }
        public Builder lockedUntil(LocalDateTime lockedUntil) { this.lockedUntil = lockedUntil; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public User build() {
            return new User(id, username, passwordHash, email, fullName, phoneNumber, providerId, role, authProvider, emailVerified, accountStatus, failedLoginAttempts, lockedUntil, enabled, createdAt, updatedAt);
        }
    }
}
