package com.example.lmchecker.dto;

import jakarta.validation.constraints.NotBlank;

public class OtpRequestDto {

    @NotBlank(message = "Identifier (email or mobile) is required")
    private String identifier;

    public OtpRequestDto() {}

    public OtpRequestDto(String identifier) {
        this.identifier = identifier;
    }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }
}
