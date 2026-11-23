# EMAIL CONFIGURATION & NOTIFICATION SERVICE GUIDE
## RegTech Sentinel — Transactional Email & Token Delivery

This document explains how email delivery works in RegTech Sentinel for user verification, password resets, and alert notifications.

---

## 1. Dual-Mode Email Architecture

RegTech Sentinel features a **fail-safe dual-mode email subsystem**:
1. **Production Mode (Live SMTP):** Dispatches real transactional emails via standard SMTP providers (Gmail, SendGrid, Amazon SES, Brevo, Mailgun).
2. **Development / Fallback Mode (Console Logger):** If SMTP host or credentials are not configured or fail to connect, the system safely catches the exception and logs the verification or password reset link directly into the backend application console. This ensures developers and hackathon judges can complete the registration and reset flows without needing real SMTP credentials.

```
+-----------------------------------------------------------------------------------+
|                            EMAIL DISPATCH PIPELINE                                |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [EmailService.sendVerificationEmail(to, token)]                                  |
|        │                                                                          |
|        ├──► Generate URL: http://localhost:3000/verify-email?token=<token>        |
|        │                                                                          |
|        ├──► Is SMTP Host Configured?                                              |
|        │         │                                                                |
|        │         ├─── YES ───► [JavaMailSender.send(mimeMessage)]                 |
|        │         │                    │                                           |
|        │         │                    ├──► Success: Email Delivered               |
|        │         │                    └──► Failure: Catch exception & log link    |
|        │         │                                                                |
|        │         └─── NO  ───► [Development Fallback Logger]                      |
|        │                                 │                                        |
|        │                                 ▼                                        |
|        │              "================ EMAIL DISPATCH (DEV MODE) ================" |
|        │              "To: user@example.com"                                      |
|        │              "Subject: Verify your RegTech Sentinel Account"             |
|        │              "Link: http://localhost:3000/verify-email?token=xyz"        |
|        │              "===========================================================" |
|        ▼                                                                          |
|  [User or Evaluator clicks link from email or server log to complete flow]        |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 2. Configuration Parameters

The email subsystem is configured in `backend/src/main/resources/application.yml` and can be overridden via environment variables:

```yaml
spring:
  mail:
    host: ${SPRING_MAIL_HOST:localhost}
    port: ${SPRING_MAIL_PORT:587}
    username: ${SPRING_MAIL_USERNAME:}
    password: ${SPRING_MAIL_PASSWORD:}
    properties:
      mail:
        smtp:
          auth: ${SPRING_MAIL_SMTP_AUTH:false}
          starttls:
            enable: ${SPRING_MAIL_STARTTLS_ENABLE:false}
            required: ${SPRING_MAIL_STARTTLS_REQUIRED:false}
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
```

---

## 3. Production SMTP Provider Setup

### Option A: Gmail SMTP (Quick Setup)
1. Enable 2-Step Verification on your Google Account.
2. Generate an **App Password** (Security > 2-Step Verification > App Passwords).
3. Set the following variables in `backend/.env`:
   ```env
   SPRING_MAIL_HOST=smtp.gmail.com
   SPRING_MAIL_PORT=587
   SPRING_MAIL_USERNAME=your-email@gmail.com
   SPRING_MAIL_PASSWORD=your-16-character-app-password
   SPRING_MAIL_SMTP_AUTH=true
   SPRING_MAIL_STARTTLS_ENABLE=true
   SPRING_MAIL_STARTTLS_REQUIRED=true
   ```

### Option B: SendGrid
```env
SPRING_MAIL_HOST=smtp.sendgrid.net
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=apikey
SPRING_MAIL_PASSWORD=your-sendgrid-api-key
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_STARTTLS_ENABLE=true
SPRING_MAIL_STARTTLS_REQUIRED=true
```

### Option C: Amazon SES
```env
SPRING_MAIL_HOST=email-smtp.us-east-1.amazonaws.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-ses-smtp-username
SPRING_MAIL_PASSWORD=your-ses-smtp-password
SPRING_MAIL_SMTP_AUTH=true
SPRING_MAIL_STARTTLS_ENABLE=true
SPRING_MAIL_STARTTLS_REQUIRED=true
```

---

## 4. Email Templates & Expiration Policies

| Email Type | Trigger Event | Token Lifetime | Link Format |
| :--- | :--- | :---: | :--- |
| **Account Verification** | User registration via `/api/v1/auth/register` | **24 hours** | `http://localhost:3000/verify-email?token=<token>` |
| **Password Reset** | Forgot password request via `/api/v1/auth/forgot-password` | **1 hour** | `http://localhost:3000/reset-password?token=<token>` |
| **Account Unlock Notice** | Admin manually unlocks an account | N/A (Notice) | N/A |

### Verification Email Format
```
Subject: Verify your RegTech Sentinel Account

Dear [Full Name],

Thank you for registering on the RegTech Sentinel Enterprise Platform.

Please click the link below to verify your email address and activate your account:
http://localhost:3000/verify-email?token=8f3c7e62-4b21-4c91-9e8a-2184ef19a3bc

This link is valid for 24 hours. If you did not create an account, please disregard this email.

Best regards,
RegTech Sentinel Team
```

### Password Reset Email Format
```
Subject: Password Reset Request — RegTech Sentinel

Dear [Full Name],

We received a request to reset your password. Click the link below to set a new password:
http://localhost:3000/reset-password?token=a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d

This link is valid for 1 hour. If you did not request a password reset, your account is secure and no action is required.

Best regards,
RegTech Sentinel Security Team
```

---

## 5. Verifying Email in Development Mode

When running locally without SMTP credentials:
1. Register a new user at `http://localhost:3000/register`.
2. Inspect the backend terminal console.
3. You will see a banner containing the direct verification URL:
   ```
   ================================================================================
   [DEV MODE] EMAIL VERIFICATION TOKEN FOR user@example.com
   Link: http://localhost:3000/verify-email?token=...
   ================================================================================
   ```
4. Copy and paste the link into the browser or click it to instantly activate the account.
