# AUTHENTICATION & SECURITY SETUP GUIDE
## RegTech Sentinel — Production Authentication Architecture

This guide details the complete authentication, authorization, session management, and account lifecycle implementation for the RegTech Sentinel platform.

---

## 1. Authentication Architecture Overview

RegTech Sentinel uses an enterprise-grade hybrid authentication model:
- **Stateless JWT (JSON Web Tokens)** using HMAC-SHA256 (`HS256`).
- **Standard BCrypt Password Hashing** (12 salt rounds) for local credentials.
- **Google OAuth2 / OpenID Connect** for federated enterprise sign-in.
- **Role-Based Access Control (RBAC)** across 5 hierarchical roles.
- **Account Lockout Protection** preventing automated brute-force attacks.
- **Token-based Email Verification & Password Reset** workflows.
- **Sliding-Window Rate Limiting** on authentication endpoints.

```
+-----------------------------------------------------------------------------------+
|                           AUTHENTICATION FLOW PIPELINE                            |
+-----------------------------------------------------------------------------------+
|                                                                                   |
|  [Client Request]                                                                 |
|        │                                                                          |
|        ▼                                                                          |
|  [RateLimitingFilter] ────(>120 req/min)────► [HTTP 429 Too Many Requests]        |
|        │                                                                          |
|        ▼                                                                          |
|  [SecurityFilterChain (Spring Security 6)]                                        |
|        │                                                                          |
|        ├──► Public Endpoint? (/api/v1/auth/**, /actuator/health) ──► Allow        |
|        │                                                                          |
|        ▼                                                                          |
|  [JwtAuthenticationFilter]                                                        |
|        │                                                                          |
|        ├──► Header: "Authorization: Bearer <token>"                               |
|        ├──► Validate HMAC-SHA256 signature with JWT_SECRET                         |
|        ├──► Check token expiration (24h)                                          |
|        ├──► Load UserDetails & authorities                                        |
|        └──► Set SecurityContextHolder.getContext().setAuthentication(auth)        |
|        │                                                                          |
|        ▼                                                                          |
|  [@PreAuthorize Role Check] ──(Forbidden Role)──► [HTTP 403 Forbidden]            |
|        │                                                                          |
|        ▼                                                                          |
|  [Controller / Business Service Execution]                                        |
|                                                                                   |
+-----------------------------------------------------------------------------------+
```

---

## 2. Seeded Demo Accounts

The database comes pre-seeded via Flyway migration `V6__seed_demo_users.sql` with four active, email-verified demo accounts:

| Username | Plaintext Password | Primary Role | Assigned Department | Email Address | Account Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| `regulator` | `Regulator@123` | `ROLE_REGULATOR` | Legal Metrology Dept | `regulator@regtech.gov.in` | `ACTIVE` |
| `admin` | `Admin@123` | `ROLE_ADMIN` | Systems Administration | `admin@regtech.gov.in` | `ACTIVE` |
| `analyst` | `Analyst@123` | `ROLE_ANALYST` | Freight Intelligence | `analyst@regtech.gov.in` | `ACTIVE` |
| `reviewer` | `Reviewer@123` | `ROLE_REVIEWER` | Compliance Review | `reviewer@regtech.gov.in` | `ACTIVE` |

> [!IMPORTANT]
> The seeded passwords use standard BCrypt hash representations:
> - `$2a$12$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy` (matches `Admin@123`, `Regulator@123`, etc.).
> - Plaintext passwords are never stored in the database.

---

## 3. Role-Based Access Control (RBAC) Matrix

The system enforces 5 distinct roles:

| Role Name | Authority String | Description & Access Scope |
| :--- | :--- | :--- |
| `VIEWER` | `ROLE_VIEWER` | Default role for self-registered users. Read-only access to public dashboards and freight benchmarks. Cannot trigger new scans or optimize voyages. |
| `REVIEWER` | `ROLE_REVIEWER` | Can inspect compliance scans, view statutory violations, and export reports. |
| `ANALYST` | `ROLE_ANALYST` | Full access to SIH26006 Maritime Freight Intelligence: run Holt-Winters forecasts, optimize vessel chartering, run bulk procurement landed cost models, and download reports. |
| `REGULATOR` | `ROLE_REGULATOR` | Legal Metrology compliance authority. Can trigger OCR scans, issue compliance notices, and modify packaging rule criteria. |
| `ADMIN` | `ROLE_ADMIN` | Complete administrative authority: user management, role promotion (`PATCH /api/v1/users/{id}/role`), account unlocking, and system configuration. |

---

## 4. Account Lockout & Brute-Force Defense

To prevent dictionary and credential stuffing attacks:
1. Each failed login attempt increments the user's `failed_login_attempts` counter.
2. Upon reaching **5 consecutive failed attempts**:
   - `account_status` is transitioned to `LOCKED`.
   - `locked_until` is set to `LocalDateTime.now().plusMinutes(15)`.
3. During the 15-minute lockout window, all login attempts for that username return HTTP 401 with:
   ```json
   {
     "success": false,
     "message": "Account is temporarily locked. Please try again later or reset your password.",
     "data": null
   }
   ```
4. An `ADMIN` user can immediately unlock an account at any time via:
   `POST /api/v1/users/{id}/unlock`
5. A successful login automatically resets `failed_login_attempts` to 0 and clears `locked_until`.

---

## 5. User Registration & Email Verification Workflow

1. **User Registration:**
   - POST payload to `/api/v1/auth/register`:
     ```json
     {
       "username": "john_doe",
       "email": "john@example.com",
       "password": "Password@123",
       "fullName": "John Doe",
       "department": "Procurement"
     }
     ```
   - User is created with `role = ROLE_VIEWER`, `email_verified = FALSE`, and `account_status = PENDING_VERIFICATION`.
2. **Token Issuance:**
   - A cryptographically secure 256-bit random UUID token is generated and persisted in `security_tokens` with `token_type = EMAIL_VERIFICATION` and a 24-hour expiration.
   - An email is dispatched with the verification link:
     `http://localhost:3000/verify-email?token=<token>`
   - *Development Fallback:* If SMTP is unconfigured, the verification token is logged directly to the backend application logs.
3. **Verification Confirmation:**
   - User visits the link, triggering `POST /api/v1/auth/verify-email?token=<token>`.
   - Backend marks `email_verified = TRUE`, `account_status = ACTIVE`, and invalidates the token.

---

## 6. Password Reset Workflow

1. **Forgot Password Request:**
   - POST payload to `/api/v1/auth/forgot-password`:
     ```json
     { "email": "john@example.com" }
     ```
   - If the account exists, a one-time token (`token_type = PASSWORD_RESET`, 1-hour expiration) is generated and emailed to the user.
2. **Password Reset Execution:**
   - User visits `http://localhost:3000/reset-password?token=<token>`.
   - Submits `POST /api/v1/auth/reset-password`:
     ```json
     {
       "token": "<token>",
       "newPassword": "NewSecurePassword@123"
     }
     ```
   - Backend hashes the new password with BCrypt, updates the database, clears lockout status, and deletes the token.

---

## 7. Rate Limiting Specification

The `RateLimitingFilter` applies token-bucket rate limiting based on client IP:
- **Maximum Limit:** 120 requests per minute per IP address.
- **Window Type:** Sliding 60-second window.
- **Violation Response:** HTTP 429 Too Many Requests with header `Retry-After: 60`.
- **Exclusion:** Automated tests passing header `X-Test-Bypass-Rate-Limit: true` under the `test` profile are exempt.
