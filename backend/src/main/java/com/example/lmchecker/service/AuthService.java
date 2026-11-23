package com.example.lmchecker.service;

import com.example.lmchecker.audit.AuditLogService;
import com.example.lmchecker.dto.*;
import com.example.lmchecker.entity.*;
import com.example.lmchecker.exception.BadRequestException;
import com.example.lmchecker.exception.UnauthorizedException;
import com.example.lmchecker.mapper.UserMapper;
import com.example.lmchecker.repository.SecurityTokenRepository;
import com.example.lmchecker.repository.UserRepository;
import com.example.lmchecker.security.JwtUtils;
import com.example.lmchecker.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_MINUTES = 15;

    // Minimum 8 characters, at least 1 uppercase, 1 lowercase, 1 digit, 1 special character
    private static final Pattern PASSWORD_STRENGTH_PATTERN =
            Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?]).{8,100}$");

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final SecurityTokenRepository securityTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuditLogService auditLogService;
    private final AccountNotificationService notificationService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtUtils jwtUtils,
            UserRepository userRepository,
            SecurityTokenRepository securityTokenRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            AuditLogService auditLogService,
            AccountNotificationService notificationService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.securityTokenRepository = securityTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.auditLogService = auditLogService;
        this.notificationService = notificationService;
    }

    @Transactional(noRollbackFor = {UnauthorizedException.class, BadCredentialsException.class})
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String identifier = request.getUsername() != null ? request.getUsername().trim() : "";

        // Check user existence for lockout inspection
        Optional<User> optionalUser = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier));

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            if (!user.isAccountNonLocked()) {
                long minutesRemaining = Duration.between(LocalDateTime.now(), user.getLockedUntil()).toMinutes() + 1;
                throw new UnauthorizedException("Account is temporarily locked due to multiple failed login attempts. Please try again in " + minutesRemaining + " minutes.");
            }
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(identifier, request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            User user = userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new UnauthorizedException("User record not found"));

            // Successful login: reset failed counter and lock
            if (user.getFailedLoginAttempts() > 0 || user.getLockedUntil() != null || "LOCKED".equalsIgnoreCase(user.getAccountStatus())) {
                user.setFailedLoginAttempts(0);
                user.setLockedUntil(null);
                user.setAccountStatus("ACTIVE");
                userRepository.save(user);
            }

            UserDto userDto = userMapper.toDto(user);
            auditLogService.logAction("LOGIN", "USER", user.getId().toString(), null, "Role: " + user.getRole(), httpRequest);

            return new LoginResponse(jwt, jwtUtils.getJwtExpirationMs(), userDto);
        } catch (BadCredentialsException e) {
            // Rate limiting and lockout accounting
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                int attempts = user.getFailedLoginAttempts() + 1;
                user.setFailedLoginAttempts(attempts);

                if (attempts >= MAX_FAILED_ATTEMPTS) {
                    user.setLockedUntil(LocalDateTime.now().plusMinutes(LOCKOUT_MINUTES));
                    user.setAccountStatus("LOCKED");
                    userRepository.saveAndFlush(user);
                    log.warn("User '{}' locked out for {} minutes after {} failed attempts", user.getUsername(), LOCKOUT_MINUTES, attempts);
                    throw new UnauthorizedException("Account locked for " + LOCKOUT_MINUTES + " minutes due to " + MAX_FAILED_ATTEMPTS + " consecutive failed attempts.");
                } else {
                    userRepository.saveAndFlush(user);
                    int remaining = MAX_FAILED_ATTEMPTS - attempts;
                    throw new UnauthorizedException("Invalid username or password. " + remaining + " attempts remaining before account lockout.");
                }
            }
            throw new UnauthorizedException("Invalid username/email or password.");
        } catch (LockedException e) {
            throw new UnauthorizedException("Account is temporarily locked due to multiple failed login attempts.");
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            log.error("Authentication failed: {}", e.getMessage());
            throw new UnauthorizedException("Invalid username/email or password.");
        }
    }

    @Transactional
    public UserDto register(RegisterRequest request, HttpServletRequest httpRequest) {
        String fullName = request.getFullName() != null ? request.getFullName().trim() : "";
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        String password = request.getPassword() != null ? request.getPassword() : "";
        String confirmPassword = request.getConfirmPassword() != null ? request.getConfirmPassword() : "";

        if (fullName.isEmpty()) {
            throw new BadRequestException("Full name is required.");
        }
        if (email.isEmpty()) {
            throw new BadRequestException("Email address is required.");
        }
        if (!request.isAcceptTerms()) {
            throw new BadRequestException("You must accept the terms of service and privacy policy to register.");
        }
        if (!password.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match.");
        }
        if (!PASSWORD_STRENGTH_PATTERN.matcher(password).matches()) {
            throw new BadRequestException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with this email address is already registered.");
        }

        // Generate sanitized unique username based on email
        String baseUsername = email.split("@")[0].replaceAll("[^a-zA-Z0-9_]", "_").toLowerCase();
        String username = baseUsername;
        int counter = 1;
        while (userRepository.existsByUsername(username)) {
            username = baseUsername + counter++;
        }

        // Public registration: strictly restricted role VIEWER
        User user = User.builder()
                .username(username)
                .email(email)
                .fullName(fullName)
                .passwordHash(passwordEncoder.encode(password))
                .role(Role.VIEWER)
                .authProvider(AuthProvider.LOCAL)
                .emailVerified(false)
                .accountStatus("ACTIVE")
                .failedLoginAttempts(0)
                .enabled(true)
                .build();

        user = userRepository.save(user);

        // Generate secure 24-hour single-use email verification token
        String tokenStr = UUID.randomUUID().toString();
        SecurityToken token = new SecurityToken(tokenStr, user, TokenType.EMAIL_VERIFICATION, LocalDateTime.now().plusHours(24));
        securityTokenRepository.save(token);

        notificationService.sendVerificationEmail(user.getEmail(), user.getFullName(), tokenStr);
        auditLogService.logAction("REGISTER_PUBLIC", "USER", user.getId().toString(), null, "Role: VIEWER, Email: " + user.getEmail(), httpRequest);

        log.info("Public user registered: username={}, email={}, role=VIEWER", user.getUsername(), user.getEmail());
        return userMapper.toDto(user);
    }

    @Transactional
    public LoginResponse verifyEmail(String tokenStr, HttpServletRequest httpRequest) {
        if (tokenStr == null || tokenStr.trim().isEmpty()) {
            throw new BadRequestException("Verification token is required.");
        }

        SecurityToken token = securityTokenRepository.findByTokenAndTokenType(tokenStr.trim(), TokenType.EMAIL_VERIFICATION)
                .orElseThrow(() -> new BadRequestException("Verification link is invalid or has already been used."));

        if (!token.isValid()) {
            throw new BadRequestException("Verification link is expired or has already been used. Please request a new one.");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        user.setAccountStatus("ACTIVE");
        userRepository.save(user);

        token.setConsumed(true);
        securityTokenRepository.save(token);

        auditLogService.logAction("VERIFY_EMAIL", "USER", user.getId().toString(), null, "Email verified: " + user.getEmail(), httpRequest);
        log.info("Email verified successfully for user '{}'", user.getUsername());

        // Issue JWT token so user is automatically authenticated upon verification
        String jwt = jwtUtils.generateTokenFromUsernameAndRole(user.getUsername(), user.getRole().name(), jwtUtils.getJwtExpirationMs());
        return new LoginResponse(jwt, jwtUtils.getJwtExpirationMs(), userMapper.toDto(user));
    }

    @Transactional
    public String resendVerificationEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new BadRequestException("Email address is required.");
        }
        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmail(normalizedEmail);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (!user.isEmailVerified()) {
                securityTokenRepository.invalidateTokensForUser(user, TokenType.EMAIL_VERIFICATION);
                String tokenStr = UUID.randomUUID().toString();
                SecurityToken token = new SecurityToken(tokenStr, user, TokenType.EMAIL_VERIFICATION, LocalDateTime.now().plusHours(24));
                securityTokenRepository.save(token);
                notificationService.sendVerificationEmail(user.getEmail(), user.getFullName(), tokenStr);
                log.info("Resent verification email to '{}'", user.getEmail());
            }
        }

        // Generic response prevents email enumeration
        return "If an unverified account associated with this email exists, a verification link has been dispatched.";
    }

    @Transactional
    public String forgotPassword(ForgotPasswordRequest request) {
        String email = request.getEmail() != null ? request.getEmail().trim().toLowerCase() : "";
        if (email.isEmpty()) {
            throw new BadRequestException("Email address is required.");
        }

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            securityTokenRepository.invalidateTokensForUser(user, TokenType.PASSWORD_RESET);
            String tokenStr = UUID.randomUUID().toString();
            SecurityToken token = new SecurityToken(tokenStr, user, TokenType.PASSWORD_RESET, LocalDateTime.now().plusHours(1));
            securityTokenRepository.save(token);
            notificationService.sendPasswordResetEmail(user.getEmail(), user.getFullName(), tokenStr);
            log.info("Generated password reset token for '{}'", user.getEmail());
        }

        // Generic response prevents email enumeration
        return "If an account associated with this email exists, password reset instructions have been dispatched.";
    }

    @Transactional
    public String resetPassword(ResetPasswordRequest request, HttpServletRequest httpRequest) {
        String tokenStr = request.getToken() != null ? request.getToken().trim() : "";
        String newPassword = request.getNewPassword() != null ? request.getNewPassword() : "";
        String confirmPassword = request.getConfirmPassword() != null ? request.getConfirmPassword() : "";

        if (tokenStr.isEmpty()) {
            throw new BadRequestException("Reset token is required.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("Passwords do not match.");
        }
        if (!PASSWORD_STRENGTH_PATTERN.matcher(newPassword).matches()) {
            throw new BadRequestException("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }

        SecurityToken token = securityTokenRepository.findByTokenAndTokenType(tokenStr, TokenType.PASSWORD_RESET)
                .orElseThrow(() -> new BadRequestException("Password reset link is invalid or has already been used."));

        if (!token.isValid()) {
            throw new BadRequestException("Password reset link has expired or has already been used. Please request a new one.");
        }

        User user = token.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        user.setEmailVerified(true); // Password reset via email validates email ownership
        userRepository.save(user);

        token.setConsumed(true);
        securityTokenRepository.save(token);

        auditLogService.logAction("RESET_PASSWORD", "USER", user.getId().toString(), null, "Password reset via token", httpRequest);
        log.info("Password successfully reset for user '{}'", user.getUsername());

        return "Password has been successfully updated. You may now log in with your new credentials.";
    }

    @Transactional
    public String changePassword(ChangePasswordRequest request, HttpServletRequest httpRequest) {
        User user = getCurrentAuthenticatedUser();
        String currentPassword = request.getCurrentPassword() != null ? request.getCurrentPassword() : "";
        String newPassword = request.getNewPassword() != null ? request.getNewPassword() : "";
        String confirmPassword = request.getConfirmPassword() != null ? request.getConfirmPassword() : "";

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new BadRequestException("Current password does not match records.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new BadRequestException("New password and confirmation do not match.");
        }
        if (!PASSWORD_STRENGTH_PATTERN.matcher(newPassword).matches()) {
            throw new BadRequestException("New password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        auditLogService.logAction("CHANGE_PASSWORD", "USER", user.getId().toString(), null, "Self password change", httpRequest);
        return "Password successfully updated.";
    }

    @Transactional
    public UserDto updateProfile(UpdateProfileRequest request, HttpServletRequest httpRequest) {
        User user = getCurrentAuthenticatedUser();
        if (request.getFullName() != null && !request.getFullName().trim().isEmpty()) {
            user.setFullName(request.getFullName().trim());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber().trim());
        }
        user = userRepository.save(user);

        auditLogService.logAction("UPDATE_PROFILE", "USER", user.getId().toString(), null, "Profile updated", httpRequest);
        return userMapper.toDto(user);
    }

    public LoginResponse refreshToken(String refreshToken) {
        if (refreshToken == null || !jwtUtils.validateJwtToken(refreshToken)) {
            throw new BadRequestException("Invalid or expired refresh token");
        }

        String username = jwtUtils.getUserNameFromJwtToken(refreshToken);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UnauthorizedException("User not found"));

        String newJwt = jwtUtils.generateTokenFromUsernameAndRole(user.getUsername(), user.getRole().name(), jwtUtils.getJwtExpirationMs());
        return new LoginResponse(newJwt, jwtUtils.getJwtExpirationMs(), userMapper.toDto(user));
    }

    public UserDto getCurrentUser() {
        return userMapper.toDto(getCurrentAuthenticatedUser());
    }

    private User getCurrentAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserPrincipal principal)) {
            throw new UnauthorizedException("User session is not authenticated.");
        }

        return userRepository.findById(principal.getId())
            .orElseThrow(() -> new UnauthorizedException("Authenticated user record not found."));
    }
}
