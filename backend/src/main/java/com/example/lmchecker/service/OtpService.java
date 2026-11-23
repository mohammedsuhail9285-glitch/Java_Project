package com.example.lmchecker.service;

import com.example.lmchecker.audit.AuditLogService;
import com.example.lmchecker.dto.*;
import com.example.lmchecker.entity.OtpCode;
import com.example.lmchecker.entity.User;
import com.example.lmchecker.exception.BadRequestException;
import com.example.lmchecker.exception.UnauthorizedException;
import com.example.lmchecker.mapper.UserMapper;
import com.example.lmchecker.repository.OtpCodeRepository;
import com.example.lmchecker.repository.UserRepository;
import com.example.lmchecker.security.JwtUtils;
import com.example.lmchecker.service.otp.ConsoleOtpDeliveryService;
import com.example.lmchecker.service.otp.OtpDeliveryService;
import com.example.lmchecker.service.otp.SmsOtpDeliveryService;
import com.example.lmchecker.service.otp.SmtpOtpDeliveryService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class OtpService {

    private static final Logger log = LoggerFactory.getLogger(OtpService.class);
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9\\-\\s()]{7,20}$");
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_HOURLY_REQUESTS = 5;
    private static final int MAX_VERIFY_ATTEMPTS = 5;

    private final OtpCodeRepository otpCodeRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;

    private final ConsoleOtpDeliveryService consoleDeliveryService;
    private final SmtpOtpDeliveryService smtpDeliveryService;
    private final SmsOtpDeliveryService smsDeliveryService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${app.otp.delivery-mode:demo}")
    private String deliveryMode;

    public OtpService(
            OtpCodeRepository otpCodeRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtUtils jwtUtils,
            UserMapper userMapper,
            AuditLogService auditLogService,
            ConsoleOtpDeliveryService consoleDeliveryService,
            SmtpOtpDeliveryService smtpDeliveryService,
            SmsOtpDeliveryService smsDeliveryService) {
        this.otpCodeRepository = otpCodeRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userMapper = userMapper;
        this.auditLogService = auditLogService;
        this.consoleDeliveryService = consoleDeliveryService;
        this.smtpDeliveryService = smtpDeliveryService;
        this.smsDeliveryService = smsDeliveryService;
    }

    public OtpRequestResponse requestOtp(OtpRequestDto requestDto, HttpServletRequest httpRequest) {
        String rawIdentifier = requestDto.getIdentifier() != null ? requestDto.getIdentifier().trim() : "";
        if (rawIdentifier.isEmpty()) {
            throw new BadRequestException("Identifier (email or mobile number) is required.");
        }

        boolean isMobile = isMobileNumber(rawIdentifier);

        // Mobile SMS check: clearly inform that SMS provider is not yet configured
        if (isMobile) {
            log.info("Mobile OTP requested for {}. Returning SMS provider requirement notice.", rawIdentifier);
            OtpDeliveryResult result = smsDeliveryService.sendOtp(rawIdentifier, null, OTP_EXPIRY_MINUTES);
            throw new BadRequestException(result.getMessage());
        }

        // Rate limit check: max 5 requests per hour per identifier
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        long requestCount = otpCodeRepository.countByIdentifierAndCreatedAtAfter(rawIdentifier.toLowerCase(), oneHourAgo);
        if (requestCount >= MAX_HOURLY_REQUESTS) {
            log.warn("Rate limit exceeded for OTP request on identifier: {}", rawIdentifier);
            throw new BadRequestException("Too many OTP requests for this identifier. Please try again after 1 hour.");
        }

        // Check if user exists (to prevent silent registration and avoid enumeration)
        Optional<User> userOpt = findUserByIdentifier(rawIdentifier);

        if (userOpt.isEmpty()) {
            log.info("OTP requested for unregistered identifier: {}. Responding generically to prevent enumeration.", rawIdentifier);
            // Return generic response without revealing account existence
            return new OtpRequestResponse(
                "If this identifier is registered with an account, a verification code has been dispatched.",
                null,
                OTP_EXPIRY_MINUTES * 60,
                60,
                "EMAIL"
            );
        }

        User user = userOpt.get();

        // Invalidate any previous unconsumed codes for this identifier to maintain single-code window
        var pendingCodes = otpCodeRepository.findByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(rawIdentifier.toLowerCase(), "LOGIN");
        for (OtpCode code : pendingCodes) {
            code.setConsumed(true);
        }
        otpCodeRepository.saveAll(pendingCodes);

        // Generate 6-digit random code
        String rawCode = String.format("%06d", secureRandom.nextInt(1_000_000));
        String hashedCode = passwordEncoder.encode(rawCode);

        // Store hashed code with 5-minute expiration
        OtpCode otpCode = new OtpCode(
            rawIdentifier.toLowerCase(),
            hashedCode,
            "LOGIN",
            LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES)
        );
        otpCodeRepository.save(otpCode);

        // Select delivery service based on configuration
        OtpDeliveryService deliveryService = resolveDeliveryService();
        OtpDeliveryResult deliveryResult = deliveryService.sendOtp(rawIdentifier, rawCode, OTP_EXPIRY_MINUTES);

        // Audit log OTP request without revealing the code
        auditLogService.logAction(
            "OTP_REQUESTED",
            "USER",
            user.getId().toString(),
            null,
            "Delivery: " + deliveryService.getMode() + ", Identifier: " + maskIdentifier(rawIdentifier),
            httpRequest
        );

        return new OtpRequestResponse(
            deliveryResult.getMessage(),
            deliveryResult.getDevOtpHint(),
            OTP_EXPIRY_MINUTES * 60,
            60,
            deliveryService.getMode().equalsIgnoreCase("smtp") ? "EMAIL" : "DEMO_CONSOLE"
        );
    }

    @Transactional
    public LoginResponse verifyOtp(OtpVerifyDto verifyDto, HttpServletRequest httpRequest) {
        String identifier = verifyDto.getIdentifier() != null ? verifyDto.getIdentifier().trim().toLowerCase() : "";
        String code = verifyDto.getCode() != null ? verifyDto.getCode().trim() : "";

        if (identifier.isEmpty() || code.isEmpty()) {
            throw new BadRequestException("Identifier and verification code are required.");
        }

        // Look for active, unconsumed code
        OtpCode otpCode = otpCodeRepository
            .findFirstByIdentifierAndPurposeAndConsumedFalseOrderByCreatedAtDesc(identifier, "LOGIN")
            .orElseThrow(() -> new BadRequestException("Invalid or expired verification code. Please request a new code."));

        // Check if expired
        if (otpCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            otpCode.setConsumed(true);
            otpCodeRepository.save(otpCode);
            throw new BadRequestException("Verification code has expired. Please request a new code.");
        }

        // Check attempt threshold (max 5 tries)
        if (otpCode.getAttempts() >= MAX_VERIFY_ATTEMPTS) {
            otpCode.setConsumed(true);
            otpCodeRepository.save(otpCode);
            throw new BadRequestException("Too many invalid verification attempts. Code has been invalidated. Please request a new code.");
        }

        // Increment attempt count
        otpCode.setAttempts(otpCode.getAttempts() + 1);

        // Verify match against stored BCrypt hash
        if (!passwordEncoder.matches(code, otpCode.getHashedCode())) {
            otpCodeRepository.save(otpCode);
            int remaining = MAX_VERIFY_ATTEMPTS - otpCode.getAttempts();
            if (remaining <= 0) {
                otpCode.setConsumed(true);
                otpCodeRepository.save(otpCode);
                throw new BadRequestException("Too many invalid verification attempts. Code has been invalidated.");
            }
            throw new BadRequestException("Invalid verification code. " + remaining + " attempts remaining.");
        }

        // Code matches successfully! Mark consumed
        otpCode.setConsumed(true);
        otpCodeRepository.save(otpCode);

        // Retrieve existing User
        User user = findUserByIdentifier(identifier)
            .orElseThrow(() -> new BadRequestException("No registered user found for this identifier."));

        if (!user.isEnabled()) {
            throw new UnauthorizedException("User account has been disabled. Please contact administrator.");
        }

        // Issue JWT token with same claims as password login
        String jwt = jwtUtils.generateTokenFromUsernameAndRole(user.getUsername(), user.getRole().name(), jwtUtils.getJwtExpirationMs());

        // Audit log successful OTP login
        auditLogService.logAction(
            "LOGIN_OTP",
            "USER",
            user.getId().toString(),
            null,
            "Method: OTP, Role: " + user.getRole() + ", Identifier: " + maskIdentifier(identifier),
            httpRequest
        );

        return new LoginResponse(jwt, jwtUtils.getJwtExpirationMs(), userMapper.toDto(user));
    }

    public boolean isDemoMode() {
        return "demo".equalsIgnoreCase(deliveryMode);
    }

    public String getDeliveryMode() {
        return deliveryMode;
    }

    private OtpDeliveryService resolveDeliveryService() {
        if ("smtp".equalsIgnoreCase(deliveryMode)) {
            return smtpDeliveryService;
        }
        return consoleDeliveryService;
    }

    private Optional<User> findUserByIdentifier(String identifier) {
        String clean = identifier.trim();
        return userRepository.findByEmail(clean)
            .or(() -> userRepository.findByPhoneNumber(clean))
            .or(() -> userRepository.findByUsername(clean));
    }

    private boolean isMobileNumber(String identifier) {
        if (identifier == null) return false;
        String trimmed = identifier.trim();
        return !trimmed.contains("@") && PHONE_PATTERN.matcher(trimmed).matches();
    }

    private String maskIdentifier(String identifier) {
        if (identifier == null) return "UNKNOWN";
        if (identifier.contains("@")) {
            String[] parts = identifier.split("@");
            return (parts[0].length() > 2 ? parts[0].substring(0, 2) + "***" : parts[0] + "***") + "@" + parts[1];
        }
        if (identifier.length() > 4) {
            return identifier.substring(0, 2) + "***" + identifier.substring(identifier.length() - 2);
        }
        return "***";
    }
}
