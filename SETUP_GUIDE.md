# Environment Setup & Deployment Guide

**Project:** RegTech Sentinel & Maritime Freight Forecasting System (Unified SIH26006 Platform)  
**Target Environment:** Windows 11 / Linux / macOS / Docker Containers  

---

## 1. System Prerequisites

| Component | Minimum Version | Recommended Version | Verification Command |
| :--- | :--- | :--- | :--- |
| **Java Development Kit (JDK)** | OpenJDK 21 LTS | OpenJDK 21 LTS | `java -version` |
| **Apache Maven** | 3.8+ | 3.9+ | `mvn -version` |
| **Node.js & npm** | Node 18+ | Node 20+ | `node -v` / `npm -v` |
| **Python** | 3.10+ | 3.11 - 3.14 | `python --version` |
| **Tesseract OCR** | 5.0+ | 5.5.0 | `tesseract --version` |
| **Git** | 2.30+ | Latest | `git --version` |

---

## 2. Directory Structure

```
d:\JAVA_PROJECT\
|-- backend\            # Spring Boot 3.3.4 (Java 21 LTS) Application
|   |-- src\main\       # Controllers, Services, Repositories, Entities, Migrations
|   |-- src\test\       # Unit and Integration Test Suites
|   `-- pom.xml         # Maven project descriptor
|-- frontend\           # React 18 + TypeScript + Vite + TailwindCSS
|   |-- src\            # Components, Pages, API Clients, Types
|   `-- package.json    # npm dependencies and build scripts
|-- ai-service\         # Python FastAPI AI / OCR & Freight Microservice
|   |-- app\            # FastAPI routes, OCR preprocessors, Freight models
|   `-- requirements.txt# Python pip dependencies
|-- data\               # Local H2 persistent database files
|-- storage\uploads\    # Sample packaging labels & uploaded evidence
|-- start_all.bat       # Master Windows launcher (starts all 3 tiers)
|-- stop_all.bat        # Master Windows shutdown script
`-- test_system.ps1     # Automated end-to-end verification script
```

---

## 3. Quick Local Setup (Development Mode)

### Step 1: Python AI Microservice Setup
```powershell
cd d:\JAVA_PROJECT\ai-service
python -m venv venv
.\venv\Scripts\Activate.ps1
pip install -r requirements.txt
# Verify FastAPI loads
uvicorn app.main:app --host 0.0.0.0 --port 8000
```

### Step 2: Spring Boot Backend Setup
Ensure `JAVA_HOME` points to JDK 21 LTS:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
cd d:\JAVA_PROJECT\backend
mvn clean compile
```

### Step 3: Frontend Web Portal Setup
```powershell
cd d:\JAVA_PROJECT\frontend
npm install
npm run build
```

### Step 4: Launch All Services Simultaneously
From the project root:
```powershell
.\start_all.bat
```
This opens 3 distinct terminal windows running:
1. Python FastAPI on `http://localhost:8000`
2. Spring Boot on `http://localhost:8080`
3. React Vite dev server on `http://localhost:3000`

---

## 4. Environment Variables Reference

| Variable Name | Default Value | Description |
| :--- | :--- | :--- |
| `PORT` | `8080` | Spring Boot HTTP port |
| `SPRING_PROFILES_ACTIVE` | `dev` | Active Spring profile (`dev`, `docker`, `prod`) |
| `JWT_SECRET` | *(64-character dev key)* | Secret for signing JWT tokens (override in prod) |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000,http://localhost:5173` | Trusted CORS origins |
| `AI_SERVICE_URL` | `http://localhost:8000` | URL of the Python AI microservice |
| `SPRING_DATASOURCE_URL` | `jdbc:h2:file:./data/lmchecker;...` | Database connection JDBC string |
| `POSTGRES_USER` | `postgres` | PostgreSQL username (for Docker/Prod) |
| `POSTGRES_PASSWORD` | `postgres` | PostgreSQL password (for Docker/Prod) |

---

## 5. Docker Deployment

To run the entire system in isolated containers with PostgreSQL:
```powershell
docker-compose up --build -d
```
Services exposed:
- Web Portal: `http://localhost:3000`
- REST API: `http://localhost:8080`
- AI Microservice: `http://localhost:8000`
- PostgreSQL: `localhost:5432`

---

## 6. Verification & Automated Testing

Run the PowerShell verification suite:
```powershell
powershell -ExecutionPolicy Bypass -File .\test_system.ps1
```

Run Backend Unit & Integration Tests:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
cd d:\JAVA_PROJECT\backend
mvn test "-Dtest=RuleEngineTests"
```

Run OCR Computer Vision Accuracy Benchmark:
```powershell
cd d:\JAVA_PROJECT
python test-data\evaluate_ocr_accuracy.py
```
Expected output: **80/80 (100.0%)** field accuracy across baseline, blur, low contrast, noise, and 8° skew conditions.

