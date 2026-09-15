# NAVISENSE AI — REST API DOCUMENTATION

Base URL: `http://localhost:8081/api`

---

## 1. Endpoints Overview

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/dashboard/summary` | Returns enterprise KPIs, command center status, alerts, and route heatmap |
| `GET` | `/api/freight/forecast` | Returns 7, 14, 30, 60, 90d forecasts, confidence bands, and accuracy metrics |
| `GET` | `/api/freight/history` | Returns historical daily freight observations |
| `GET` | `/api/ports` | Returns digital port matrix (draft, LOA, beam, congestion) |
| `GET` | `/api/vessels` | Returns vessel class specifications and cost factors |
| `GET` | `/api/routes` | Returns trade route distances and risk factors |
| `POST` | `/api/charter/analyze` | Generates full charter decision, vessel ranking, budget check, and cost breakdown |
| `POST` | `/api/simulation` | Runs What-If stress test with parameter deltas |
| `GET` | `/api/backtest` | Returns 12-month historical backtesting results and cumulative savings |
| `GET` | `/api/alerts` | Returns live active operational alerts |
| `GET` | `/api/history` | Returns all traceable charter decisions with optional keyword filter |
| `POST` | `/api/copilot` | Queries the offline rule-based procurement copilot with dynamic calculation |

---

## 2. Request & Response Examples

### 2.1 POST `/api/charter/analyze`
**Request Body:**
```json
{
  "cargoType": "Coking Coal",
  "quantityTonnes": 150000.0,
  "originCountry": "Australia",
  "loadingPort": "Hay Point",
  "destinationPort": "Paradip",
  "shipmentDate": "2026-09-25",
  "contractDurationMonths": 3,
  "preferredVessel": "Panamax"
}
```

**Response (200 OK):**
```json
{
  "fingerprint": "NAV-26006-20260911-001",
  "primaryDecision": "BOOK_NOW",
  "decisionBadgeClass": "badge-success",
  "recommendedVessel": "Panamax",
  "routeDescription": "Australia (Hay Point) → Paradip",
  "recommendedContract": "Short-Term Multi-Voyage Contract",
  "currentRateUsd": 24.50,
  "forecastRateUsd": 25.10,
  "totalCostInr": 389025000.00,
  "totalCostInrFormatted": "₹38.90 Cr",
  "potentialSavingsInr": 18400000.00,
  "potentialSavingsInrFormatted": "₹1.84 Cr",
  "savingsPercentage": 4.5,
  "decisionConfidence": 88,
  "riskLevel": "MEDIUM",
  "bestCharterWindow": "15 Sep 2026 – 20 Sep 2026",
  "portCompatibilityStatus": "PASSED",
  "voyageCount": 2,
  "capacityUtilizationPct": 100.0,
  "voyageDays": 16.2,
  "turnaroundHours": 32.0,
  "rationale": "Panamax is recommended because the selected cargo volume...",
  "positiveFactors": [...],
  "negativeFactors": [...],
  "vesselRankings": [...],
  "contractOptions": [...],
  "portAlternatives": [...]
}
```

---

### 2.2 POST `/api/copilot`
**Request Body:**
```json
{
  "query": "Why was Capesize rejected?"
}
```

**Response (200 OK):**
```json
{
  "question": "Why was Capesize rejected?",
  "answer": "Capesize vessels (typical draft 18.2m, LOA ~292m) exceed the maximum permitted draft limit of 16.5m at Paradip and 10.5m at Haldia...",
  "category": "PORT_COMPATIBILITY",
  "confidence": "98%"
}
```

---

## 3. Error Response Format
When validation fails or invalid data is provided:
```json
{
  "timestamp": "2026-09-11T12:00:00",
  "status": 400,
  "error": "Validation Failed",
  "details": {
    "quantityTonnes": "Cargo quantity must be at least 1,000 tonnes"
  }
}
```
