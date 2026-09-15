# NAVISENSE AI — DATA ASSUMPTIONS & METHODOLOGY DISCLOSURE

## SIH26006 — Smart India Hackathon 2026

---

## 1. Data Honesty & Representative Prototype Notice

In accordance with strict hackathon ethics and industrial software guidelines:

> **IMPORTANT:**
> All historical freight observations, port congestion scores, bunker fuel baseline prices, and synthetic market indices in this prototype are **deterministic representative demonstration data** generated with pseudo-random seed `26006L`.
>
> NaviSense AI does **NOT** fabricate claims of having live proprietary Baltic Exchange feeds, live paid AIS satellite tracking, or real-time internal SAIL SAP/ERP integration in this offline evaluation build.

---

## 2. Seeded Parameters & Realistic Maritime References

| Category | Parameter | Reference / Benchmark Value | Source Assumption |
|---|---|---|---|
| **Currency** | USD / INR Exchange Rate | ₹85.50 / USD | RBI Reference baseline |
| **Bunker Fuel** | Very Low Sulphur Fuel Oil (VLSFO) | $620.00 / Metric Tonne | Global bunker benchmark proxy |
| **Port Tariffs** | Handling & Berthing Tariff | $3.25 / Cargo Tonne | Average Major Indian Port Trust tariff proxy |
| **Demurrage** | Daily Vessel Hire / Demurrage | $18,500 / Day (Panamax) | Standard Baltic Panamax Time Charter equivalent |
| **Paradip Port** | Maximum Draft | 16.5 Meters | Paradip Port Authority published navigation guidelines |
| **Vizag Port** | Maximum Draft | 18.1 Meters | Visakhapatnam Port Authority Outer Harbour guidelines |
| **Gangavaram** | Maximum Draft | 19.5 Meters | Gangavaram Deepwater Port berthing guidelines |
| **Haldia Dock** | Maximum Draft | 10.5 Meters | Kolkata Port Trust riverine lock draft restrictions |
| **Hay Point** | Maximum Draft | 19.5 Meters | Dalrymple Bay / Hay Point Coal Terminal guidelines |

---

## 3. Transition to Production Deployment

For full production deployment at SAIL corporate procurement headquarters:
1. **Commercial Market Data:** Implement `MarketDataProvider` to connect directly to the **Baltic Exchange API** (BDI, BPI, BSI) or S&P Global Platts.
2. **AIS Satellite Tracking:** Implement `CongestionDataProvider` to stream real-time vessel queues and anchorage delays via MarineTraffic or Spire Maritime AIS feeds.
3. **Internal ERP Integration:** Ingest actual long-term Contracts of Affreightment (COA) terms and SAIL steel plant inventory consumption rates via SAP RFC / REST connectors.
