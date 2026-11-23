package com.example.lmchecker.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 128, message = "Full name cannot exceed 128 characters")
    private String fullName;

    @NotBlank(message = "Email address is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 128, message = "Email cannot exceed 128 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private String phone;
    private boolean acceptTerms = false;

    public RegisterRequest() {}

    public RegisterRequest(String fullName, String email, String password, String confirmPassword, boolean acceptTerms) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.acceptTerms = acceptTerms;
    }

    public RegisterRequest(String fullName, String email, String password, String confirmPassword, String phone, boolean acceptTerms) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.phone = phone;
        this.acceptTerms = acceptTerms;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }
}
