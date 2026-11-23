package com.example.lmchecker.dto;

import com.example.lmchecker.entity.Role;
import java.time.LocalDateTime;

public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private String authProvider;
    private String phoneNumber;
    private boolean emailVerified;
    private String accountStatus;
    private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserDto() {}

    public UserDto(Long id, String username, String email, String fullName, Role role, boolean enabled, LocalDateTime createdAt) {
        this(id, username, email, fullName, role, "LOCAL", null, false, "ACTIVE", enabled, createdAt, null);
    }

    public UserDto(Long id, String username, String email, String fullName, Role role, String authProvider, String phoneNumber, boolean enabled, LocalDateTime createdAt) {
        this(id, username, email, fullName, role, authProvider, phoneNumber, false, "ACTIVE", enabled, createdAt, null);
    }

    public UserDto(Long id, String username, String email, String fullName, Role role, String authProvider, String phoneNumber, boolean emailVerified, String accountStatus, boolean enabled, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.authProvider = authProvider;
        this.phoneNumber = phoneNumber;
        this.emailVerified = emailVerified;
        this.accountStatus = accountStatus;
        this.enabled = enabled;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public String getAuthProvider() { return authProvider; }
    public void setAuthProvider(String authProvider) { this.authProvider = authProvider; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public String getAccountStatus() { return accountStatus; }
    public void setAccountStatus(String accountStatus) { this.accountStatus = accountStatus; }

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
