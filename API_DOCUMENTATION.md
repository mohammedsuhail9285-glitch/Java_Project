# RegTech Sentinel — REST API Documentation

Comprehensive REST API specification for RegTech Sentinel (Department of Consumer Affairs, Government of India).

Base URL: `http://localhost:8080/api/v1`  
AI Microservice Base URL: `http://localhost:8000`  
Swagger UI: `http://localhost:8080/swagger-ui.html`  
OpenAPI JSON: `http://localhost:8080/v3/api-docs`

---

## 1. Authentication & RBAC (`/auth`)

### `POST /auth/login`
Authenticates a user and issues a signed JWT bearer token.
- **Request Body:**
  ```json
  {
    "username": "regulator",
    "password": "Regulator@123"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "tokenType": "Bearer",
    "user": {
      "id": 2,
      "username": "regulator",
      "email": "regulator@consumeraffairs.nic.in",
      "fullName": "Priya Sharma",
      "role": "REGULATOR",
      "designation": "Deputy Controller",
      "department": "Legal Metrology Division"
    }
  }
  ```

### `GET /auth/me`
Retrieves the authenticated user's profile.
- **Headers:** `Authorization: Bearer <token>`
- **Response `200 OK`:** User object

---

## 2. Product Catalog (`/products`)

### `GET /products`
Searches catalog with optional filters and pagination.
- **Parameters:** `keyword`, `categoryId`, `sellerId`, `status`, `page`, `size`, `sortBy`, `direction`
- **Response `200 OK`:** `ApiResponse<Page<ProductDetailDto>>`

### `GET /products/{id}`
Retrieves complete product record by ID.

### `GET /products/{id}/fields`
Retrieves OCR-extracted statutory fields with bounding boxes and confidence scores.

### `GET /products/{id}/ocr`
Retrieves raw OCR text, recognized lines, and language metadata.

---

## 3. Violations & Regulatory Review (`/violations`)

### `GET /violations`
Lists potential non-compliance cases.
- **Parameters:** `status` (`NEW`, `UNDER_REVIEW`, `CONFIRMED`, `DISMISSED`, `RESOLVED`), `severity` (`CRITICAL`, `MAJOR`, `MINOR`), `ruleId`, `productId`, `page`, `size`
- **Response `200 OK`:** `Page<ViolationDto>`

### `GET /violations/{id}`
Retrieves single violation with linked `Evidence` (original image path, cropped crop path, bounding box coordinates, and raw OCR text).

### `POST /violations/{id}/review`
Commits a human regulator review decision with statutory rationale.
- **Access:** `REGULATOR`, `REVIEWER`, `ADMIN`
- **Request Body:**
  ```json
  {
    "action": "CONFIRM",
    "comment": "Verified physical label does not bear compliant declaration as required by LMPC Rule 6.",
    "remediationAction": "Issue statutory notice under LM Act Sec 36"
  }
  ```
- **Response `200 OK`:** Updated `ViolationDto`

---

## 4. Ingestion & Scans (`/ingestion`, `/scans`)

### `POST /ingestion/manual`
Creates a single product with optional image upload and triggers automated scan.

### `POST /ingestion/upload`
Uploads a batch catalog file (CSV or JSON).

### `POST /ingestion/url`
Fetches a single marketplace URL via the safe crawler interface.

### `POST /ingestion/crawl-sandbox`
Executes safe simulated sandbox crawl across pre-configured e-commerce products without making live external HTTP scraping calls.
- **Request Body:**
  ```json
  {
    "batchSize": 5,
    "platform": "RetailBharat"
  }
  ```

### `GET /scans/jobs`
Lists background scan jobs with execution stages (`PROCESSING`, `OCR_PROCESSING`, `VALIDATING`, `COMPLETED`).

---

## 5. LMPC Statutory Rules & Simulator (`/rules`)

### `GET /rules`
Lists all active legal metrology rules (`LM001` to `LM010`).

### `PUT /rules/{id}`
Updates rule thresholds, severity, remediation text, or enabled status. (Requires `ADMIN` or `REGULATOR`).

### `POST /rules/simulator/run`
Executes dry-run simulation of a draft rule against sample products without committing to database.

---

## 6. Regulatory Reports & Exports (`/reports`)

### `GET /reports/pdf/product/{productId}`
Downloads court-ready, formal PDF inspection dossier with statutory disclaimer.

### `GET /reports/excel`
Downloads multi-sheet XLSX spreadsheet of all catalog items and potential violations.

### `GET /reports/csv`
Downloads flat CSV stream of potential violations.

---

## 7. AI Microservice & Operational Monitoring (`/ai/monitoring`)

### `GET /ai/monitoring`
Returns operational telemetry including FastAPI health, active OCR engine (`tesseract` vs `synthetic_ocr_fallback`), average OCR confidence %, and vision/NLP model details.

---

