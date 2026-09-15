# NAVISENSE AI — 3–5 MINUTE SIH JUDGE EVALUATION SCRIPT

## Problem Statement SIH26006 | Ministry of Steel / SAIL
**Theme:** Transportation & Logistics | **Target:** Steel Authority of India Limited (SAIL)

---

## ⏱️ Evaluation Presentation Timeline (3 to 5 Minutes)

### [00:00 – 00:45] Problem Context & Innovation Hook
> *"Good morning, respected judges. Steel Authority of India Limited (SAIL) imports 15 to 20 million tonnes of coking coal and raw materials annually from Australia, the United States, Mozambique, Russia, and Indonesia to blast furnaces across eastern India (Bhilai, Bokaro, Rourkela, Durgapur, Burnpur).*
> *Historically, vessel chartering has remained largely reactive—locking single-voyage spot fixtures at peak rates, suffering unexpected anchorage delays at East Coast ports, and paying substantial demurrage charges.*
> *We present **NAVISENSE AI** — a predictive, explainable, risk-aware maritime charter intelligence platform built in Java 21 and Spring Boot 3.3.4."*

---

### [00:45 – 01:30] Command Center & Executive Decision Card
*(Action: Open `http://localhost:8081/dashboard`)*
> *"Here is the **Maritime Command Center**. Within 3 seconds, a logistics executive can understand what is happening and what to do.*
> *At the top is our **Executive Decision Card**: for our primary benchmark corridor (Australia to Paradip), NaviSense prescribes **BOOK NOW** with a **Panamax** fleet under a **Short-Term Multi-Voyage Contract**, delivering **₹1.84 Crores** in logistics savings with an 88% confidence score.*
> *Below, executives monitor the Baltic Dry Index equivalent, 7-day and 30-day forecast curves, real-time berth queue levels, corridor risk scores, and live operational alerts.*
> *Our hybrid ensemble combines 35% exponential smoothing, 30% linear trend regression, 25% weighted moving average, and 10% seasonality, cutting error by over 50% against naive spot persistence."*

---

### [01:30 – 02:30] End-to-End Decision Workflow & Port Draft Safety
*(Action: Navigate to `/charter` ➔ Click `Scenario 1: Australia ➔ Paradip (150k t)` ➔ Note target budget ₹40 Cr ➔ Click `ANALYZE CHARTER`)*
> *"Let us execute a complete shipment procurement analysis.*
> *1. **The Decision:** NaviSense recommends **BOOK NOW** within the optimal laycan window of 15–20 September before projected rate increases.*
> *2. **Physical Port Safety Gate:** Notice our Vessel Class Matrix. **Capesize bulk carriers are strictly REJECTED** because their 18.2m laden draft exceeds Paradip's 16.5m maximum navigation draft limit. NaviSense mathematically prevents catastrophic vessel grounding and millions in demurrage penalties before any charter party contract is signed.*
> *3. **The Optimal Fleet:** **Panamax** is ranked #1 (Suitability Score: 92/100) providing 100% draft clearance and 95.2% deadweight capacity utilization across 2 voyages.*
> *4. **Financial Impact & Budget Compliance:** The projected total outlay of ₹38.90 Cr is comfortably within the ₹40.0 Cr budget, generating **₹1.84 Crores in net savings** vs volatile spot booking.*
> *5. **Explainability & Transparency:** Notice the 'Why this recommendation?' synthesis and the 'How was this calculated?' mathematical audit breakdown showing sea days, bunker fuel burn, stevedoring tariffs, and queue demurrage."*

---

### [02:30 – 03:15] Traceability Ledger & What-If Stress Simulator
*(Action: Open `/history` to show the decision ledger, then open `/simulation` and click `Recalculate Scenario Impact`)*
> *"Every analysis creates an immutable decision fingerprint (`NAV-26006-YYYYMMDD-SEQ`) saved in our **Decision History & Audit Trail** for complete procurement governance.*
> *Now let us test market volatility in the **What-If Scenario Simulator**. We simulate a +10% freight inflation and a +25% port congestion spike at Paradip. NaviSense immediately recalculates the ₹3.44 Cr budget variance and prescribes shifting to short-term COA hedges."*

---

### [03:15 – 04:00] Ask NaviSense Copilot & 12-Month Backtest
*(Action: Open `/copilot` and submit `What vessel should I use for 100,000 tonnes from Australia to Paradip?`, then open `/backtest`)*
> *"Our offline **Procurement Copilot ('Ask NaviSense')** uses dynamic Java regex extraction to parse tonnage, origin, and destination, running our actual logistics algorithms on the fly without external cloud AI dependencies.*
> *Finally, our **Historical Backtesting Engine** validates that over 12 months of simulated voyages, NaviSense achieved a 100% outperformance hit ratio and **₹24.28 Crores in cumulative logistics savings**.*
> *NAVISENSE AI transforms SAIL from reactive chartering to predictive procurement. Thank you!"*

