package com.example.lmchecker.service.otp;

import com.example.lmchecker.dto.OtpDeliveryResult;

public interface OtpDeliveryService {

    /**
     * Deliver the one-time verification code to the given identifier.
     *
     * @param identifier email address or mobile phone number
     * @param rawCode the 6-digit plain OTP code
     * @param expiryMinutes number of minutes until code expires
     * @return delivery result with status, client message, and optional demo hint
     */
    OtpDeliveryResult sendOtp(String identifier, String rawCode, int expiryMinutes);

    /**
     * The delivery mode name (e.g. "demo", "smtp", "sms").
     */
    String getMode();
}