## 8. SIH26006: Maritime Freight Forecasting & Bulk Cargo Procurement (`/freight`)

Base URL: `http://localhost:8080/api/v1/freight`  
Role Access: Authenticated users (`ADMIN`, `REGULATOR`, `REVIEWER`, `ANALYST`)

### `GET /freight/overview`
Aggregates macro maritime market telemetry, Baltic Dry indices, monitored East Coast ports, trade routes, active procurement plans, and total cost savings realized.
- **Response `200 OK`:**
  ```json
  {
    "marketData": {
      "bdi": 1820,
      "bci": 2840,
      "bpi": 1690,
      "bsi": 1280,
      "bhsi": 740,
      "bdiChangePct": 2.45,
      "bdiSentiment": "BULLISH",
      "bunkerIfo380Usd": 485.50,
      "bunkerVlsfoUsd": 620.00
    },
    "totalPorts": 5,
    "totalRoutes": 5,
    "totalProcurementPlans": 3,
    "totalOptimizedSavingsInr": 13962500.0,
    "monitoredPorts": [...],
    "activeTradeRoutes": [...],
    "recentPlans": [...]
  }
  ```

### `GET /freight/ports`
Lists all Indian East Coast discharge ports with max permissible draft, Panamax/Capesize berths, and waiting times.
- Ports:
  - `INPRT` (Paradip Port): 14.5m draft, Deepwater bulk terminal
  - `INVTZ` (Visakhapatnam Port): 14.5m draft, Inner & Outer harbor
  - `INHLD` (Haldia Dock Complex): 11.0m draft (Restricts Capesize/Panamax)
  - `INDHM` (Dhamra Port): 18.0m draft (Capesize capable deepwater)
  - `INENR` (Kamarajar / Ennore Port): 16.0m draft (Bulk coal berth)

### `GET /freight/vessels`
Lists bulk carrier vessel classes (`CAPESIZE`, `PANAMAX`, `SUPRAMAX`, `HANDYSIZE`) with DWT ranges, typical draft, daily fuel consumption, and cargo suitability.

### `GET /freight/routes`
Lists international bulk trade corridors:
- Australia (Hay Point/Gladstone) -> Paradip/Vizag (Coking Coal)
- Indonesia (Samarinda/Muara Pantai) -> Haldia/Dhamra (Thermal Coal)
- South Africa (Richards Bay) -> Ennore/Paradip (Steam Coal)
- Brazil (Tubarao/Ponta da Madeira) -> Dhamra/Paradip (Iron Ore Pellets)
- Black Sea / Novorossiysk -> Vizag/Paradip (Fertilizers & Minerals)

### `GET /freight/forecasts`
Retrieves time-series freight rate forecasts (7-day, 14-day, 30-day, 60-day horizons) across vessel classes with 95% confidence intervals and rate trajectory direction (`UPWARD`, `STABLE`, `DOWNWARD`).

### `POST /freight/chartering/optimize`
Decision-support chartering optimization model. Evaluates cargo quantity, commodity, origin, destination port draft constraints, Baltic forward rate curve, and current market sentiment.
- **Request Body:**
  ```json
  {
    "commodity": "Australian Premium Hard Coking Coal",
    "quantityTons": 75000.0,
    "originCountry": "Australia",
    "destinationPortCode": "INPRT",
    "fobPriceUsd": 245.0,
    "targetLaycanStart": "2026-12-01",
    "targetLaycanEnd": "2026-12-08"
  }
  ```
- **Response `200 OK`:**
  ```json
  {
    "planReference": "PROC-20261120-PRT-001",
    "recommendedVesselClass": "PANAMAX",
    "draftFeasible": true,
    "charterDecision": "CHARTER_NOW",
    "decisionRationale": "Panamax vessel draft 13.8m fits Paradip draft limit 14.5m. Forward 14-day freight rate curve indicates 4.2% upward trajectory. Prompt fixture locks in favorable rate USD 24.80/ton.",
    "fobPriceUsd": 245.0,
    "freightRateUsdTon": 24.80,
    "portHandlingUsdTon": 7.50,
    "totalLandedCostUsdTon": 277.30,
    "totalLandedCostInrTon": 23015.90,
    "totalCargoLandedCostInr": 1726192500.0,
    "estimatedCostSavingsInr": 13962500.0
  }
  ```

### `GET /freight/reports/plan/{id}/pdf`
Generates and downloads a formal procurement and vessel fixture dossier as a court-ready / boardroom PDF document with landed cost waterfall and executive signatures.

### `GET /freight/reports/excel`
Generates and downloads a comprehensive multi-sheet Excel workbook (`Maritime_Freight_Procurement_Analysis.xlsx`) containing:
- Sheet 1: Market Telemetry & Baltic Indices
- Sheet 2: East Coast Ports & Draft Specifications
- Sheet 3: Forward Freight Forecasts & Rate Curves
- Sheet 4: Active Procurement Plans & Fixtures

