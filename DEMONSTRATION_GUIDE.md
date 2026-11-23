# SIH26006 Live Demonstration & Evaluation Guide

**Project:** RegTech Sentinel & Maritime Freight Forecasting System (Unified SIH26006 Platform)  
**Smart India Hackathon Problem Statement:** SIH26006 — *Development of an Intelligent Freight Forecasting Model for Optimized Vessel Chartering and Bulk Cargo Procurement from Overseas to East Coast of India* (Ministry of Steel)  
**Dual Regulatory Framework:** Ministry of Steel & Department of Consumer Affairs  

---

## 1. Quick Start for Evaluators

1. **Launch All Services with 1 Click:**
   ```powershell
   .\start_all.bat
   ```
   This automatically launches:
   - Python FastAPI AI & OCR Microservice on `http://localhost:8000`
   - Spring Boot 3.3.4 Enterprise Backend on `http://localhost:8080`
   - React + TypeScript Web Portal on `http://localhost:3000`

2. **Run Automated End-to-End System Verification:**
   ```powershell
   powershell -ExecutionPolicy Bypass -File .\test_system.ps1
   ```
   Verifies all 7 layers (AI service, backend health, authentication, Module 1 LMPC risk summary, LMPC rules, Module 2 Baltic Dry market telemetry, and vessel chartering optimization simulation).

3. **Web Portal Access:**
   - **URL:** [http://localhost:3000](http://localhost:3000)
   - **User Credentials:**
     - `regulator` / `Regulator@123` (Role: Senior Metrology Officer / Regulatory Lead)
     - `admin` / `Admin@123` (Role: System Administrator)
     - `reviewer` / `Reviewer@123` (Role: Compliance Inspector)
     - `analyst` / `Analyst@123` (Role: Bulk Freight Procurement Analyst)

---

## 2. SIH26006 Demonstration Walkthrough (Ministry of Steel)

### Scenario A: Real-Time Maritime Freight Telemetry & East Coast Port Overview
1. Log in as `regulator` or `analyst`.
2. Navigate to **Freight & Charter** in the sidebar -> **Overview** (`/freight`).
3. **Key Visuals to Highlight to Evaluators:**
   - **Baltic Dry Index (BDI) Live Ticker:** Real-time Baltic Dry Index (1,820 pts, +2.45% Bullish sentiment), Capesize (BCI 2,840), Panamax (BPI 1,690), Supramax (BSI 1,280), Handysize (BHSI 740).
   - **Bunker Fuel Pricing:** VLSFO ($620/MT) and IFO380 ($485.50/MT) tracked for accurate bunker adjustment factoring (BAF).
   - **East Coast Port Cards:** Live status of Paradip (14.5m draft), Visakhapatnam (14.5m draft), Haldia (11.0m shallow draft), Dhamra (18.0m deepwater), and Ennore (16.0m draft).
   - **Aggregated Procurement Savings:** Quantified total savings generated across scheduled bulk fixtures (INR 1.39+ Crores).

### Scenario B: AI Multi-Horizon Freight Rate Forecasting Curves
1. Click **Rate Forecasts** in the sidebar (`/freight/forecasts`).
2. **Key Capabilities to Demonstrate:**
   - **Time-Horizon Selector:** Toggle between 7-Day, 14-Day, 30-Day, and 60-Day forecasting horizons.
   - **Vessel Class Forward Curves:** Interactive Recharts curve showing Spot vs 7d vs 14d vs 30d trajectories.
   - **95% Confidence Interval (CI) Band:** Demonstrates statistical risk modeling against volatile maritime shipping rates.
   - **Trade Corridor Heatmap:** Demonstrates rate differentials from Australia (Hay Point) to Paradip, Indonesia (Samarinda) to Haldia, and South Africa (Richards Bay) to Ennore.

### Scenario C: Port Draft Constraints & Vessel Chartering Feasibility Simulator
1. Click **Charter Optimizer** (`/freight/chartering`).
2. **Draft Constraint Test (The "Haldia vs Capesize" Test):**
   - Select **Destination Port:** `Haldia Dock Complex (INHLD)`. Notice the permissible draft is strictly `11.0m`.
   - Select **Cargo Quantity:** `120,000 MT`.
   - The engine automatically detects that a Capesize vessel (18.2m draft) or Panamax vessel (14.5m draft) **CANNOT safely enter Haldia**.
   - Notice the Red Alert: *"Vessel class draft exceeds Haldia channel limit. Transshipment / lighterage or Handysize splitting required."*
   - Change Destination Port to `Dhamra Port (INDHM)`. Notice it turns Green: *"Dhamra (18.0m draft) is Capesize-capable with zero draft penalty."*

### Scenario D: End-to-End Bulk Procurement Plan Simulation & Executive Dossier Export
1. Click **Bulk Procurement** (`/freight/procurement`).
2. Configure a live bulk cargo procurement plan:
   - **Commodity:** Australian Premium Hard Coking Coal
   - **Quantity:** 75,000 Metric Tons
   - **Origin:** Australia (Hay Point Terminal)
   - **Destination Port:** Paradip Port (INPRT)
   - **FOB Base Price:** USD 245.00 / MT
   - **Target Laycan:** 10 to 18 days forward
3. Click **"Run Optimization Model"**.
4. **Inspect the Output:**
   - **Charter Recommendation:** `CHARTER_NOW` (or `HOLD_WAIT` based on forward curve).
   - **Recommended Vessel Class:** `PANAMAX` (75,000 DWT).
   - **Draft Feasibility:** `FEASIBLE` (Panamax draft 13.8m <= Paradip draft 14.5m).
   - **Landed Cost Waterfall:**
     - FOB Base: USD 245.00/MT
     - Ocean Freight: USD 24.80/MT
     - Port Handling & Demurrage: USD 7.50/MT
     - Total Landed Cost: **USD 277.30/MT (INR 23,015.90/MT)**
   - **Estimated Cost Savings:** Over **INR 13.9 Lakhs** saved by locking in favorable spot vs forecasted rate rise.
5. **Demonstrate Boardroom Exports:**
   - Click **"Download PDF Dossier"**: Downloads formal procurement fixture dossier with executive sign-off sections, landed cost breakdown, and navigation constraints.
   - Click **"Export Excel Workbook"**: Downloads multi-sheet XLSX file (`Maritime_Freight_Procurement_Analysis.xlsx`) containing market telemetry, port specifications, forward curves, and procurement plans.

---

## 3. Module 1 Demonstration Walkthrough (Legal Metrology Compliance)

1. Navigate to **Legal Metrology -> Dashboard** (`/dashboard`).
   - Highlight **200 realistic e-commerce products** across Packaged Food, Cosmetics, and Electronics.
   - View compliance metrics: Compliant vs Potential Non-Compliance vs Under Review.
2. Navigate to **Single Scan** (`/scan`).
   - Upload any sample packaging image (e.g. `storage/uploads/mock_label_atta_5kg.png` or `mock_label_facewash_150ml.png`).
   - Watch OpenCV automated deskew preprocessor straighten skewed images and Tesseract OCR extract statutory fields.
   - Notice the statutory declarations verified: MRP, Net Quantity, Country of Origin, Customer Care.
3. Navigate to **Violations & Review** (`/violations`).
   - Inspect flagged non-compliance cases (e.g. missing customer care email/phone, price overcharge where listing price > on-pack MRP).
   - Perform human regulator action: Click **"Confirm Violation"** with statutory justification.
   - Generate official PDF Inspection Dossier.

