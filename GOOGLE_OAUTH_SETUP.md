# GOOGLE OAUTH2 & OPENID CONNECT SETUP GUIDE
## RegTech Sentinel — Federated Authentication Configuration

This document provides step-by-step instructions for configuring and testing Google OAuth2 / OpenID Connect authentication in RegTech Sentinel.

---

## 1. Overview & Architecture

RegTech Sentinel supports federated Google sign-in using Spring Boot's OAuth2 Client integration and OpenID Connect (`OIDC`).

```
+-------------+              +-------------------------+             +--------------------+
| React Client|              |   Spring Boot Backend   |             |    Google OAuth2   |
+-------------+              +-------------------------+             +--------------------+
       │                                  │                                    │
       │ 1. Click "Continue with Google"  │                                    │
       ├─────────────────────────────────►│                                    │
       │                                  │ 2. Redirect to Google Auth URL     │
       │                                  ├───────────────────────────────────►│
       │                                  │                                    │
       │ 3. User authenticates on Google  │                                    │
       │◄──────────────────────────────────────────────────────────────────────┤
       │                                  │                                    │
       │ 4. Google redirects with code    │                                    │
       ├─────────────────────────────────►│ 5. Authorization code callback     │
       │                                  ├───────────────────────────────────►│
       │                                  │ 6. Exchange code for ID Token      │
       │                                  │◄───────────────────────────────────┤
       │                                  │                                    │
       │                                  │ 7. Verify email & auto-provision   │
       │                                  │    or link existing user           │
       │                                  │ 8. Issue RegTech JWT Token         │
       │ 9. Redirect to frontend with JWT │                                    │
       │◄─────────────────────────────────┤                                    │
       │                                  │                                    │
```

### Key Security Safeguards
- **Client Secret Protection:** The `GOOGLE_CLIENT_SECRET` is stored strictly on the backend and is never exposed to the frontend bundle or client network traffic.
- **Backend Email Verification:** Frontend-provided identity claims or roles are never trusted. The backend directly exchanges the authorization code with Google's token endpoint over TLS and validates the verified email claim (`email_verified: true`).
- **Account Linking Safety:** An existing local account is linked to Google only if the email matches an already verified local account.
- **Role Assignment:** First-time Google registrations are assigned `ROLE_VIEWER` by default to prevent privilege escalation.

---

## 2. Google Cloud Console Configuration

Follow these steps to obtain genuine Google OAuth credentials:

### Step 1: Create a Project
1. Open the [Google Cloud Console](https://console.cloud.google.com/).
2. Click the project dropdown and select **New Project**.
3. Set the project name (e.g., `RegTech-Sentinel-Auth`) and click **Create**.

### Step 2: Configure OAuth Consent Screen
1. In the left navigation, go to **APIs & Services** > **OAuth consent screen**.
2. Select **External** user type and click **Create**.
3. Fill in the required application information:
   - **App name:** `RegTech Sentinel`
   - **User support email:** Your email address
   - **Developer contact information:** Your email address
4. Click **Save and Continue**.
5. On the **Scopes** page, click **Add or Remove Scopes** and select:
   - `.../auth/userinfo.email`
   - `.../auth/userinfo.profile`
   - `openid`
6. Click **Save and Continue**.
7. Under **Test users**, add the email addresses that will test the login during hackathon evaluation.
8. Click **Save and Continue**.

### Step 3: Create OAuth 2.0 Client Credentials
1. In the left navigation, go to **APIs & Services** > **Credentials**.
2. Click **+ Create Credentials** > **OAuth client ID**.
3. Select **Application type:** `Web application`.
4. Set the **Name:** `RegTech Sentinel Backend`.
5. Under **Authorized JavaScript origins**, add:
   - `http://localhost:8080`
   - `http://localhost:3000`
   - `http://127.0.0.1:8080`
   - `http://127.0.0.1:3000`
6. Under **Authorized redirect URIs**, add:
   - `http://localhost:8080/login/oauth2/code/google`
   - `http://127.0.0.1:8080/login/oauth2/code/google`
7. Click **Create**.
8. Copy the generated **Client ID** and **Client Secret**.

---

## 3. Environment Variable Configuration

Populate the following variables in your environment or in `backend/.env`:

```env
# Google OAuth2 Credentials
GOOGLE_CLIENT_ID=your-google-client-id.apps.googleusercontent.com
GOOGLE_CLIENT_SECRET=GOCSPX-yourGoogleClientSecret12345
GOOGLE_REDIRECT_URI=http://localhost:8080/login/oauth2/code/google

# Frontend Redirection after successful OAuth login
FRONTEND_OAUTH_REDIRECT_URI=http://localhost:3000/oauth2/redirect
```

These map directly to `backend/src/main/resources/application.yml`:
```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID:google-client-id-placeholder}
            client-secret: ${GOOGLE_CLIENT_SECRET:google-client-secret-placeholder}
            redirect-uri: ${GOOGLE_REDIRECT_URI:http://localhost:8080/login/oauth2/code/google}
            scope:
              - openid
              - profile
              - email
```

---

## 4. Frontend Integration

On the frontend, the "Continue with Google" button simply initiates the OAuth flow by directing the browser to the backend authorization endpoint:

```typescript
const handleGoogleLogin = () => {
  window.location.href = `${API_BASE_URL}/oauth2/authorization/google`;
};
```

When Google authenticates the user, it redirects to the backend callback (`/login/oauth2/code/google`). The backend processes the authentication and redirects the browser to:
```
http://localhost:3000/oauth2/redirect?token=<jwt_token>
```
The React `OAuthRedirectHandler` component intercepts the URL, stores the token in local storage, updates the `AuthContext`, and redirects to `/dashboard`.

---

## 5. Development Mode & Testing Without Live Google Credentials

If live Google credentials are not yet configured in `.env`:
1. The backend starts normally with placeholder values (`google-client-id-placeholder`).
2. Standard username/password authentication remains 100% operational with the seeded demo credentials (`regulator`, `admin`, `analyst`, `reviewer`).
3. If a user clicks "Continue with Google" without active credentials, the browser displays a helpful message explaining that OAuth credentials need to be supplied in `backend/.env`.
