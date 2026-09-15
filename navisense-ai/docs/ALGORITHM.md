# NAVISENSE AI — MATHEMATICAL & ALGORITHM SPECIFICATION

## SIH26006 — Smart India Hackathon 2026
**Theme:** Transportation & Logistics | **Target:** SAIL / Ministry of Steel

---

## 1. Freight Forecasting Model (Hybrid Ensemble)

NaviSense AI implements a transparent, deterministic hybrid time-series ensemble in Java. Unlike black-box neural networks, all parameters are mathematically explainable and verifiable.

### 1.1 Ensemble Formulation
For any forecast horizon $h \in \{7, 14, 30, 60, 90\}$ days:

$$\hat{Y}_{t+h} = w_1 S_t + w_2 T_{t+h} + w_3 WMA_{t} + w_4 C_{t+h}$$

Where the weights are centrally configured:
* **$w_1 = 0.35$ (35% Exponential Smoothing):** Captures short-term market momentum.
  $$S_t = \alpha Y_t + (1 - \alpha) S_{t-1} \quad (\alpha = 0.35)$$
* **$w_2 = 0.30$ (30% Least-Squares Linear Trend Regression):** Fits long-term freight drifts over the past 60 observations.
  $$m = \frac{n \sum (x y) - \sum x \sum y}{n \sum x^2 - (\sum x)^2}, \quad c = \bar{y} - m\bar{x}, \quad T_{t+h} = m(n+h) + c$$
* **$w_3 = 0.25$ (25% Weighted Moving Average):** Weighs recent 14-day observations linearly ($w_i = i$).
  $$WMA_t = \frac{\sum_{i=1}^{k} i \cdot Y_{t-k+i}}{\sum_{i=1}^{k} i}$$
* **$w_4 = 0.10$ (10% Sinusoidal Seasonality Adjustment):** Models annual trade cycles.
  $$C_{t+h} = Y_t \cdot \left[1 + 0.04 \cdot \sin\left(\frac{2\pi (h \bmod 365)}{365}\right)\right]$$

### 1.2 Confidence Intervals & Volatility
Annualized freight volatility $\sigma$ is computed from daily log returns:
$$r_t = \ln\left(\frac{Y_t}{Y_{t-1}}\right), \quad \sigma = \text{std}(r) \times \sqrt{252} \times 100\%$$

The 95% forecast confidence interval for horizon $h$ is:
$$\text{Lower Bound} = \max\left(5.0, \hat{Y}_{t+h} - 1.96 \cdot \frac{\sigma}{100} \cdot Y_t \cdot \sqrt{\frac{h}{30}}\right)$$
$$\text{Upper Bound} = \hat{Y}_{t+h} + 1.96 \cdot \frac{\sigma}{100} \cdot Y_t \cdot \sqrt{\frac{h}{30}}$$

---

## 2. Vessel Optimization & Ranking Scoring Formula

For every candidate vessel class $v \in \{\text{Handysize, Supramax, Panamax, Capesize}\}$:

### 2.1 Physical Compatibility Gate
$$\text{Compatible}(v, \text{Ports}) = (Draft_v \le MaxDraft) \land (LOA_v \le MaxLOA) \land (Beam_v \le MaxBeam)$$
If $\text{Compatible} = \text{false}$, $\text{Score} = 0$, status = $\text{REJECTED}$.

### 2.2 Composite Score for Compatible Vessels (0–100)
$$\text{Score}(v) = S_{\text{util}} + S_{\text{cost}} + S_{\text{time}} + S_{\text{scale}}$$
* **$S_{\text{util}}$ (35 pts max):** Capacity Utilization $=\left(\frac{\text{Cargo Tonnes}}{\text{Voyages} \times \text{Capacity}_v}\right) \times 35$
* **$S_{\text{cost}}$ (35 pts max):** Cost Competitiveness $=\max(5.0, 35.0 - (\text{CostPerTonneUsd} \times 0.35))$
* **$S_{\text{time}}$ (15 pts max):** Turnaround Efficiency $=\max(5.0, 15.0 - (\text{VoyageDays} \times 0.2))$
* **$S_{\text{scale}}$ (15 pts max):** Scale / Emissions Efficiency $=\left(\frac{100 - \text{CO}_2\text{Index}}{60}\right) \times 15$

---

## 3. Multi-Factor Risk Assessment Algorithm

Composite risk score $R \in [10, 99]$ is calculated as:
$$R = w_{\text{vol}} R_{\text{vol}} + w_{\text{cong}} R_{\text{cong}} + w_{\text{compat}} R_{\text{compat}} + w_{\text{route}} R_{\text{route}} + w_{\text{uncert}} R_{\text{uncert}} + w_{\text{idle}} R_{\text{idle}}$$

* **Volatility Risk ($w = 0.25$):** $R_{\text{vol}} = \min(100, (\sigma / 30) \times 100)$
* **Berth Congestion Risk ($w = 0.25$):** $R_{\text{cong}} = \text{Port Congestion Index}$
* **Compatibility Risk ($w = 0.15$):** $R_{\text{compat}} = \max(10, 100 - \text{UtilizationPct})$
* **Corridor Risk ($w = 0.15$):** $R_{\text{route}} = \text{TradeRoute.riskFactor}$
* **Horizon Uncertainty ($w = 0.10$):** $R_{\text{uncert}} = (h / 90) \times 80 + 20$
* **Idle Risk ($w = 0.10$):** $R_{\text{idle}} = (R_{\text{cong}} \times 0.6) + (\text{Utilization} < 70 ? 40 : 10)$

Risk Categories:
* **$0 - 35$:** LOW
* **$36 - 65$:** MEDIUM
* **$66 - 85$:** HIGH
* **$86 - 100$:** CRITICAL

---

## 4. Contract Strategy Evaluation Heuristic

* **Spot Charter:** Baseline market rate. Volatility exposure = HIGH, Flexibility = HIGH.
* **Short-Term Multi-Voyage Contract (COA):** Applies 4.5% negotiated volume discount with locked bunker escalation clause.
* **Medium-Term COA:** Applies 8.0% discount across annual volume commitments.
* **Dynamic Hybrid Contract:**
  $$\text{Split}_{\text{Fixed}} = \begin{cases} 
  70\% & \text{if } \sigma > 22\% \text{ or Trend = UPWARD} \\
  40\% & \text{if Trend = DOWNWARD} \\
  60\% & \text{otherwise (Balanced)}
  \end{cases}$$
  $$\text{Expected Cost} = (\text{Cost}_{\text{COA}} \times \text{Split}_{\text{Fixed}}) + (\text{Cost}_{\text{Spot}} \times (1 - \text{Split}_{\text{Fixed}}))$$

---

## 5. Smart Charter Window Algorithm

Evaluates future 30-day forecast points:
* If 14-day predicted rate exceeds current rate by $>3\%$, the window triggers immediately (**Day 2 to Day 7**) to hedge against rate inflation.
* If predicted rate declines by $>3\%$, the window shifts to **Day 12 to Day 20** to capture the market softening dip.
* Otherwise, sets a balanced procurement window (**Day 5 to Day 12**).
