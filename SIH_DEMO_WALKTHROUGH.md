# SMART INDIA HACKATHON (SIH) LIVE DEMONSTRATION SCRIPT
## Problem Statement: SIH26006 — Intelligent Freight Forecasting & Bulk Cargo Procurement
### Evaluator Walkthrough & Live Presentation Script (15 Minutes)

---

## 1. Hackathon Presentation Overview

| Segment | Time | Focus Area | Demonstrator Action |
| :---: | :---: | :--- | :--- |
| **Part 1** | 00:00 - 02:00 | **Problem Context & Dual-Core Architecture** | Problem statement intro, Ministry of Steel context, architecture slide |
| **Part 2** | 02:00 - 04:00 | **Authentication & Role-Based Security** | Login via `analyst`, Google OAuth toggle, lockout defense demo |
| **Part 3** | 04:00 - 07:00 | **Market Intelligence & 14-Day Freight Forecast** | Baltic indices (BDI/BCI/BPI), Holt-Winters forecast, 95% confidence bounds |
| **Part 4** | 07:00 - 10:00 | **Nautical Optimization & Lighterage** | Capesize to Paradip vs Haldia Sandheads lighterage, Demurrage risk |
| **Part 5** | 10:00 - 12:00 | **Spot vs COA & Bulk Procurement Landed Cost** | Multi-voyage COA savings ($625k), Landed Cost breakdown (USD/INR) |
| **Part 6** | 12:00 - 13:30 | **Secondary Core: Legal Metrology (LMPC) Engine** | Quick OCR scan demo, Rule 6(1) violations, statutory evidence |
| **Part 7** | 13:30 - 15:00 | **Empirical Proof & Technical Q&A** | 38/38 automated tests, 3.38% MAPE holdout accuracy, Q&A |

---

## 2. Step-by-Step Evaluator Script

### PART 1: The Problem & Dual-Core Architecture (00:00 - 02:00)
- **Spoken Script:**  
  *"Respected Jury Members, India's steel manufacturing sector relies heavily on imported metallurgical coking coal from Australia and South Africa. A single steel mill imports millions of metric tonnes annually. A misjudgment of just \$2 per metric tonne on ocean freight translates to tens of millions of rupees in avoidable procurement losses.*  
  *Furthermore, East Coast Indian ports present stark nautical constraints—deep-draft Capesize vessels can dock at Paradip or Vizag, but are physically barred from Haldia's shallow 8.5m riverine draft, requiring costly lighterage at Sandheads.*  
  *Our project, **RegTech Sentinel**, directly solves **SIH26006** with an Intelligent Freight Forecasting and Vessel Chartering Optimization engine, while seamlessly preserving an enterprise Legal Metrology compliance core."*

---

### PART 2: Authentication & Enterprise Security (02:00 - 04:00)
- **Action:** Open `http://localhost:3000/login` in the browser.
- **Visuals to Highlight:**
  1. Professional responsive login interface.
  2. "Continue with Google" OAuth2 integration.
  3. "Remember me" and "Show/Hide password" toggles.
  4. Quick Demo Role Selector buttons (`Analyst`, `Admin`, `Regulator`, `Reviewer`).
- **Demonstrator Action:**
  - Click the **Analyst** preset button. The form populates `analyst` and `Analyst@123`.
  - Click **Sign In**.
  - System issues a stateless JWT token and navigates directly to the Freight Command Dashboard.
- **Security Note to Jury:**
  *"Our authentication layer enforces BCrypt hashing, JWT session security, sliding-window IP rate limiting (120 req/min), and automatic 15-minute account lockouts after 5 consecutive failed attempts."*

---

### PART 3: Baltic Market Intelligence & 14-Day Freight Forecast (04:00 - 07:00)
- **Action:** Click **Freight Market Intelligence** on the sidebar (`/freight/market-data`).
- **Visuals to Highlight:**
  1. Live KPI cards: Baltic Dry Index (BDI: 1,845), Baltic Capesize Index (BCI: 2,890), Baltic Panamax Index (BPI: 1,620), Brent Crude ($82.50/bbl), LSFO 380 Bunker Fuel ($620/MT).
  2. Provenance badges clearly indicating `HISTORICAL_BENCHMARK` vs `LIVE_FEED`.
  3. Recharts interactive freight time-series trends.
- **Action:** Navigate to **14-Day Freight Forecast** (`/freight/forecast`).
  - Select Route: `Hay Point (Australia) to Paradip (India)`.
  - Click **Generate Forecast**.
