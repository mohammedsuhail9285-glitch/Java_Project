# System Architecture & Technical Design Document

**Project:** RegTech Sentinel & Maritime Freight Forecasting System (Unified SIH26006 Platform)  
**Problem Statement:** SIH26006 — *Development of an Intelligent Freight Forecasting Model for Optimized Vessel Chartering and Bulk Cargo Procurement from Overseas to East Coast of India* (Ministry of Steel) & Statutory Legal Metrology E-Commerce Compliance (Dept. of Consumer Affairs)  
**Lead Frameworks:** Java 21 LTS, Spring Boot 3.3.4, React 18, TypeScript, Python 3.14 FastAPI, OpenCV, Tesseract OCR 5.5, PostgreSQL / H2  

---

## 1. Executive Architectural Overview

The application is structured as a high-performance **Unified Dual-Core Enterprise Platform**:
- **Module 1 (Consumer Regulatory Defense):** Automated Legal Metrology (Packaged Commodities) Rules, 2011 compliance verification using computer vision OCR and deterministic rule engines.
- **Module 2 (Maritime Bulk Logistics & Procurement):** Intelligent dry-bulk ocean freight rate forecasting and vessel chartering optimization for raw material imports (coking coal, thermal coal, limestone, iron ore pellets) to Indian East Coast ports.

```
                      +------------------------------------------+
                      |       Modern Web Client (React 18)       |
                      |   TypeScript + TailwindCSS + Recharts   |
                      +--------------------+---------------------+
                                           |
                              HTTP / REST JSON & File Uploads
                                           |
                                           v
                      +--------------------+---------------------+
                      |    Enterprise Core: Spring Boot 3.3.4    |
                      |                (Java 21 LTS)             |
                      +--------------------+---------------------+
                      |  - Spring Security (JWT / RBAC)          |
                      |  - Module 1: LMPC Compliance Engine      |
                      |  - Module 2: Maritime Charter Solver     |
                      |  - PDF / Excel Reporting Engine (OpenPDF)|
                      |  - Flyway Database Migrations (V1-V5)    |
                      +----------+--------------------+----------+
                                 |                    |
                Internal Microservice RPC        JPA / Hibernate ORM
                                 |                    |
                                 v                    v
         +-----------------------+-----+    +---------+------------------+
         |   AI Microservice (FastAPI) |    |   Relational Database      |
         |  - Tesseract 5.5 Engine     |    |   - H2 File/Mem (Dev)      |
         |  - OpenCV Deskew Preprocess |    |   - PostgreSQL (Docker/Prod|
         |  - Time-Series Freight Model|    +----------------------------+
         +-----------------------------+
```

---

## 2. Core Modules & Component Architecture

### Module 1: Legal Metrology (LMPC) Regulatory Engine
1. **Packaging Image Ingestion:** Multi-part file upload with validation of MIME types, magic bytes, dimensions, and file size (up to 15MB).
2. **Computer Vision & OCR Deskew:**
   - Automated contour orientation detection using OpenCV `minAreaRect`.
   - Affine transformation to rotate skewed packaging labels back to 0° baseline.
   - Dynamic Tesseract OCR executable discovery across environment paths.
   - Normalized text extraction into statutory fields: MRP, Net Quantity, Manufacturer, Country of Origin, Customer Care, Date of Packaging.
3. **Deterministic Statutory Rules Engine:**
   - Evaluates mandatory rules (`LM001` - `LM010`) according to LMPC Rules 2011 Rule 6.
   - Cross-source comparison: detects e-commerce listing price exceeding on-pack MRP.
   - Audit trail persistence: records evidence crops, bounding boxes, rule violations, and human regulator decisions (`CONFIRM`, `DISMISS`, `RESOLVE`).

### Module 2: SIH26006 Maritime Freight & Bulk Cargo Engine
1. **Baltic Dry Index (BDI) Telemetry Ingestion:**
   - Tracks sub-indices: Baltic Capesize (BCI), Panamax (BPI), Supramax (BSI), Handysize (BHSI).
   - Monitors bunker fuel pricing (VLSFO / IFO380) and USD/INR exchange rates.
2. **East Coast Port & Channel Draft Solver:**
   - Models physical navigational constraints across 5 major discharge ports:
     - **Paradip (INPRT):** 14.5m draft, deepwater bulk terminal.
     - **Visakhapatnam (INVTZ):** 14.5m draft, outer harbor.
     - **Haldia (INHLD):** 11.0m draft (strictly restricts Capesize/Panamax, forces Handysize or lighterage).
     - **Dhamra (INDHM):** 18.0m draft (Capesize capable deepwater).
     - **Ennore / Kamarajar (INENR):** 16.0m draft (dedicated coal berths).
