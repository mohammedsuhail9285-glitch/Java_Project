package com.example.lmchecker.service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountNotificationService {

    private static final Logger log = LoggerFactory.getLogger(AccountNotificationService.class);

    private final Optional<JavaMailSender> mailSender;
    private final String senderEmail;
    private final String frontendUrl;

    @Autowired
    public AccountNotificationService(
            Optional<JavaMailSender> mailSender,
            @Value("${spring.mail.username:no-reply@regtechsentinel.gov.in}") String senderEmail,
            @Value("${app.frontend.url:http://localhost:3000}") String frontendUrl) {
        this.mailSender = mailSender;
        this.senderEmail = senderEmail;
        this.frontendUrl = frontendUrl;
    }

    public void sendVerificationEmail(String recipientEmail, String fullName, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        log.info("[EMAIL SERVICE] Dispatching Verification Email to '{}': URL={}", recipientEmail, verificationUrl);

        if (mailSender.isEmpty()) {
            log.info("[DEV EMAIL] JavaMailSender unconfigured. Verification Link: {}", verificationUrl);
            return;
        }

        try {
            JavaMailSender sender = mailSender.get();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, "RegTech Sentinel Security");
            helper.setTo(recipientEmail);
            helper.setSubject("RegTech Sentinel: Verify Your Account Email");
            helper.setText(buildVerificationEmailHtml(fullName, verificationUrl), true);

            sender.send(message);
            log.info("Successfully sent verification email to {}", recipientEmail);
        } catch (Exception e) {
            log.warn("Could not dispatch SMTP verification email to {}: {}. Verification URL was: {}",
                    recipientEmail, e.getMessage(), verificationUrl);
        }
    }

    public void sendPasswordResetEmail(String recipientEmail, String fullName, String token) {
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        log.info("[EMAIL SERVICE] Dispatching Password Reset Email to '{}': URL={}", recipientEmail, resetUrl);

        if (mailSender.isEmpty()) {
            log.info("[DEV EMAIL] JavaMailSender unconfigured. Reset Password Link: {}", resetUrl);
            return;
        }

        try {
            JavaMailSender sender = mailSender.get();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, "RegTech Sentinel Security");
            helper.setTo(recipientEmail);
            helper.setSubject("RegTech Sentinel: Password Reset Instructions");
            helper.setText(buildPasswordResetEmailHtml(fullName, resetUrl), true);

            sender.send(message);
            log.info("Successfully sent password reset email to {}", recipientEmail);
        } catch (Exception e) {
            log.warn("Could not dispatch SMTP password reset email to {}: {}. Reset URL was: {}",
                    recipientEmail, e.getMessage(), resetUrl);
        }
    }

    private String buildVerificationEmailHtml(String name, String verificationUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f8fafc; padding: 24px; color: #1e293b;">
              <div style="max-width: 560px; margin: 0 auto; background: #ffffff; border-radius: 8px; border: 1px solid #e2e8f0; overflow: hidden;">
                <div style="background: #0f172a; padding: 20px 24px; color: #ffffff;">
                  <h2 style="margin: 0; font-size: 20px;">RegTech Sentinel</h2>
                  <p style="margin: 4px 0 0; font-size: 13px; color: #94a3b8;">Unified Maritime Freight & Legal Metrology Platform</p>
                </div>
                <div style="padding: 24px;">
                  <p style="font-size: 15px;">Hello <strong>%s</strong>,</p>
                  <p style="font-size: 14px; line-height: 1.6;">Thank you for registering on the RegTech Sentinel Platform. Please verify your email address to complete your registration and activate your account access.</p>
                  <div style="margin: 28px 0; text-align: center;">
                    <a href="%s" style="background: #1e40af; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 14px; display: inline-block;">Verify Email Address</a>
                  </div>
                  <p style="font-size: 12px; color: #64748b; line-height: 1.5;">This link will expire in 24 hours. If you did not create an account on RegTech Sentinel, please ignore this email.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name != null ? name : "User", verificationUrl);
    }

    private String buildPasswordResetEmailHtml(String name, String resetUrl) {
        return """
            <!DOCTYPE html>
            <html>
            <body style="font-family: Arial, sans-serif; background-color: #f8fafc; padding: 24px; color: #1e293b;">
              <div style="max-width: 560px; margin: 0 auto; background: #ffffff; border-radius: 8px; border: 1px solid #e2e8f0; overflow: hidden;">
                <div style="background: #0f172a; padding: 20px 24px; color: #ffffff;">
                  <h2 style="margin: 0; font-size: 20px;">RegTech Sentinel</h2>
                  <p style="margin: 4px 0 0; font-size: 13px; color: #94a3b8;">Security & Access Control</p>
                </div>
                <div style="padding: 24px;">
                  <p style="font-size: 15px;">Hello <strong>%s</strong>,</p>
                  <p style="font-size: 14px; line-height: 1.6;">We received a request to reset your password for your RegTech Sentinel account. Click the button below to choose a new password:</p>
                  <div style="margin: 28px 0; text-align: center;">
                    <a href="%s" style="background: #dc2626; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 6px; font-weight: bold; font-size: 14px; display: inline-block;">Reset Password</a>
                  </div>
                  <p style="font-size: 12px; color: #64748b; line-height: 1.5;">This password reset link is single-use and will expire in 1 hour. If you did not request a password reset, you can safely ignore this email.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(name != null ? name : "User", resetUrl);
    }
}
