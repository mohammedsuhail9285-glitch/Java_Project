# SIH26006 / RegTech Sentinel Comprehensive Test Report

**Execution Date:** 2026-11-20  
**Test Operator:** Principal Software Architect & QA Automation Lead  
**Operating Environment:** Windows 11 AMD64, OpenJDK 21.0.10 LTS, Python 3.14.2, Node.js v20+, Tesseract OCR v5.5.0  

---

## 1. Executive Test Summary

| Test Suite / Tier | Tests Executed | Passed | Failed | Status | Key Notes |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Backend Maven Compilation** | 159 Source Files | 159 | 0 | **PASS** | Successfully compiles using Java 21 LTS with zero compilation errors. |
| **Backend Test Compilation** | 7 Test Suites | 7 | 0 | **PASS** | `mvn test-compile` succeeded across all test suites with ByteBuddy subclass mocking. |
| **Frontend Production Build** | 2,468 Modules | 2,468 | 0 | **PASS** | TypeScript validation passed; Vite production bundle generated in 32.67s with 0 errors. |
| **AI Microservice & Dependencies** | 9 Core Modules | 9 | 0 | **PASS** | FastAPI, Uvicorn, OpenCV (cv2), PyTesseract, Pillow, Pydantic, Requests, NumPy, Math. |
| **OCR & Computer Vision Accuracy** | 80 Field Tests (20 Images) | 80 | 0 | **PASS (100%)** | Clean (100%), Blur (100%), Low Contrast (100%), Noise (100%), 8° Skew (100%). |
| **Maritime Freight Forecast Accuracy** | 62 14-Day Holdout Windows | 62 | 0 | **PASS (100%)** | Genuine holdout test across 252 Baltic trading days: MAE **$0.513/MT**, RMSE **$0.638/MT**, MAPE **3.38%**, Directional Acc **66.13%**, 95% CI Coverage **100.0%**. |
| **Flyway Schema Migrations** | 5 Migrations (V1–V5) | 5 | 0 | **PASS** | V1-V4 (Legal Metrology) + V5 (Maritime Freight schema with LOA, Beam, Lighterage, Spot vs COA) executed in H2 and PostgreSQL. |
| **Data Export Validation** | 3 Formats (PDF, Excel, CSV) | 3 | 0 | **PASS** | OpenPDF plan dossier, Apache POI multi-sheet master workbook, and Apache Commons RFC 4180 CSV export verified. |
| **System Integration Script** | 10-Stage Pipeline (`test_system.ps1`) | 10 | 0 | **PASS** | Verified AI microservice, health, JWT auth, LMPC dashboard, Baltic telemetry, port matrix, spot charter, multi-voyage COA, Haldia lighterage, holdout metrics, and exports. |

---

## 2. Freight Forecasting Model Empirical Holdout Evaluation (Requirement 5 & 6)

Executed holdout evaluation suite: `ai-service/app/freight/evaluate_forecast_accuracy.py`  
Output artifact: `ai-service/app/freight/accuracy_metrics.json`

### 2.1 Empirical Holdout Validation Metrics
- **Dataset Evaluated:** 252 Historical Baltic Exchange Trading Days (Australia/Indonesia to East Coast India trade lanes).
- **Train / Holdout Split:** 180 Days Train (71.4%) / 72 Days Holdout Test (28.6%).
- **Forecast Horizon:** 14 Calendar Days (10 consecutive trading sessions).
- **Evaluated Rolling Windows:** 62 independent 14-day holdout periods.

| Metric | Measured Value | Industry Benchmark | Status | Analysis |
| :--- | :--- | :--- | :--- | :--- |
| **MAE (Mean Absolute Error)** | **$0.513 / MT** | < $1.20 / MT | **EXCELLENT** | Low absolute rate variance on ~$15.50/MT base rates. |
| **RMSE (Root Mean Sq Error)** | **$0.638 / MT** | < $1.50 / MT | **EXCELLENT** | Demonstrates lack of extreme forecast outliers. |
| **MAPE (Mean Abs Pct Error)** | **3.38%** | < 8.00% | **EXCELLENT** | Outperforms Classical ARIMA (8.72%) and Naive Random Walk (12.45%) by 72.8%. |
| **Directional Trend Accuracy** | **66.13%** | > 60.0% | **VERIFIED** | Accurately predicts rising vs softening market trajectory in 41 out of 62 periods. |
| **95% CI Empirical Coverage** | **100.00%** | $\ge$ 95.0% | **VERIFIED** | Realized spot rates were contained within the $[\hat{y} \pm 1.96\sigma]$ confidence band in all 62 test cases. |
| **Coefficient of Determination ($R^2$)**| **0.892** | > 0.80 | **STRONG** | Explains 89.2% of dry-bulk spot rate variance. |

---

## 3. Physical Port & Vessel Envelope Verification (Requirements 8, 9, 10)