3. **Multi-Horizon Rate Forecasting:**
   - Computes forward freight curves for 7-day, 14-day, 30-day, and 60-day horizons.
   - Evaluates rate trajectory (`UPWARD`, `STABLE`, `DOWNWARD`) with 95% confidence intervals.
4. **Chartering Decision Engine:**
   - Recommends fixture timing:
     - `CHARTER_NOW`: When forward rate trajectory is rising or port congestion is building.
     - `HOLD_WAIT`: When forward rates are softening, recommending waiting 7-10 days to capture lowest freight rates.
     - `SPLIT_COA`: Contract of Affreightment splitting for massive parcels (>120,000 MT) facing draft limits.
5. **Landed Cost Waterfall Model:**
   - Landed Cost/MT = $\text{FOB Base Price} + \text{Ocean Freight Rate} + \text{Bunker Adjustment Factor (BAF)} + \text{Port Demurrage / Handling}$.
   - Computes both USD/MT and INR/MT values and computes net procurement savings against spot benchmark.

---

## 3. Database Architecture & Schema Evolution (Flyway)

The relational schema is managed through idempotent Flyway migrations (`db/migration`):

| Migration | Version Name | Core Tables & Responsibilities |
| :--- | :--- | :--- |
| **V1** | `init_schema` | `users`, `products`, `rules`, `violations`, `audit_logs` |
| **V2** | `seed_data` | Default administrator & officer accounts, initial LMPC rules catalog |
| **V3** | `auth_provider_and_otp` | OAuth2 social provider mappings and multi-factor OTP tokens |
| **V4** | `realistic_demo_catalog` | 200 real-world e-commerce products spanning Packaged Food, Cosmetics, Electronics |
| **V5** | `maritime_freight_schema` | `maritime_ports`, `vessel_classes`, `trade_routes`, `freight_market_data`, `freight_forecasts`, `procurement_plans` |

### Key Entity Relationships
- `MaritimePort` (1) <--- (N) `TradeRoute` (Origin & Destination)
- `VesselClass` (1) <--- (N) `FreightForecast`
- `ProcurementPlan` references `MaritimePort`, `VesselClass`, and calculates landed cost metrics.

---

## 4. Security Architecture

1. **Stateless JWT Authentication:**
   - HMCA-SHA256 tokens signed with high-entropy keys.
   - Enforced fail-safe check: production and docker environments strictly reject hardcoded default development secrets.
2. **Role-Based Access Control (RBAC):**
   - `ADMIN`: Full system configuration, rule updates, user management.
   - `REGULATOR`: Statutory inspection, review confirmation, notice generation.
   - `REVIEWER`: Evidence examination, comment verification.
   - `ANALYST`: Freight market forecasting, procurement scenario simulations.
3. **CORS Security:**
   - Strict origin allowlist (`app.cors.allowed-origins`) replacing insecure wildcard origin patterns.
4. **Resilient HTTP Communication:**
   - Backend client requests to the Python microservice enforce strict connect (1500ms) and read (2000ms) timeouts with analytical fallback algorithms to prevent thread starvation.

---

## 5. Technology Stack Summary

| Layer | Technologies Used | Version |
| :--- | :--- | :--- |
| **Backend Language** | Java (OpenJDK) | 21 LTS |
| **Backend Framework** | Spring Boot | 3.3.4 |
| **Security** | Spring Security + JJWT | 0.12.6 |
| **ORM / Data** | Spring Data JPA / Hibernate | 6.5.3 |
| **Database** | PostgreSQL / H2 In-Memory & File | PostgreSQL 16 / H2 2.2 |
| **Migrations** | Flyway Core | 10.10.0 |
| **Document Generation** | OpenPDF (PDF dossiers) + Apache POI (Excel workbooks) | OpenPDF 2.0.3 / POI 5.2.5 |
| **Frontend Language** | TypeScript / JavaScript | 5.4+ / ES2022 |
| **Frontend Framework** | React + Vite | React 18.3.1 / Vite 5.4 |
| **Styling & Icons** | TailwindCSS + Lucide React | Tailwind 3.4 / Lucide 0.441 |
| **Charts** | Recharts | 2.12.7 |
| **AI / Microservice** | Python FastAPI + Uvicorn | Python 3.14 / FastAPI 0.115 |
| **Computer Vision** | OpenCV Headless + NumPy | OpenCV 4.10+ / NumPy 2.x |
| **OCR Engine** | Tesseract OCR | 5.5.0 |

