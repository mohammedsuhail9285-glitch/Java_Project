# RegTech Sentinel / SIH26006 Project Audit & Requirement Verification Report

**Date:** 2026-11-20  
**Audit Conducted By:** Principal Software Architect & QA Automation Engineer  
**Project Workspace:** `D:\JAVA_PROJECT`  
**Current Tech Stack:** Java 21/25 (Spring Boot 3.3.4), React 18 + TypeScript + Vite, Python 3.14 (FastAPI + OpenCV + Tesseract OCR), H2 / PostgreSQL (Flyway)

---

## 1. Executive Summary & Problem Statement Conflict Analysis

### 1.1 The Critical Domain Discrepancy
In **PART 1**, the user provided the official SIH problem statement:
> **"Development of an Intelligent Freight Forecasting Model for Optimized Vessel Chartering and Bulk Cargo Procurement from overseas to East Coast of India"**  
> *(Official SIH Problem Statement ID: SIH26006, Ministry of Steel)*

However, physical inspection of the repository (`D:\JAVA_PROJECT`) reveals:
- **Existing Project Identity:** **RegTech Sentinel** (`com.example.lmchecker`).
- **Domain:** Statutory Legal Metrology Compliance & Decision Support System under the **Legal Metrology (Packaged Commodities) Rules, 2011** and **Consumer Protection (E-Commerce) Rules, 2020** *(Department of Consumer Affairs, Government of India, typically SIH26034 / SIH1433)*.
- **Backend Core:** Packaging label inspection, mandatory declaration verification (MRP, Net Quantity, Manufacturer, Country of Origin, Mfg Date, Consumer Care), dual-pane physical label vs digital listing cross-referencing, OpenPDF statutory notices, and Apache POI XLSX audit workbooks.
- **AI Microservice:** Tesseract OCR image preprocessing, spatial bounding box extraction, and Legal Metrology declaration regex/NLP parsers.
- **Frontend SPA:** Dual-pane inspection workspace, priority review queues for Metrology Officers, rule configuration for LMPC rules LM001–LM010, and compliance telemetry.
- **Master Prompt (Part 2):** Every specific instruction in sections 3, 4, 5, 7, and 10 of Part 2 describes the exact classes, bugs, and features of the Legal Metrology application (e.g., `targetProducts.subList(0, 25)` in batch scanning, hardcoded Windows path in `tesseract_engine.py`, CORS wildcard in `SecurityConfig.java`, synthetic fallback OCR marking, dual-source comparison).

### 1.2 Mandatory Architectural Constraint
Per the Master Prompt:
> *"Do not start modifying files until you have inspected the project and verified the requirement mapping. Do not recreate the project from scratch."*  
> *"If you encounter a major architectural decision, explain the alternatives and ask me before making an irreversible change."*

Destructively deleting or overwriting the entire Legal Metrology codebase would violate the core rules to preserve working functionality. Therefore, this audit provides a complete **Requirement Traceability Matrix**, an **In-Depth Codebase Audit**, and presents the **Architectural Alternatives** for user confirmation before proceeding with irreversible domain modifications.

---

## 2. Requirement Traceability Matrix

