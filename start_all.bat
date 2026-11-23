@echo off
title SIH26006 & RegTech Sentinel - Launcher
echo =================================================================
echo  SIH26006 & REGTECH SENTINEL UNIFIED PLATFORM
echo  Ministry of Steel (Maritime Freight) & Dept. of Consumer Affairs
echo =================================================================
echo.

cd /d "%~dp0"

:: Detect and configure OpenJDK 21 LTS if available
if exist "C:\Program Files\Java\jdk-21.0.10\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
    set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%"
    echo [Environment] Configured OpenJDK 21 LTS
)

echo [1/3] Starting AI & Freight Forecasting Microservice (Port 8000)...
start "SIH26006 AI Microservice [Port 8000]" cmd /k "cd /d "%~dp0ai-service" && venv\Scripts\python.exe -m uvicorn app.main:app --host 127.0.0.1 --port 8000"

timeout /t 3 /nobreak >nul

echo [2/3] Starting Spring Boot Enterprise Backend (Port 8080)...
start "SIH26006 Spring Boot Backend [Port 8080]" cmd /k "cd /d "%~dp0backend" && mvn spring-boot:run"

timeout /t 5 /nobreak >nul

echo [3/3] Starting Frontend Web Portal (Vite React on port 3000)...
start "SIH26006 Frontend Portal [Port 3000]" cmd /k "cd /d "%~dp0frontend" && npm run dev"

echo.
echo =================================================================
echo  All services have been initiated!
echo  Open your browser to: http://localhost:3000
echo  - Module 1 (Legal Metrology) : http://localhost:3000/dashboard
echo  - Module 2 (Maritime Freight): http://localhost:3000/freight
echo.
echo  Demo Credentials:
echo    - Regulator: regulator / Regulator@123
echo    - Admin:     admin / Admin@123
echo    - Analyst:   analyst / Analyst@123
echo    - Reviewer:  reviewer / Reviewer@123
echo =================================================================
pause
