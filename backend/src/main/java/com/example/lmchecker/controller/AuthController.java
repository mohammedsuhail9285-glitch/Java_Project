package com.example.lmchecker.controller;

import com.example.lmchecker.dto.*;
import com.example.lmchecker.security.DynamicClientRegistrationRepository;
import com.example.lmchecker.service.AuthService;
import com.example.lmchecker.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping({"/api/v1/auth", "/api/auth"})
@Tag(name = "Authentication & Account Management", description = "Endpoints for login, registration, Google OAuth, email verification, password reset, and profile management")
public class AuthController {

    private final AuthService authService;
    private final OtpService otpService;
    private final DynamicClientRegistrationRepository clientRegistrationRepository;
    private final boolean cookieSecure;

    public AuthController(
            AuthService authService,
            OtpService otpService,
            DynamicClientRegistrationRepository clientRegistrationRepository,
            @Value("${app.security.cookie-secure:false}") boolean cookieSecure) {
        this.authService = authService;
        this.otpService = otpService;
        this.clientRegistrationRepository = clientRegistrationRepository;
        this.cookieSecure = cookieSecure;
    }

    @Operation(summary = "Get authentication configuration and enabled providers")
    @GetMapping("/config")
    public ResponseEntity<ApiResponse<AuthConfigResponse>> getAuthConfig() {
        boolean googleEnabled = clientRegistrationRepository.isGoogleConfigured();
        String otpMode = otpService.getDeliveryMode();
        boolean demoMode = otpService.isDemoMode();

        AuthConfigResponse config = new AuthConfigResponse(googleEnabled, otpMode, demoMode);
        return ResponseEntity.ok(ApiResponse.ok("Auth configuration retrieved", config));
    }

    @Operation(summary = "Traditional username/email and password authentication")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest loginRequest,
            HttpServletRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.login(loginRequest, request);
        setAccessTokenCookie(response, loginResponse.getAccessToken());
        return ResponseEntity.ok(ApiResponse.ok("Login successful", loginResponse));
    }

    @Operation(summary = "New user account registration")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserDto>> register(
            @Valid @RequestBody RegisterRequest registerRequest,
            HttpServletRequest request) {
        UserDto userDto = authService.register(registerRequest, request);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful. Please check your email to verify your account.", userDto));
    }

    @Operation(summary = "Verify account email address using verification token")
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyEmailPost(
            @RequestParam String token,
            HttpServletRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.verifyEmail(token, request);
        setAccessTokenCookie(response, loginResponse.getAccessToken());
        return ResponseEntity.ok(ApiResponse.ok("Email address successfully verified.", loginResponse));
    }

    @Operation(summary = "Verify account email address via GET link")
    @GetMapping("/verify-email")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyEmailGet(
            @RequestParam String token,
            HttpServletRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = authService.verifyEmail(token, request);
        setAccessTokenCookie(response, loginResponse.getAccessToken());
        return ResponseEntity.ok(ApiResponse.ok("Email address successfully verified.", loginResponse));
    }

    @Operation(summary = "Resend email verification link")
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<String>> resendVerification(@RequestParam String email) {
        String message = authService.resendVerificationEmail(email);
        return ResponseEntity.ok(ApiResponse.ok(message, message));
    }

    @Operation(summary = "Request password reset link via registered email")
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String message = authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.ok(message, message));
    }

    @Operation(summary = "Reset account password using single-use reset token")
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request,
            HttpServletRequest httpRequest) {
        String message = authService.resetPassword(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok(message, message));
    }

    @Operation(summary = "Change password for authenticated user")
    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            HttpServletRequest httpRequest) {
        String message = authService.changePassword(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok(message, message));
    }

    @Operation(summary = "Update user profile details")
    @PatchMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            HttpServletRequest httpRequest) {
        UserDto updated = authService.updateProfile(request, httpRequest);
        return ResponseEntity.ok(ApiResponse.ok("Profile updated successfully", updated));
    }

    @Operation(summary = "Retrieve currently authenticated user profile")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser() {
        UserDto currentUser = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.ok("Current user retrieved", currentUser));
    }

    @Operation(summary = "Refresh JWT access token using token or cookie")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(
            @RequestParam(required = false) String refreshToken,
            @CookieValue(name = "accessToken", required = false) String cookieToken,
            HttpServletResponse response) {
        String tokenToUse = refreshToken != null ? refreshToken : cookieToken;
        LoginResponse refreshResponse = authService.refreshToken(tokenToUse);
        setAccessTokenCookie(response, refreshResponse.getAccessToken());
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", refreshResponse));
    }

    @Operation(summary = "Sign out and clear session cookies")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletResponse response) {
        clearAccessTokenCookie(response);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", "SESSION_CLEARED"));
    }

    @Operation(summary = "Request a one-time password (OTP) via email or mobile")
    @PostMapping("/otp/request")
    public ResponseEntity<ApiResponse<OtpRequestResponse>> requestOtp(
            @Valid @RequestBody OtpRequestDto requestDto,
            HttpServletRequest request) {
        OtpRequestResponse otpResponse = otpService.requestOtp(requestDto, request);
        return ResponseEntity.ok(ApiResponse.ok("OTP request processed", otpResponse));
    }

    @Operation(summary = "Verify OTP code and complete login")
    @PostMapping("/otp/verify")
    public ResponseEntity<ApiResponse<LoginResponse>> verifyOtp(
            @Valid @RequestBody OtpVerifyDto verifyDto,
            HttpServletRequest request,
            HttpServletResponse response) {
        LoginResponse loginResponse = otpService.verifyOtp(verifyDto, request);
        setAccessTokenCookie(response, loginResponse.getAccessToken());
        return ResponseEntity.ok(ApiResponse.ok("OTP verification successful", loginResponse));
    }

    private void setAccessTokenCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", token)
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(86400) // 1 day
            .sameSite("Lax")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void clearAccessTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("accessToken", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .path("/")
            .maxAge(0)
            .sameSite("Lax")
            .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
