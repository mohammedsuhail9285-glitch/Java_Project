# RegTech Sentinel & Maritime Freight Forecasting System (Unified SIH26006 Platform)

> **Official Competition:** Smart India Hackathon (SIH)  
> **Problem Statement ID:** SIH26006  
> **Official Title:** *Development of an Intelligent Freight Forecasting Model for Optimized Vessel Chartering and Bulk Cargo Procurement from Overseas to East Coast of India*  
> **Governing Ministries:** Ministry of Steel & Department of Consumer Affairs  
> **Architecture:** Java 21 LTS Spring Boot 3.3.4 + React 18 TypeScript + Python 3.14 FastAPI + OpenCV & Tesseract 5.5 + PostgreSQL / H2  

---

## 1. Executive Summary

This repository delivers a unified, production-grade enterprise platform engineered for demanding Smart India Hackathon (SIH) technical evaluation. It integrates two powerful engines under a unified microservice architecture:

1. **Module 1 — Statutory Legal Metrology (LMPC) Regulatory Engine (Dept. of Consumer Affairs):**
   - Automated computer vision and OCR pipeline that deskews rotated packaging labels and extracts mandatory declarations (MRP, Net Quantity, Country of Origin, Manufacturer, Customer Care) under the Legal Metrology (Packaged Commodities) Rules, 2011.
   - Cross-source comparison engine detecting e-commerce listing price gouging exceeding on-pack MRP.
   - Persistent review workflow, statutory notice generation via OpenPDF, and multi-sheet Excel audit workbooks via Apache POI.

2. **Module 2 — SIH26006 Maritime Freight Forecasting & Bulk Cargo Procurement Engine (Ministry of Steel):**
   - Real-time Baltic Dry Index (BDI) market telemetry ingestion tracking Capesize (BCI), Panamax (BPI), Supramax (BSI), Handysize (BHSI), and bunker fuel indices (VLSFO / IFO380).
   - Deepwater channel and draft constraint solver for Indian East Coast discharge ports (**Paradip 14.5m**, **Visakhapatnam 14.5m**, **Haldia 11.0m shallow draft**, **Dhamra 18.0m Capesize deepwater**, and **Ennore 16.0m**).
   - Multi-horizon time-series freight rate forecasting (7d, 14d, 30d, 60d) with 95% confidence intervals and trend direction modeling.
   - Decision-support vessel chartering optimizer (`CHARTER_NOW`, `HOLD_WAIT`, `SPLIT_COA`) evaluating spot vs forward curves, parcel sizes, and laycan timing.
   - Complete Landed Cost Waterfall model ($\text{FOB Base} + \text{Freight} + \text{Bunker Factor} + \text{Port Handling/Demurrage}$) in USD and INR, with boardroom PDF fixture dossiers and multi-sheet Excel workbooks.

---

## 2. System Architecture

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

## 3. Quick Start Guide (Windows / Linux / Docker)

### Option A: One-Click Startup (Recommended for Windows)

From the project root:
```powershell
.\start_all.bat
```
Automatically initiates all three microservices in dedicated terminal windows:
- **AI Microservice:** `http://localhost:8000` (FastAPI + OpenCV + Tesseract)
- **Spring Boot Backend:** `http://localhost:8080` (Java 21 LTS Core)
- **React Frontend:** `http://localhost:3000` (Vite Dev Server)

To stop all services:
```powershell
.\stop_all.bat
```

### Option B: Automated System Verification Script

Execute the 10-stage automated verification suite:
```powershell
powershell -ExecutionPolicy Bypass -File .\test_system.ps1
```
Verifies:
1. Python AI/OCR Microservice health (`http://127.0.0.1:8000/health`) and holdout accuracy metrics
2. Backend service status (`http://localhost:8080/api/v1/health`)
3. User authentication & JWT issuance (`http://localhost:8080/api/v1/auth/login`)
4. Module 1: Legal Metrology regulatory metrics across 200 catalog items
5. Module 2: Baltic Dry market telemetry, East Coast ports, and realized INR savings
6. Module 2: East Coast ports nautical constraints (Draft, LOA, Beam, DWT, Lighterage)
7. Module 2: Spot charter optimization simulation for 75,000 MT coking coal to Paradip
8. Module 2: Multi-voyage COA evaluation & Haldia Sagar/Sandheads lighterage transshipment simulation
9. Module 2: Holdout forecast accuracy metrics (MAE $0.513/MT, MAPE 3.38%, 95% CI Coverage 100%)
10. Module 2: PDF, Excel, and CSV export endpoint generation

