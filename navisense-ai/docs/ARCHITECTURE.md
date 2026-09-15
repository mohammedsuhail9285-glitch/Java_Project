# NAVISENSE AI — SYSTEM ARCHITECTURE

## SIH26006 — Smart India Hackathon 2026
**Organization:** Ministry of Steel | **Department:** Steel Authority of India Limited (SAIL)

---

## 1. High-Level Architectural Flow

NAVISENSE AI is architected as an offline-first, deterministic, high-throughput maritime decision support system built entirely in Java 21 and Spring Boot 3.3.4.

```text
+-------------------------------------------------------------------------------+
|                       PRESENTATION & CLIENT INTERFACE                         |
|   Bootstrap 5.3 UI   •   Chart.js 4.4 Visualizations   •   Printable Reports  |
+-------------------------------------------------------------------------------+
                                        │
                                        ▼ HTTP GET / POST
+-------------------------------------------------------------------------------+
|                           SPRING MVC CONTROLLER LAYER                         |
|   PageController (Thymeleaf Views)   •   CharterApiController (REST JSON API) |
+-------------------------------------------------------------------------------+
                                        │
                                        ▼ DTOs & Validation
+-------------------------------------------------------------------------------+
|                        CHARTER INTELLIGENCE ORCHESTRATOR                      |
|                           CharterIntelligenceService                          |
+-------------------------------------------------------------------------------+
         │                │                │                │               │
         ▼                ▼                ▼                ▼               ▼
+----------------+ +----------------+ +----------------+ +------------+ +-----------+
| FreightForecast| |PortCompatibilty| |VesselOptimize  | |VoyageCost  | |RiskAssess |
|    Service     | |    Service     | |    Service     | |  Service   | |  Service  |
+----------------+ +----------------+ +----------------+ +------------+ +-----------+
         │                │                │                │               │
         ▼                ▼                ▼                ▼               ▼
+-------------------------------------------------------------------------------+
|                     DOMAIN DATA REPOSITORIES & PROVIDERS                      |
|  FreightObservationRepository • PortRepository • VesselTypeRepository • Route |
|  MarketDataProvider • CongestionDataProvider • ExchangeRateProvider           |
+-------------------------------------------------------------------------------+
                                        │
                                        ▼ JPA / Hibernate
+-------------------------------------------------------------------------------+
|                        EMBEDDED DATABASE PERSISTENCE                          |
|                       H2 In-Memory Database Engine                            |
+-------------------------------------------------------------------------------+
```

---

## 2. Core Service Responsibilities

### 2.1 CharterIntelligenceService (Central Orchestrator)
Orchestrates the entire decision workflow:
1. Validates cargo input and looks up trade corridor distances.
2. Invokes `FreightForecastService` to forecast forward rate trajectories.
3. Invokes `PortCompatibilityService` to enforce physical constraints.
4. Invokes `VesselOptimizationService` to rank candidate vessels.
5. Invokes `VoyageCostService` to compute total multi-voyage delivered logistics costs.
6. Invokes `RiskAssessmentService` to calculate composite corridor risk.
7. Invokes `ContractStrategyService` to compare Spot vs COA vs Hybrid contracts.
8. Synthesizes a mathematically grounded "Why this recommendation?" explanation and factor contributions.
9. Generates a unique Decision Fingerprint (`NAV-26006-YYYYMMDD-SEQ`) and persists it for traceability.

### 2.2 FreightForecastService
Executes an ensemble forecasting algorithm (35% Exponential Smoothing, 30% Least-Squares Linear Trend Regression, 25% Weighted Moving Average, 10% Sinusoidal Seasonality) with volatility bands across 7, 14, 30, 60, and 90-day horizons.

### 2.3 PortCompatibilityService
Enforces draft, LOA, and beam checks against destination and origin loading ports. Rejects vessels with exact physical reasons.

### 2.4 VesselOptimizationService
Evaluates Handysize, Supramax, Panamax, and Capesize classes on utilization, cost per tonne, turnaround time, and relative CO₂ emissions.

### 2.5 VoyageCostService
Calculates sea transit days, bunker fuel consumption, port tariffs, and berth queue demurrage costs in USD and INR (Crores / Lakhs).

### 2.6 RiskAssessmentService
Generates a multi-factor risk score (0-100) combining market volatility, port congestion, weather channels, and laycan risks.

---

## 3. Technology Stack Justification

* **Java 21 LTS:** Strong type safety, high throughput, zero external language runtime dependency.
* **Spring Boot 3.3.4:** Production-ready framework with embedded Tomcat server, declarative validation, and transaction management.
* **H2 Database:** Zero-configuration embedded database allowing the evaluator to run the application immediately offline without installing MySQL or Postgres.
* **Thymeleaf & Vanilla JS:** Clean server-side templating ensuring complete compatibility without Node.js or complex frontend build toolchains.