- **Visuals to Highlight:**
  1. Holt-Winters additive/multiplicative forecasting curve.
  2. **95% Confidence Interval Upper and Lower Bounds (Shaded corridor).**
  3. Notice how the interval widens monotonically over the 14-day horizon, reflecting natural mathematical uncertainty propagation: $\sigma_{e, h} = \sigma_e \sqrt{1 + (h-1)\alpha^2}$.
- **Spoken Script:**
  *"Unlike black-box models, our Holt-Winters engine delivers explainable 14-day rate forecasts with rigorous 95% confidence bounds. On our 252-day Baltic holdout benchmark across 62 rolling windows, this achieved a Mean Absolute Percentage Error of just 3.38% with 100% confidence interval coverage."*

---

### PART 4: Nautical Optimization & Haldia Lighterage (07:00 - 10:00)
- **Action:** Navigate to **Vessel Optimization** (`/freight/vessels`).
- **Scenario A (Direct Deep Berthing):**
  - Origin: `Hay Point, Australia`
  - Destination: `Paradip, India`
  - Cargo: `150,000 MT Coking Coal`
  - Click **Optimize Vessel Chartering**.
  - **Result:** Capesize vessel (180k DWT) is selected. Status: `Feasible - Direct Berthing (Draft 17.1m allows 16.8m arrival draft)`. Total Freight: $14.80/MT.
- **Scenario B (Shallow Draft with Lighterage):**
  - Change Destination to: `Haldia Dock Complex, India`.
  - Cargo: `75,000 MT Coking Coal`.
  - Click **Optimize Vessel Chartering**.
  - **Result:** Panamax vessel selected. Status: `Feasible via Sandheads / Sagar Island Lighterage`.
  - **Explainable Breakdown:**
    - Mother Vessel Ocean Freight: $16.50/MT
    - Lighterage Stevedoring at Sandheads: $4.20/MT
    - Daughter Barge Haulage to Haldia Berth: $1.50/MT
    - Demurrage Waiting Allowance: 3.5 days ($68,250)
- **Spoken Script:**
  *"Notice how our algorithm understands Indian maritime realities. A naive model would either reject the shipment or send an unladen vessel. Our system automatically models the Sandheads deep-water lighterage operation, including daughter barge haulage and demurrage exposure."*

---

### PART 5: Spot vs Multi-Voyage COA & Landed Cost (10:00 - 12:00)
- **Action:** Navigate to **Procurement & Landed Cost** (`/freight/procurement`).
- **Visuals to Highlight:**
  1. Input FOB Price: $265.00/MT (Hay Point Prime Coking Coal).
  2. Input Cargo Quantity: 150,000 MT.
  3. Select Charter Option: **Contract of Affreightment (COA) vs Single Spot Voyage**.
  4. Click **Calculate Landed Cost**.
- **Results Displayed:**
  - **Spot Voyage Total Landed Cost:** $42,670,000 USD ($284.47 / MT | ₹23,611 / MT INR).
  - **COA Multi-Voyage Total Landed Cost:** $42,045,000 USD ($280.30 / MT | ₹23,265 / MT INR).
  - **Net Direct Savings:** **$625,000 USD (₹5.18 Crore INR)** on this annual contract!
  - **Waterfall Cost Chart:** Clear visual decomposition of FOB Cargo + Ocean Freight + Bunker Surcharge + Demurrage + Insurance + Port Handling.
- **Action:** Click **Export Executive Report (PDF/CSV)**.
  - The browser instantly downloads the complete procurement dossier.

---

### PART 6: Secondary Core — Legal Metrology (LMPC) Engine (12:00 - 13:30)
- **Spoken Script:**  
  *"To demonstrate the unified dual-core capability of our platform, our secondary module handles statutory consumer compliance under the Legal Metrology Act, 2009."*
- **Action:** Log out and log in as `regulator` / `Regulator@123` (or navigate to `/scans`).
- **Action:** Upload a package label image from `test-data/sample-packages/label1.jpg`.
- **Result:**
  - Tesseract OCR extracts all 8 mandatory declarations (MRP, Net Quantity, Date of Packing, Manufacturer Address, Consumer Care).
  - Rule Engine flags violations in red with exact statutory penal sections (e.g., Section 36(1) for missing date of manufacture).

---

### PART 7: Empirical Proof & Technical Q&A (13:30 - 15:00)
- **Action:** Switch to terminal to show empirical test proofs:
  1. Show `mvn test` output: **38 tests run, 0 failures, 0 errors**.
  2. Show `evaluate_forecast_accuracy.py` output: **MAPE 3.38%, MAE $0.513/MT**.
  3. Show `evaluate_ocr_accuracy.py` output: **80/80 fields (100%)**.
- **Closing Statement:**
  *"RegTech Sentinel delivers a complete, reproducible, and mathematically rigorous solution to SIH26006. Thank you, and we welcome your questions!"*
