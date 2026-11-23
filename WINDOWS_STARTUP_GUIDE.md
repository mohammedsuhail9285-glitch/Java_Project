# WINDOWS 10/11 STARTUP & EXECUTION GUIDE
## RegTech Sentinel — Quick-Start Manual for Developers & Hackathon Evaluators

This guide details how to start, run, verify, and shut down the complete RegTech Sentinel platform on a Windows 10 or Windows 11 machine.

---

## 1. System Requirements & Prerequisites

Ensure the following tools are installed on your Windows machine:
- **Operating System:** Windows 10 (Build 19041+) or Windows 11 (64-bit).
- **Java Development Kit:** OpenJDK 21 LTS (Oracle, Eclipse Temurin, or Microsoft OpenJDK).
- **Node.js & npm:** Node.js v18.0+ or v20.0+ LTS and npm 9+.
- **Python:** Python 3.10, 3.11, 3.12, 3.13, or 3.14 (with virtual environment capability).
- **Build Tool:** Apache Maven 3.8+ (or use the included Maven wrapper `mvnw.cmd`).
- **Web Browser:** Google Chrome, Microsoft Edge, or Mozilla Firefox.

### Quick Version Verification
Open PowerShell and run:
```powershell
java -version
javac -version
node -v
npm -v
python --version
mvn -version
```

---

## 2. One-Click Automated Startup (`start_all.bat`)

The easiest way to launch the entire multi-service stack is the automated batch script:

1. Open PowerShell or Command Prompt in the project root: `d:\JAVA_PROJECT`.
2. Run:
   ```cmd
   start_all.bat
   ```
3. The script performs the following actions:
   - Checks if ports `8080`, `8000`, and `3000` are free; terminates stale or orphaned processes.
   - Launches the **AI Microservice** (`FastAPI / Uvicorn`) in a dedicated terminal window on port `8000`.
   - Launches the **Spring Boot Backend** (`Java 21 / Maven`) in a dedicated terminal window on port `8080`.
   - Waits for the backend to become healthy (`/actuator/health`).
   - Launches the **React Frontend** (`Vite Dev Server`) in a dedicated terminal window on port `3000`.
   - Opens your default web browser to:
     `http://localhost:3000/login`

---

## 3. Manual Step-by-Step Startup

If you prefer starting each service individually in its own terminal window:

### Step 1: Start the AI / Forecasting Microservice (Port 8000)
Open **Terminal 1**:
```powershell
cd d:\JAVA_PROJECT\ai-service
.\venv\Scripts\activate
uvicorn app.main:app --host 127.0.0.1 --port 8000 --reload
```
*Verification:* Visit `http://localhost:8000/docs` in your browser to view the interactive FastAPI Swagger UI.

---

### Step 2: Start the Java Spring Boot Backend (Port 8080)
Open **Terminal 2**:
```powershell
cd d:\JAVA_PROJECT\backend
mvn spring-boot:run
```
*Alternative using packaged JAR:*
```powershell
java -jar target/lmchecker-1.0.0.jar
```
*Verification:* Open `http://localhost:8080/actuator/health` in your browser. It should return:
```json
{"status":"UP"}
```

---

### Step 3: Start the React Frontend Web Application (Port 3000)
Open **Terminal 3**:
```powershell
cd d:\JAVA_PROJECT\frontend
npm run dev
```
*Verification:* Open your browser to `http://localhost:3000`. The login screen will appear.

---

## 4. Logging In with Demo Credentials

Navigate to `http://localhost:3000/login`. You can log in using any of the seeded accounts:

| Role | Username | Password | Purpose & Capabilities |
| :--- | :--- | :--- | :--- |
| **Analyst** | `analyst` | `Analyst@123` | **Primary SIH26006 Evaluation**: Freight Forecasting, Vessel Optimization, Landed Cost Analysis. |
| **Admin** | `admin` | `Admin@123` | System administration, user management, role elevation, unlocking locked accounts. |
| **Regulator** | `regulator` | `Regulator@123` | Legal Metrology compliance inspection, statutory OCR scanning, and notice generation. |
| **Reviewer** | `reviewer` | `Reviewer@123` | Audit review and read-only compliance report verification. |

> [!TIP]
> You can also click the quick-fill **Demo Preset Buttons** directly beneath the login form to auto-fill credentials with a single click.

---

## 5. Running the Automated Test Suites on Windows

### 5.1 Backend Maven Tests (38 Tests)
```powershell
cd d:\JAVA_PROJECT\backend
mvn test
```
*Expected Result:*
`Tests run: 38, Failures: 0, Errors: 0, Skipped: 0`
`BUILD SUCCESS`

### 5.2 Frontend Production Build & Type Checking
```powershell
cd d:\JAVA_PROJECT\frontend
npm run build
```
*Expected Result:*
`✓ 2474 modules transformed.`
`dist/index.html ... built in ~87s` with 0 errors.

### 5.3 Python AI Freight Accuracy Evaluation
```powershell
cd d:\JAVA_PROJECT\ai-service
.\venv\Scripts\activate
python app\freight\evaluate_forecast_accuracy.py
```
*Expected Result:*
Evaluates 62 rolling holdout windows across 252 Baltic trading days:
- MAE: $0.513 / MT
- RMSE: $0.638 / MT
- MAPE: 3.38%
- Directional Accuracy: 66.13%
- 95% Confidence Interval Coverage: 100.0%

---

## 6. One-Click Teardown (`stop_all.bat`)

To cleanly shut down all services and free up the ports:
1. Open Command Prompt or PowerShell in `d:\JAVA_PROJECT`.
2. Run:
   ```cmd
   stop_all.bat
   ```
3. This terminates any active Node, Java, and Uvicorn processes holding ports `3000`, `8080`, and `8000`.

---

## 7. Windows Troubleshooting FAQ

### Problem 1: Port 8080, 8000, or 3000 is already in use
**Solution:** Find and kill the process holding the port:
```powershell
netstat -ano | findstr :8080
taskkill /F /PID <PID_FOUND>
```

### Problem 2: PowerShell Execution Policy prevents script activation
**Solution:** If running `.\venv\Scripts\activate` throws a permissions error:
```powershell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
```

### Problem 3: Java version mismatch
**Solution:** Ensure `JAVA_HOME` points to JDK 21:
```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-21.0.10"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
java -version
```
