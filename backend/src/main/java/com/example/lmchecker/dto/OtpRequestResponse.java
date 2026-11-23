package com.example.lmchecker.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OtpRequestResponse {

    private String message;
    private String devOtpHint;
    private int expiresInSeconds;
    private int cooldownSeconds;
    private String deliveryChannel;

    public OtpRequestResponse() {}

    public OtpRequestResponse(String message, String devOtpHint, int expiresInSeconds, int cooldownSeconds, String deliveryChannel) {
        this.message = message;
        this.devOtpHint = devOtpHint;
        this.expiresInSeconds = expiresInSeconds;
        this.cooldownSeconds = cooldownSeconds;
        this.deliveryChannel = deliveryChannel;
    }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDevOtpHint() { return devOtpHint; }
    public void setDevOtpHint(String devOtpHint) { this.devOtpHint = devOtpHint; }

    public int getExpiresInSeconds() { return expiresInSeconds; }
    public void setExpiresInSeconds(int expiresInSeconds) { this.expiresInSeconds = expiresInSeconds; }

    public int getCooldownSeconds() { return cooldownSeconds; }
    public void setCooldownSeconds(int cooldownSeconds) { this.cooldownSeconds = cooldownSeconds; }

    public String getDeliveryChannel() { return deliveryChannel; }
    public void setDeliveryChannel(String deliveryChannel) { this.deliveryChannel = deliveryChannel; }
}