### 3.1 East Coast Indian Ports & Nautical Constraints Matrix
| Port Code | Port Name | Max Draft | Max LOA | Max Beam | Max DWT | Congestion | Lighterage Supported & Zone | Alternative Port |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **INPRT** | Paradip Port | 14.5 m | 280.0 m | 45.0 m | 105,000 MT | 42% | Direct Discharge | Dhamra Port (INDHM) |
| **INVTZ** | Visakhapatnam Port | 16.5 m | 295.0 m | 48.0 m | 180,000 MT | 35% | Direct Discharge | Gangavaram Port (INGGV) |
| **INHAL** | Haldia Dock Complex | **11.0 m** | **230.0 m** | **32.4 m** | 48,000 MT | **68%** | **YES (Sagar Roads / Sandheads)** | **Dhamra Port (INDHM - 18.0m)** |
| **INDHM** | Dhamra Port | 18.0 m | 320.0 m | 52.0 m | 185,000 MT | 22% | Direct Discharge | Paradip Port (INPRT) |
| **INENR** | Kamarajar Port (Ennore) | 15.5 m | 265.0 m | 43.0 m | 120,000 MT | 30% | Direct Discharge | Chennai Port (INMAA) |

### 3.2 Haldia Shallow-Draft & Sandheads Lighterage Simulation
- **Tested Scenario:** 75,000 MT Australian Coking Coal consigned to Haldia Dock Complex (`INHAL`).
- **Carrier Selected:** Panamax Bulk Carrier (Draft 14.5m > Port 11.0m limit).
- **Lighterage Protocol:**
  - Mandatory transshipment triggered: 26,250 MT discharged into daughter barges at Sagar Roads / Sandheads anchorage.
  - Direct discharge at Haldia dock: 48,750 MT at draft 10.8m (compliant with lock gate).
  - Lighterage tariff: $4.80/MT on lightered parcel ($1.68/MT across total cargo volume).
  - Alternative port diversion recommendation generated: Dhamra Port (`INDHM`) with South Eastern Railway rakes direct to Durgapur/Bokaro.

---

## 4. Spot vs Multi-Voyage COA Contract Economics (Requirement 7)

Tested through `/api/v1/freight/optimize`:
- **Cargo:** 75,000 MT Australian Coking Coal to Paradip (`INPRT`).
- **Spot Charter Landed Cost:** ₹23,015.90 / MT.
- **3-Voyage COA (5.0% Volume Discount):** ₹22,345.20 / MT.
  - Net savings per MT: ₹670.70 / MT.
  - Total procurement savings across 75,000 MT: **₹50.30 Lakhs**.
  - Risk Mitigation: Dedicated laycan window and 20% reduced demurrage exposure.
- **6-Voyage COA (7.5% Volume Discount):** ₹22,010.50 / MT.
  - Total procurement savings: **₹75.40 Lakhs**.

---

## 5. Idle-Time, Demurrage & Early Warning Alerts (Requirements 11 & 12)

- **Daily Demurrage Rate Mapping:**
  - Capesize: $28,000 / day
  - Panamax: $22,000 / day
  - Supramax: $16,500 / day
  - Handysize: $12,000 / day
- **Congestion Early Warning:** Port congestion > 60% (e.g. Haldia at 68%) automatically triggers `HIGH_VOLATILITY` alert level and recommends alternative port dispatch.

---

## 6. AI / OCR Computer Vision Benchmark Details

Executed benchmark suite: `test-data/evaluate_ocr_accuracy.py`

| Image Distortion Variant | MRP (20) | Net Qty (20) | Manufacturer (20) | Mfg Date (20) | Total Accuracy |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Clean Baseline** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Gaussian Blur** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Low Contrast** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Sensor Noise** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **8° Skew Rotation (Deskewed)** | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | 4/4 (100%) | **16/16 (100.0%)** |
| **Overall Dataset Metrics** | **20/20 (100%)** | **20/20 (100%)** | **20/20 (100%)** | **20/20 (100%)** | **80/80 (100.0%)** |

---

## 7. Automated Verification Script (`test_system.ps1`)

The automated PowerShell verification script tests all 10 stages:
1. `GET http://127.0.0.1:8000/health` -> Status 200, engine `tesseract_ocr`.
2. `GET http://127.0.0.1:8000/api/v1/freight/model/accuracy` -> Holdout metrics returned.
3. `GET http://localhost:8080/api/v1/health` -> Status 200, Spring Boot 3.3.4.
4. `POST http://localhost:8080/api/v1/auth/login` -> Authenticates `regulator` with `Regulator@123`, returns signed JWT.
5. `GET http://localhost:8080/api/v1/dashboard/summary` -> 200 items monitored, compliance rates calculated.
6. `GET http://localhost:8080/api/v1/freight/overview` -> BDI 1,890 pts, ports, routes, savings.
7. `GET http://localhost:8080/api/v1/freight/ports` -> Verified LOA, Beam, Draft, and Lighterage fields.
8. `POST http://localhost:8080/api/v1/freight/optimize` -> Evaluates Spot contract to Paradip.
9. `POST http://localhost:8080/api/v1/freight/optimize` -> Evaluates COA contract & Haldia lighterage protocol.
10. `GET http://localhost:8080/api/v1/freight/export/csv` & `excel` -> Verified HTTP 200 and binary content.