| Requirement / Component | Existing Implementation | Current Status | Missing / Defective Functionality | Required Modification | Verification / Testing Method |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **SIH26006 Official: Freight Forecasting & Vessel Chartering (Ministry of Steel)** | None in repository. Entire codebase is Legal Metrology. | **Domain Mismatch** | No BDI/BCI/BPI freight rate index forecasting, vessel class optimization, or bulk cargo landed cost models. | Awaiting User Decision: Either adapt current platform architecture (Spring Boot + React + Python AI) into Freight Engine OR complete & harden Legal Metrology system. | Domain requirement verification with stakeholder. |
| **Statutory Declaration & Compliance Engine** | `com.example.lmchecker.compliance.*` (LM001–LM010 evaluators) | **Implemented & Functional** | Rule engine works, but test execution hits Windows Maven Surefire multi-drive path resolution bug. | Fix Maven Surefire configuration in `pom.xml` (`<useManifestOnlyJar>false</useManifestOnlyJar>`). | Run `mvn test -Dtest=RuleEngineTests`. |
| **Batch Processing & Catalog Ingestion** | `InMemoryJobQueueService.java` | **Defective (Known Risk)** | Line 119 silently caps batch execution to 25 items: `targetProducts = targetProducts.subList(0, 25);`. In-memory queue lacks crash resilience and progress persistence. | Remove arbitrary 25-item limit; process all requested items with paginated chunking, persistence in `scan_jobs`, and cancellation support. | Run batch scan with >30 products and assert all are processed. |
| **OCR & Label Extraction Pipeline** | `ai-service/app/ocr/` (`TesseractOCREngine`, `FallbackOCREngine`) | **Partially Functional** | 1. Python venv missing `opencv-python-headless` and `requests`.<br>2. Hardcoded path to `C:\Users\arshath\...`.<br>3. `FallbackOCREngine` returns synthetic mock text without explicit UI notice. | 1. Install missing packages in venv.<br>2. Make Tesseract resolution dynamic using `%LOCALAPPDATA%` and `%USERPROFILE%`.<br>3. Clearly label synthetic OCR fallback as `DEMO_SYNTHETIC`. | Execute `evaluate_ocr_accuracy.py` and call `POST /api/v1/ocr/extract`. |
| **API Security & CORS** | `SecurityConfig.java`, `application.yml` | **Vulnerable (Known Risk)** | `config.setAllowedOriginPatterns(List.of("*"))` paired with `setAllowCredentials(true)` violates security best practices and browser CORS policy. Default JWT secret hardcoded. | Bind CORS allowed origins to `app.cors.allowed-origins` configuration. Validate JWT secret strength and disallow default secret in production profile. | Test CORS headers with origin validation; test JWT creation and verification. |
| **Database & Schema Migrations** | Flyway V1–V4 migrations (`db/migration/`) | **Functional** | Migrations work on H2; PostgreSQL Docker container setup needs validation. | Verify compatibility of V1–V4 against PostgreSQL syntax and Docker Compose network. | Run Flyway migration against PostgreSQL in Docker. |
| **Frontend Enterprise UI** | React 18, Vite, Tailwind CSS, Lucide icons | **Builds Cleanly** | Production build succeeds (`tsc && vite build`). Some chunk sizes >500kB. Dual-pane inspection and review queue are implemented. | Code-split heavy routes via `React.lazy()` to optimize bundle size; ensure fallback OCR warning badge is visible. | Run `npm run build` and UI end-to-end user journeys. |

---

## 3. In-Depth Technical Audit Findings

### 3.1 Backend (Java Spring Boot 3.3.4)
1. **Windows Multi-Drive Maven Surefire Bug:**
   - **Issue:** When the project resides on drive `D:\JAVA_PROJECT` and Maven local repository is on `C:\Users\arshath\.m2\repository`, Maven Surefire fails to create a manifest JAR (`'other' has different root` / `Boot Manifest-JAR contains absolute paths`).
   - **Impact:** `mvn test` hangs or dumps streams when run without `-Dsurefire.useManifestOnlyJar=false`.
   - **Fix:** Add `<useManifestOnlyJar>false</useManifestOnlyJar>` to `maven-surefire-plugin` in `backend/pom.xml`.

2. **CORS Configuration Flaw (`SecurityConfig.java`):**
   - **Issue:** Line 107 has `config.setAllowedOriginPatterns(List.of("*"))` while Line 110 has `config.setAllowCredentials(true)`.
   - **Impact:** Permitting credentials (`Cookie`, `Authorization` header) with wildcard origin patterns is disallowed by modern browser security standards and allows unauthorized cross-origin requests.
   - **Fix:** Inject `@Value("${app.cors.allowed-origins}")` and configure explicit trusted origins (`http://localhost:3000`, `http://localhost:5173`, `http://127.0.0.1:3000`, `http://127.0.0.1:5173`).

3. **Hardcoded JWT Default Secret (`application.yml`):**
   - **Issue:** `app.jwt.secret` defaults to a known static hexadecimal string (`404E6352...`).
   - **Impact:** Any attacker with knowledge of the default key can forge valid administrative JWTs.
   - **Fix:** Add runtime verification that logs a critical warning in development and prevents startup in production if `JWT_SECRET` is left at the default value.

4. **Batch Scanning Pipeline Limitation (`InMemoryJobQueueService.java`):**
   - **Issue:** Lines 119–121:
     ```java
     if (targetProducts.size() > 25) {
         targetProducts = targetProducts.subList(0, 25);
     }
     ```
   - **Impact:** Scanning a catalog of 200 products only processes 25, giving users a false impression of a full scan.
   - **Fix:** Remove the arbitrary subList truncation; process batches in configurable chunks with persistent progress updates in `ScanJob`.

