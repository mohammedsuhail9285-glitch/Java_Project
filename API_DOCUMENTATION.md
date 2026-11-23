# REST API SPECIFICATION REFERENCE
## RegTech Sentinel — Unified Platform Endpoints

**Base URL (Local):** `http://localhost:8080/api/v1`  
**Authentication Scheme:** HTTP Bearer Authentication (JWT in `Authorization` header)  
**Data Format:** JSON (`application/json`)

---

## 1. Authentication & Security Endpoints (`/api/v1/auth`)

### 1.1 User Login
- **Endpoint:** `POST /api/v1/auth/login`
- **Access:** Public (Rate Limited to 120 req/min)
- **Request Body:**
  ```json
  {
    "username": "analyst",
    "password": "Analyst@123",
    "rememberMe": true
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Authentication successful",
    "data": {
      "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
      "type": "Bearer",
      "userId": 3,
      "username": "analyst",
      "email": "analyst@regtech.gov.in",
      "fullName": "Senior Freight Analyst",
      "role": "ROLE_ANALYST",
      "department": "Freight Intelligence",
      "expiresIn": 86400
    }
  }
  ```
- **Failure Responses:**
  - `401 Unauthorized`: `"Invalid username or password. Please verify your credentials."`
  - `401 Unauthorized` (Account Locked): `"Account is temporarily locked. Please try again later or reset your password."`

---

### 1.2 User Registration
- **Endpoint:** `POST /api/v1/auth/register`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "username": "shipping_officer",
    "email": "officer@steel.gov.in",
    "password": "SecurePassword@123",
    "fullName": "Shipping Procurement Officer",
    "department": "Logistics & Chartering"
  }
  ```
- **Success Response (201 Created):**
  ```json
  {
    "success": true,
    "message": "User registered successfully. Please verify your email.",
    "data": {
      "userId": 5,
      "username": "shipping_officer",
      "email": "officer@steel.gov.in",
      "role": "ROLE_VIEWER",
      "emailVerified": false
    }
  }
  ```

---

### 1.3 Verify Email
- **Endpoint:** `POST /api/v1/auth/verify-email?token={token}`
- **Access:** Public
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Email verified successfully. You may now log in.",
    "data": null
  }
  ```

---

### 1.4 Forgot Password
- **Endpoint:** `POST /api/v1/auth/forgot-password`
- **Access:** Public
- **Request Body:**
  ```json
  { "email": "officer@steel.gov.in" }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "If the account exists, a password reset link has been dispatched.",
    "data": null
  }
  ```

---

### 1.5 Reset Password
- **Endpoint:** `POST /api/v1/auth/reset-password`
- **Access:** Public
- **Request Body:**
  ```json
  {
    "token": "a1b2c3d4-e5f6-7a8b-9c0d-1e2f3a4b5c6d",
    "newPassword": "BrandNewPassword@123"
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Password has been successfully reset. Please log in with your new credentials.",
    "data": null
  }
  ```

---

## 2. User Administration Endpoints (`/api/v1/users`)

### 2.1 Promote User Role
- **Endpoint:** `PATCH /api/v1/users/{id}/role`
- **Access:** Restricted to `ROLE_ADMIN`
- **Query / Param:** `newRole=ROLE_ANALYST`
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "User role updated successfully",
    "data": {
      "userId": 5,
      "username": "shipping_officer",
      "role": "ROLE_ANALYST"
    }
  }
  ```

### 2.2 Unlock Account
- **Endpoint:** `POST /api/v1/users/{id}/unlock`
- **Access:** Restricted to `ROLE_ADMIN`
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "User account unlocked successfully",
    "data": null
  }
  ```

---

## 3. SIH26006 Maritime Freight Endpoints (`/api/v1/freight`)

