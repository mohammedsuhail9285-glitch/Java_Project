package com.example.lmchecker.dto;

public class AuthConfigResponse {

    private boolean googleOAuthEnabled;
    private String otpDeliveryMode;
    private boolean demoMode;

    public AuthConfigResponse() {}

    public AuthConfigResponse(boolean googleOAuthEnabled, String otpDeliveryMode, boolean demoMode) {
        this.googleOAuthEnabled = googleOAuthEnabled;
        this.otpDeliveryMode = otpDeliveryMode;
        this.demoMode = demoMode;
    }

    public boolean isGoogleOAuthEnabled() { return googleOAuthEnabled; }
    public void setGoogleOAuthEnabled(boolean googleOAuthEnabled) { this.googleOAuthEnabled = googleOAuthEnabled; }

    public String getOtpDeliveryMode() { return otpDeliveryMode; }
    public void setOtpDeliveryMode(String otpDeliveryMode) { this.otpDeliveryMode = otpDeliveryMode; }

    public boolean isDemoMode() { return demoMode; }
    public void setDemoMode(boolean demoMode) { this.demoMode = demoMode; }
}