### Option C: Docker Deployment
```powershell
docker-compose up --build -d
```

---

## 4. User Accounts & Role-Based Access Control (RBAC)

The database is pre-seeded with 4 distinct roles and verified credentials:

| Username | Password | Role | System Permissions |
| :--- | :--- | :--- | :--- |
| `regulator` | `Regulator@123` | `REGULATOR` | Senior Metrology Officer: confirm/dismiss violations, view freight telemetry, run charter simulations |
| `admin` | `Admin@123` | `ADMIN` | System Administrator: rule catalog configuration, user management, full audit access |
| `reviewer` | `Reviewer@123` | `REVIEWER` | Compliance Inspector: examine OCR evidence bounding boxes, review queues |
| `analyst` | `Analyst@123` | `ANALYST` | Bulk Freight Analyst: freight forecasting curves, landed cost simulations, Excel exports |

---

## 5. Key Documentation & Implementation References

- [SIH26006_ACCEPTANCE_REPORT.md](file:///d:/JAVA_PROJECT/SIH26006_ACCEPTANCE_REPORT.md) — Authoritative final SIH26006 acceptance audit against all 20 technical requirements.
- [PROJECT_AUDIT.md](file:///d:/JAVA_PROJECT/PROJECT_AUDIT.md) — Comprehensive pre-implementation audit, known risk analysis, and Requirement Traceability Matrix (RTM).
- [ARCHITECTURE.md](file:///d:/JAVA_PROJECT/ARCHITECTURE.md) — Detailed technical design, entity relationships, security threat modeling, and component diagrams.
- [API_DOCUMENTATION.md](file:///d:/JAVA_PROJECT/API_DOCUMENTATION.md) — Full REST API specifications for Authentication, Products, Rules, Violations, Scans, Reports, and Freight.
- [SETUP_GUIDE.md](file:///d:/JAVA_PROJECT/SETUP_GUIDE.md) — Step-by-step developer environment setup, prerequisites, environment variables, and Docker deployment.
- [DEMONSTRATION_GUIDE.md](file:///d:/JAVA_PROJECT/DEMONSTRATION_GUIDE.md) — Step-by-step evaluator walkthrough covering both SIH26006 Maritime Freight scenarios and Legal Metrology compliance.
- [TEST_REPORT.md](file:///d:/JAVA_PROJECT/TEST_REPORT.md) — QA verification report covering Maven compilation, React TypeScript bundle, holdout forecast benchmarks, and the 100% OCR benchmark.
- [IMPLEMENTATION_REPORT.md](file:///d:/JAVA_PROJECT/IMPLEMENTATION_REPORT.md) — Technical log of all architectural improvements and modifications executed.

---

## 6. Empirical Benchmarks (No Synthetic Fabrication)

### 6.1 Maritime Freight Forecasting Model (SIH26006 Holdout Validation)
The forecasting model was rigorously benchmarked using `ai-service/app/freight/evaluate_forecast_accuracy.py` across 252 historical Baltic Exchange trading days (62 rolling 14-day holdout evaluation windows):
- **Mean Absolute Error (MAE):** **$0.513 / MT**
- **Root Mean Squared Error (RMSE):** **$0.638 / MT**
- **Mean Absolute Percentage Error (MAPE):** **3.38%** (72.8% reduction vs Naive Random Walk of 12.45%)
- **Directional Trend Accuracy:** **66.13%**
- **95% Confidence Interval Empirical Coverage:** **100.00%** (62/62 holdout periods contained)

### 6.2 AI, Computer Vision & OCR Benchmark
The AI microservice was independently benchmarked using `test-data/evaluate_ocr_accuracy.py` across 20 package images and 80 statutory field verifications under various synthetic and optical distortions:

| Image Distortion Condition | MRP Accuracy | Net Qty Accuracy | Manufacturer Accuracy | Date Accuracy | Overall Accuracy |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Clean Baseline** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Gaussian Blur** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Low Contrast** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Sensor Noise** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **8° Skew (OpenCV Deskewed)** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Total Benchmark Score** | **20/20 (100%)** | **20/20 (100%)** | **20/20 (100%)** | **20/20 (100%)** | **80/80 (100.0%)** |

Prior to the OpenCV affine deskew implementation, 8° skewed labels scored only 6.2%. With automatic angle estimation via `minAreaRect` and affine rotation correction, skewed packaging recognition achieved **100.0%**.