### 3.1 Get Baltic Market Intelligence & Benchmarks
- **Endpoint:** `GET /api/v1/freight/market-data`
- **Access:** Authenticated (`VIEWER`, `ANALYST`, `REVIEWER`, `REGULATOR`, `ADMIN`)
- **Query Params:** `days=30` (optional, default 30)
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Market data retrieved successfully",
    "data": {
      "currentIndices": {
        "BDI": 1845.0,
        "BCI": 2890.0,
        "BPI": 1620.0,
        "BSI": 1280.0,
        "BrentCrudeUSD": 82.50,
        "LSFO380USD": 620.00
      },
      "historicalSeries": [
        {
          "recordDate": "2026-11-20",
          "bdi": 1840.0,
          "bci": 2875.0,
          "bpi": 1615.0,
          "bsi": 1275.0,
          "brentCrudeUsd": 82.10,
          "dataSource": "HISTORICAL_BENCHMARK"
        }
      ]
    }
  }
  ```

---

### 3.2 14-Day Freight Forecast with 95% Confidence Intervals
- **Endpoint:** `GET /api/v1/freight/forecast`
- **Access:** Authenticated (`ANALYST`, `ADMIN`)
- **Query Params:**
  - `routeCode=AU-HAY-IN-PAR` (Hay Point to Paradip)
  - `horizonDays=14`
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "14-day freight forecast generated",
    "data": {
      "routeCode": "AU-HAY-IN-PAR",
      "modelUsed": "Holt-Winters Triple Exponential Smoothing",
      "baselineRateUSD": 14.80,
      "predictions": [
        {
          "dayIndex": 1,
          "forecastDate": "2026-11-24",
          "predictedRateUSD": 14.85,
          "confidenceIntervalLower95": 14.32,
          "confidenceIntervalUpper95": 15.38,
          "trendDirection": "UPWARD"
        },
        {
          "dayIndex": 14,
          "forecastDate": "2026-12-07",
          "predictedRateUSD": 15.42,
          "confidenceIntervalLower95": 13.90,
          "confidenceIntervalUpper95": 16.94,
          "trendDirection": "UPWARD"
        }
      ]
    }
  }
  ```

---

### 3.3 Automated Vessel Chartering Optimization
- **Endpoint:** `POST /api/v1/freight/optimize-vessel`
- **Access:** Authenticated (`ANALYST`, `ADMIN`)
- **Request Body:**
  ```json
  {
    "originPortCode": "AU-HAY",
    "destinationPortCode": "IN-HAL",
    "cargoType": "COKING_COAL",
    "cargoQuantityMT": 75000,
    "charterType": "SPOT"
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Vessel optimization completed",
    "data": {
      "recommendedVesselClass": "PANAMAX",
      "alternativeOptions": [
        {
          "vesselClass": "PANAMAX",
          "capacityMT": 75000,
          "draftMeters": 14.2,
          "feasible": true,
          "lighterageRequired": true,
          "lighterageLocation": "Sandheads / Sagar Island Deep Anchorage",
          "estimatedFreightPerMT": 17.65,
          "demurrageRiskDays": 3.0,
          "totalVoyageCostUSD": 1323750.00,
          "justification": "Panamax vessel selected. Haldia riverine draft (8.5m) requires partial lighterage at Sandheads deep water anchorage. Lighterage cost added: $4.20/MT."
        },
        {
          "vesselClass": "CAPESIZE",
          "capacityMT": 180000,
          "draftMeters": 18.2,
          "feasible": false,
          "rejectionReason": "Exceeds Haldia maximum permissible LOA (230m) and Sandheads daughter lighterage capacity limits."
        }
      ]
    }
  }
  ```

---

