# NAVISENSE AI — Maritime Freight Forecasting & Vessel Charter Intelligence Platform

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-H2%20Embedded-blue.svg)](https://www.h2database.com/)
[![SIH](https://img.shields.io/badge/Smart%20India%20Hackathon-2026%20(SIH26006)-purple.svg)]()

> **Predict Freight. Optimize Chartering. Reduce Logistics Cost.**  
> *AI-Assisted Maritime Freight Forecasting & Vessel Charter Intelligence Platform for Steel Authority of India Limited (SAIL).*

---

## 1. Problem Statement & Background (SIH26006)

* **Problem ID:** SIH26006
* **Organization:** Ministry of Steel, Government of India
* **Department:** Steel Authority of India Limited (SAIL)
* **Category:** Software
* **Theme:** Transportation & Logistics
* **Statement Title:** *“Development of an Intelligent Freight Forecasting Model for Optimized Vessel Chartering and Bulk Cargo Procurement from overseas to East Coast of India.”*

### The Challenge
SAIL imports over 15 to 20 million tonnes of coking coal and raw materials annually from Australia, the United States, Mozambique, Russia, and Indonesia to blast furnaces across eastern India (Rourkela, Bokaro, Durgapur, Burnpur, Bhilai). Historical vessel chartering has remained largely reactive—locking single-voyage spot fixtures at peak rates, suffering unexpectedly high anchorage congestion at Indian ports, and paying substantial demurrage charges.

### The NaviSense AI Solution
NaviSense AI transforms chartering from a reactive task into a **predictive + explainable + risk-aware + cost-optimized workflow**. It evaluates forward freight trajectories, verifies strict physical berthing drafts, selects optimal deadweight vessel classes, and calculates multi-voyage contract strategies to capture crores in procurement savings.

---

## 2. Key Features

1. **Hybrid Deterministic Freight Forecasting:** 5-horizon forward rate projections (7, 14, 30, 60, 90 days) combining 35% Exponential Smoothing, 30% Least-Squares Trend Regression, 25% WMA, and 10% Seasonality with 95% volatility confidence bands.
2. **Model Evaluation & Baseline Benchmark:** Rigorous MAE, RMSE, and MAPE metrics evaluated against a Naive Persistence Baseline ($F_t = Y_{t-1}$).
3. **Digital Port Constraint & Compatibility Matrix:** Physical validation of maximum allowable draft, length overall (LOA), and beam across Paradip, Visakhapatnam, Gangavaram, Dhamra, Gopalpur, and Haldia.
4. **Vessel Class Fleet Optimizer:** Automated scoring (0–100) and ranking of Handysize, Supramax, Panamax, and Capesize bulk carriers.
5. **Multi-Voyage Logistics Cost Engine:** Complete delivered calculation in USD and INR (₹ Crores / Lakhs) accounting for base freight, sea bunker fuel burn, port discharge tariffs, and berth queue demurrage buffers.
6. **Dynamic Contract Strategy Comparison:** Evaluates Spot Charter, Short-Term Multi-Voyage Contracts (COA), Medium-Term Annual COA, and Dynamic Hybrid Split Allocations.
7. **Explainable AI Rationale & Factor Contributions:** Mathematical synthesis explaining *"Why this recommendation?"* with exact positive and negative contribution factors (+28, -12, etc.).
8. **What-If Scenario Stress Simulator:** Real-time sensitivity testing for freight rate spikes (+10%), fuel inflation, and port congestion surges (+25%).
9. **Historical Backtesting Engine:** 12-month simulated decision validation demonstrating ₹24.28 Cr in cumulative savings and 100% outperformance hit ratio.
10. **Procurement Copilot ("Ask NaviSense"):** 100% offline rule-based domain assistant in Java answering vessel sizing, draft restrictions, and booking timing queries.
11. **Decision Fingerprint Traceability:** Every procurement analysis generates an immutable reference code (`NAV-26006-YYYYMMDD-SEQ`) for audit compliance.
12. **Printable Executive HTML Report:** One-click executive summary and browser PDF print export.

---

## 3. Technology Stack

* **Language:** Java 21 LTS
* **Framework:** Spring Boot 3.3.4 (Spring MVC, Spring Data JPA)
* **Database:** H2 In-Memory Embedded Engine (Zero configuration required)
* **Validation:** Jakarta Validation / Hibernate Validator
* **Frontend:** Thymeleaf, HTML5, CSS3, Bootstrap 5.3.3, Chart.js 4.4.4, Vanilla JavaScript
* **Testing:** JUnit 5, MockMvc, AssertJ
* **Dependencies:** 100% offline executable after initial Maven dependency download (No Python, No Node.js, No external paid APIs).

---

## 4. System Requirements

* **JDK:** Java Development Kit (JDK) 21 or later
* **Maven:** Apache Maven 3.9+
* **OS:** Cross-platform (Windows 10/11, Linux, macOS)
* **RAM:** Minimum 2 GB

Verify your environment:
```bash
java -version
mvn -version
```

---

## 5. Build, Test, and Run Instructions

### Step 1: Clone or Navigate to Project
```bash
cd navisense-ai
```

### Step 2: Run Automated Unit & Integration Tests
```bash
mvn clean test
```

### Step 3: Package Executable JAR
```bash
mvn clean package
```

### Step 4: Launch Application
```bash
mvn spring-boot:run
```
*Alternatively, run the packaged JAR directly (specifying port 8081 if port 8080 is in use by another project):*
```bash
java -jar target/navisense-ai-1.0.0.jar --server.port=8081
```

### Step 4b: Launch Interactive Terminal Console (CLI Mode)
You can run the entire platform directly inside your terminal with interactive menus:
```bash
# Using Windows Batch Script (One-Click)
run-console.bat

# Or using Maven directly
mvn spring-boot:run "-Dspring-boot.run.arguments=--console"

# Or run the automated 5-step terminal demonstration
mvn spring-boot:run "-Dspring-boot.run.arguments=--demo"
```

### Step 5: Access Web Application

Open your browser to:
```text
http://localhost:8081 (or http://localhost:8080)
```
* **Maritime Command Center (Dashboard):** `http://localhost:8081/dashboard`
* **Charter Analysis Engine:** `http://localhost:8081/charter`
* **Decision History & Audit Ledger:** `http://localhost:8081/history`
* **Operational Early-Warning Alerts:** `http://localhost:8081/alerts`
* **Freight Forecast Analytics:** `http://localhost:8081/forecast`
* **What-If Scenario Simulator:** `http://localhost:8081/simulation`
* **Port Compatibility Matrix:** `http://localhost:8081/ports`
* **Vessel Class Fleet Optimizer:** `http://localhost:8081/vessels`
* **Trade Corridors:** `http://localhost:8081/routes`
* **Historical Backtesting Engine:** `http://localhost:8081/backtest`
* **Procurement Copilot ("Ask NaviSense"):** `http://localhost:8081/copilot`
* **Observability Data Lab:** `http://localhost:8081/data-lab`
* **Platform Governance & Settings:** `http://localhost:8081/settings`
* **H2 Embedded Console:** `http://localhost:8081/h2-console` (JDBC URL: `jdbc:h2:mem:navisensedb`, User: `sa`, Password: *blank*)

---

## 6. Pre-Configured SIH Demo Scenarios

On the `/charter` page, click any of the 4 one-click demo scenario buttons:
1. **Scenario 1 (Benchmark):** Australia (Hay Point) ➔ Paradip | 150,000 tonnes Coking Coal | Budget ₹50 Cr ➔ Recommended: **Panamax** via Short-Term COA (Capesize rejected due to 16.5m draft constraint; ₹1.84 Cr logistics savings; within budget).
2. **Scenario 2:** Mozambique (Maputo) ➔ Dhamra | 80,000 tonnes Coking Coal | Budget ₹28 Cr ➔ Recommended: **Supramax** via Hybrid Contract.
3. **Scenario 3:** Indonesia (Tanjung Bara) ➔ Haldia | 55,000 tonnes Thermal Coal | Budget ₹15 Cr ➔ Recommended: **Handysize** (Haldia 10.5m draft boundary clears).
4. **Scenario 4:** USA (Hampton Roads) ➔ Visakhapatnam | 180,000 tonnes Hard Coking Coal | Budget ₹75 Cr ➔ Recommended: **Capesize** (Vizag deepwater 18.1m draft clears).

---

## 7. Project Documentation

Comprehensive documentation is provided in the `docs/` directory:
* [`docs/ARCHITECTURE.md`](docs/ARCHITECTURE.md): Structural component diagrams, service contracts, and layered pipeline flow.
* [`docs/ALGORITHM.md`](docs/ALGORITHM.md): Mathematical formulations, ensemble equations, vessel scoring, and risk weights.
* [`docs/API.md`](docs/API.md): Full REST API endpoint definitions, request/response JSON payloads, and status codes.
* [`docs/SIH_DEMO_SCRIPT.md`](docs/SIH_DEMO_SCRIPT.md): Complete 3–5 minute timed evaluator presentation narrative.
* [`docs/DATA_ASSUMPTIONS.md`](docs/DATA_ASSUMPTIONS.md): Data honesty declaration, representative proxies, and industrial transition steps.

---

## 8. License & Attribution

Developed for **Smart India Hackathon 2026 (SIH26006)** under the auspices of the **Ministry of Steel, Government of India** and **Steel Authority of India Limited (SAIL)**.
