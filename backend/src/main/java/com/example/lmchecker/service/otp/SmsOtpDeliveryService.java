package com.example.lmchecker.service.otp;

import com.example.lmchecker.dto.OtpDeliveryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Stub delivery service for mobile SMS OTP.
 *
 * NOT IMPLEMENTED — integrate a provider (e.g., Twilio, MSG91) here.
 * In local/demo mode without a paid SMS account, mobile-number OTP requests
 * receive a clear explanatory response instructing the user to use email OTP.
 */
@Service("smsOtpDeliveryService")
public class SmsOtpDeliveryService implements OtpDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(SmsOtpDeliveryService.class);

    @Override
    public OtpDeliveryResult sendOtp(String identifier, String rawCode, int expiryMinutes) {
        log.warn("Mobile SMS OTP requested for {}, but SMS provider is NOT IMPLEMENTED.", identifier);
        return OtpDeliveryResult.failure("SMS OTP requires configuring an SMS provider; email OTP is available now.");
    }

    @Override
    public String getMode() {
        return "sms";
    }
}