### 3.4 Spot vs Multi-Voyage COA Comparative Analysis
- **Endpoint:** `POST /api/v1/freight/compare-contracts`
- **Access:** Authenticated (`ANALYST`, `ADMIN`)
- **Request Body:**
  ```json
  {
    "routeCode": "AU-HAY-IN-PAR",
    "annualCargoQuantityMT": 500000,
    "voyageCount": 7
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Contract comparison completed",
    "data": {
      "spotOption": {
        "averageRatePerMT": 15.20,
        "totalAnnualCostUSD": 7600000.00,
        "volatilityRisk": "HIGH",
        "marketSpikeExposure": "100%"
      },
      "coaOption": {
        "fixedRatePerMT": 13.95,
        "totalAnnualCostUSD": 6975000.00,
        "annualSavingsUSD": 625000.00,
        "percentageSavings": 8.22,
        "bunkerEscalationClause": "LSFO 380 +/- $15/MT threshold"
      },
      "recommendation": "Contract of Affreightment (COA) is strongly recommended, delivering $625,000 USD (8.22%) annual savings and immunizing steel mill inventory from spot market rate spikes."
    }
  }
  ```

---

### 3.5 Bulk Cargo Landed Cost Calculation
- **Endpoint:** `POST /api/v1/freight/landed-cost`
- **Access:** Authenticated (`ANALYST`, `ADMIN`)
- **Request Body:**
  ```json
  {
    "originPort": "Hay Point, Australia",
    "dischargePort": "Paradip, India",
    "cargoType": "COKING_COAL",
    "cargoQuantityMT": 150000,
    "fobPriceUSDPerMT": 265.00,
    "bunkerFuelUSDPerMT": 620.00,
    "vesselClass": "CAPESIZE"
  }
  ```
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Landed cost calculated",
    "data": {
      "fobCostTotalUSD": 39750000.00,
      "oceanFreightPerMT": 14.80,
      "oceanFreightTotalUSD": 2220000.00,
      "bunkerSurchargeTotalUSD": 284000.00,
      "portDemurrageEstimatedUSD": 78000.00,
      "lighterageCostUSD": 0.00,
      "marineInsuranceUSD": 45000.00,
      "totalLandedCostUSD": 42377000.00,
      "landedCostPerMTUSD": 282.51,
      "landedCostPerMTINR": 23448.33,
      "exchangeRateUSDINR": 83.00
    }
  }
  ```

---

### 3.6 Export Executive Procurement Report
- **Endpoint:** `GET /api/v1/freight/reports/export?format=csv` (or `format=pdf`)
- **Access:** Authenticated (`ANALYST`, `REVIEWER`, `ADMIN`)
- **Response Header:** `Content-Disposition: attachment; filename="procurement_report.csv"`
- **Response Content:** Stream of CSV or PDF bytes containing complete voyage and cost breakdown.

---

## 4. Legal Metrology (LMPC) Endpoints (`/api/v1`)

### 4.1 Execute Compliance Label Scan
- **Endpoint:** `POST /api/v1/scans`
- **Access:** Authenticated (`REGULATOR`, `ADMIN`)
- **Request Content:** `multipart/form-data` with `file=@label_image.jpg`
- **Success Response (200 OK):**
  ```json
  {
    "success": true,
    "message": "Packaging scan processed",
    "data": {
      "scanId": "scan-89421",
      "complianceStatus": "NON_COMPLIANT",
      "totalViolations": 1,
      "extractedFields": {
        "manufacturerName": "Patanjali Foods Limited",
        "mrp": "Rs. 150.00",
        "netQuantity": "500 g",
        "unitSalePrice": "Rs. 0.30 per g"
      },
      "violations": [
        {
          "ruleNumber": "Rule 6(1)(d)",
          "severity": "CRITICAL",
          "description": "Missing month and year of manufacture/packing",
          "penaltySection": "Section 36(1) of Legal Metrology Act, 2009"
        }
      ]
    }
  }
  ```

### 4.2 Query Statutory Rules
- **Endpoint:** `GET /api/v1/rules`
- **Access:** Public / Authenticated
- **Success Response (200 OK):** Returns all 8 Legal Metrology Packaging Commodity rules and thresholds.
