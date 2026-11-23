package com.example.lmchecker.service.otp;

import com.example.lmchecker.dto.OtpDeliveryResult;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("smtpOtpDeliveryService")
public class SmtpOtpDeliveryService implements OtpDeliveryService {

    private static final Logger log = LoggerFactory.getLogger(SmtpOtpDeliveryService.class);

    private final Optional<JavaMailSender> mailSender;

    @Value("${spring.mail.username:no-reply@consumer.gov.in}")
    private String senderEmail;

    @Autowired
    public SmtpOtpDeliveryService(Optional<JavaMailSender> mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public OtpDeliveryResult sendOtp(String identifier, String rawCode, int expiryMinutes) {
        if (mailSender.isEmpty()) {
            log.error("JavaMailSender is not configured in this environment.");
            return OtpDeliveryResult.failure("Email service is currently unconfigured. Set MAIL_HOST, MAIL_PORT, MAIL_USERNAME, and MAIL_PASSWORD.");
        }

        try {
            JavaMailSender sender = mailSender.get();
            MimeMessage message = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(senderEmail, "RegTech Sentinel Security");
            helper.setTo(identifier);
            helper.setSubject("RegTech Sentinel: Your Login Verification Code (" + rawCode + ")");
            helper.setText(buildHtmlEmail(rawCode, expiryMinutes), true);

            sender.send(message);
            log.info("Successfully dispatched OTP email to {}", maskEmail(identifier));
            return OtpDeliveryResult.success("Verification code sent to " + maskEmail(identifier), null);
        } catch (Exception e) {
            log.error("Failed to send OTP email to {}: {}", maskEmail(identifier), e.getMessage());
            return OtpDeliveryResult.failure("Failed to send email OTP: " + e.getMessage());
        }
    }

    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        String name = parts[0];
        String visible = name.length() > 2 ? name.substring(0, 2) + "***" : name + "***";
        return visible + "@" + parts[1];
    }

    private String buildHtmlEmail(String code, int expiryMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <style>
                body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f1f5f9; margin: 0; padding: 24px; }
                .container { max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 12px; border: 1px solid #e2e8f0; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.05); }
                .header { background: #0f172a; padding: 24px; text-align: center; color: #ffffff; }
                .header h1 { margin: 0; font-size: 20px; font-weight: 700; letter-spacing: 0.5px; }
                .header p { margin: 4px 0 0; font-size: 12px; color: #94a3b8; }
                .content { padding: 32px 24px; text-align: center; }
                .code-box { display: inline-block; background: #f8fafc; border: 2px dashed #0284c7; border-radius: 8px; padding: 16px 32px; font-size: 32px; font-weight: 800; letter-spacing: 8px; color: #0369a1; margin: 20px 0; }
                .expiry { font-size: 13px; color: #64748b; margin-bottom: 24px; }
                .warning { font-size: 12px; color: #b91c1c; background: #fef2f2; border: 1px solid #fecaca; border-radius: 6px; padding: 12px; text-align: left; }
                .footer { background: #f8fafc; padding: 16px; text-align: center; font-size: 11px; color: #94a3b8; border-top: 1px solid #e2e8f0; }
              </style>
            </head>
            <body>
              <div class="container">
                <div class="header">
                  <h1>RegTech Sentinel</h1>
                  <p>Department of Consumer Affairs • Legal Metrology Portal</p>
                </div>
                <div class="content">
                  <p style="font-size: 15px; color: #334155; margin: 0;">Use the following one-time password (OTP) to complete your sign-in:</p>
                  <div class="code-box">""" + code + """
                  </div>
                  <p class="expiry">This code will expire in <strong>""" + expiryMinutes + """
                   minutes</strong> and can only be used once.</p>
                  <div class="warning">
                    <strong>Security Notice:</strong> If you did not request this verification code, please ignore this email. Do not share this code with anyone under any circumstances.
                  </div>
                </div>
                <div class="footer">
                  This is an automated system notification from RegTech Sentinel Decision Support System.
                </div>
              </div>
            </body>
            </html>
            """;
    }

    @Override
    public String getMode() {
        return "smtp";
    }
}