### 3.2 AI Microservice (Python FastAPI & OCR)
1. **Missing Python Dependencies in `ai-service/venv`:**
   - **Issue:** Running `import cv2` throws `ModuleNotFoundError: No module named 'cv2'`. `requests` is also uninstalled.
   - **Impact:** Vision preprocessing and OpenCV image transformations fail at runtime.
   - **Fix:** Install `opencv-python-headless` and `requests` into the virtual environment.

2. **Tesseract Path Portability (`tesseract_engine.py`):**
   - **Issue:** Line 27 hardcodes `C:\Users\arshath\AppData\Local\Programs\Tesseract-OCR\tesseract.exe`.
   - **Impact:** Fails on any machine where the Windows username is not `arshath`.
   - **Fix:** Use `os.environ.get("LOCALAPPDATA")` and standard environment variables to construct candidate paths dynamically.

3. **Fallback OCR Transparency (`fallback_engine.py`):**
   - **Issue:** When Tesseract is unavailable, `FallbackOCREngine` produces synthetic text based on filenames (`compliant`, `missing_mrp`, etc.).
   - **Impact:** If not explicitly identified, users or automated checks may mistake synthetic fallback data for authentic computer vision extraction.
   - **Fix:** Mark all fallback results with `is_synthetic: true` and `engine: "DEMO_FALLBACK"` in the JSON response, and display a clear warning banner in the frontend.

### 3.3 Frontend (React 18 + TypeScript + Vite)
1. **Build Status:** Verified! `npm run build` completed successfully in 37.8s with zero TypeScript compilation errors.
2. **Bundle Optimization:** Chunks are ~913 kB. Implementing dynamic code-splitting via `React.lazy()` for heavy views (Analytics, Rules Management, Audit Logs) will reduce initial load time.
3. **API Alignment:** Endpoints in `src/api/*.ts` match Spring Boot endpoints on `/api/v1/`.

---

## 4. Architectural Decision Alternatives

Before making destructive modifications to the codebase, we present the three paths forward:

### Option 1: Harden & Perfect "RegTech Sentinel" (Legal Metrology DCA Application)
- **Scope:** Resolve all audited bugs: fix Maven surefire multi-drive issue, secure CORS, secure JWT secrets, remove batch 25-item cap, install missing python packages, make Tesseract portable, mark synthetic fallback OCR transparently, and verify all end-to-end tests.
- **Advantage:** Preserves 100% of the working architecture, satisfies all specific criteria in Part 2 of the master prompt, and yields a flawless, production-ready system for SIH evaluation.

### Option 2: Transform Codebase into SIH26006 Freight Forecasting Platform (Ministry of Steel)
- **Scope:** Retain the Spring Boot + React + Python architecture, but pivot the domain:
  - Backend entities become: `Vessel`, `CharterContract`, `CargoProcurement`, `PortCall`, `FreightIndexRate`.
  - Python AI microservice becomes: Time-series forecasting model (ARIMA / LSTM / XGBoost) for Baltic Dry sub-indices (Capesize BCI, Panamax BPI, Supramax BSI) and landed cost optimization for East Coast Indian ports (Paradip, Vizag, Haldia).
  - Frontend becomes: Maritime freight procurement dashboard with "Charter Now vs. Wait" decision support, landed cost calculators, and vessel class matchers.
- **Advantage:** Directly aligns with the literal text of Part 1 (SIH26006 Freight Forecasting).
- **Trade-off:** Completely replaces the existing Legal Metrology business logic.

### Option 3: Dual-Domain Enterprise Platform
- **Scope:** Keep RegTech Sentinel intact as Module 1, and add the Intelligent Freight Forecasting Engine as Module 2 under a unified government enterprise portal.

---

## 5. Recommended Immediate Implementation Plan
While awaiting user guidance on Option 1 vs Option 2:
1. **Fix non-breaking foundational infrastructure bugs immediately:**
   - Add `<useManifestOnlyJar>false</useManifestOnlyJar>` to `backend/pom.xml` so Maven unit tests run reliably on Windows.
   - Fix CORS and JWT in `SecurityConfig.java` and `application.yml`.
   - Fix batch capping in `InMemoryJobQueueService.java`.
   - Make Tesseract path discovery portable in `tesseract_engine.py`.
   - Install missing packages (`opencv-python-headless`, `requests`) in `ai-service/venv`.
2. **Execute and document fresh test runs across all tiers.**

