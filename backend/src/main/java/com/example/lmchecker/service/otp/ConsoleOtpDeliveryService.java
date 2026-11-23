package com.example.lmchecker.service.otp;

import com.example.lmchecker.dto.OtpDeliveryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service("consoleOtpDeliveryService")
public class ConsoleOtpDeliveryService implements OtpDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(ConsoleOtpDeliveryService.class);

    @Value("${app.otp.delivery-mode:demo}")
    private String configuredMode;

    @Override
    public OtpDeliveryResult sendOtp(String identifier, String rawCode, int expiryMinutes) {
        boolean isDemo = "demo".equalsIgnoreCase(configuredMode);

        log.info("================================================================================");
        log.info("[OTP CONSOLE DISPATCHER] Destination: {}", identifier);
        log.info("[OTP CONSOLE DISPATCHER] Generated One-Time Code: {}", rawCode);
        log.info("[OTP CONSOLE DISPATCHER] Valid for: {} minutes", expiryMinutes);
        log.info("[OTP CONSOLE DISPATCHER] Demo Mode Active: {}", isDemo);
        log.info("================================================================================");

        String message = isDemo
            ? "Verification code generated in DEMO mode. Check the demo hint banner or backend console."
            : "Verification code has been dispatched.";

        String devHint = isDemo ? rawCode : null;

        return OtpDeliveryResult.success(message, devHint);
    }

    @Override
    public String getMode() {
        return "demo";
    }
}
