package com.example.lmchecker.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateProfileRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 128, message = "Full name cannot exceed 128 characters")
    private String fullName;

    @Size(max = 32, message = "Phone number cannot exceed 32 characters")
    private String phoneNumber;

    public UpdateProfileRequest() {}

    public UpdateProfileRequest(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
}
