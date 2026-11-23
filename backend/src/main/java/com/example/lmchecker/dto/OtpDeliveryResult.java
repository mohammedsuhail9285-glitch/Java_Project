package com.example.lmchecker.dto;

public class OtpDeliveryResult {
    private boolean success;
    private String message;
    private String devOtpHint;

    public OtpDeliveryResult() {}

    public OtpDeliveryResult(boolean success, String message, String devOtpHint) {
        this.success = success;
        this.message = message;
        this.devOtpHint = devOtpHint;
    }

    public static OtpDeliveryResult success(String message, String devOtpHint) {
        return new OtpDeliveryResult(true, message, devOtpHint);
    }

    public static OtpDeliveryResult failure(String message) {
        return new OtpDeliveryResult(false, message, null);
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getDevOtpHint() { return devOtpHint; }
    public void setDevOtpHint(String devOtpHint) { this.devOtpHint = devOtpHint; }
}
