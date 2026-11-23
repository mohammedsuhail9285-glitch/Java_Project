package com.example.lmchecker.dto;

import jakarta.validation.constraints.NotBlank;

public class OtpVerifyDto {

    @NotBlank(message = "Identifier is required")
    private String identifier;

    @NotBlank(message = "Verification code is required")
    private String code;

    public OtpVerifyDto() {}

    public OtpVerifyDto(String identifier, String code) {
        this.identifier = identifier;
        this.code = code;
    }

    public String getIdentifier() { return identifier; }
    public void setIdentifier(String identifier) { this.identifier = identifier; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
