@echo off
title SIH26006 and RegTech Sentinel - Launcher
echo =================================================================
echo  SIH26006 and REGTECH SENTINEL UNIFIED PLATFORM
echo  Ministry of Steel (Maritime Freight) and Dept. of Consumer Affairs
echo =================================================================
echo.

cd /d "%~dp0"

:: 1. Detect and configure OpenJDK 21 LTS if available
if exist "C:\Program Files\Java\jdk-21.0.10\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
    set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%"
    echo [Environment] Configured OpenJDK 21 LTS
)

:: 2. Set unified persistent database path across all services
if not exist "%~dp0data" mkdir "%~dp0data"
if not exist "%~dp0storage\uploads" mkdir "%~dp0storage\uploads"
set "DB_FILE_PATH=%~dp0data\lmchecker"
del /f /q "%~dp0data\*.lock.db" >nul 2>&1
del /f /q "%~dp0backend\data\*.lock.db" >nul 2>&1

:: 3. Clear existing orphaned processes holding service ports
echo [Pre-flight] Verifying port availability...
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr :8000 ^| findstr LISTENING') do (
    echo [Pre-flight] Releasing port 8000 - PID %%a
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr :8080 ^| findstr LISTENING') do (
    echo [Pre-flight] Releasing port 8080 - PID %%a
    taskkill /F /PID %%a >nul 2>&1
)
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr :3000 ^| findstr LISTENING') do (
    echo [Pre-flight] Releasing port 3000 - PID %%a
    taskkill /F /PID %%a >nul 2>&1
)

:: 4. Start AI and Freight Forecasting Microservice (Port 8000)
echo.
echo [1/3] Starting AI and Freight Forecasting Microservice on port 8000...
start "SIH26006 AI Microservice [Port 8000]" cmd /k "cd /d "%~dp0ai-service" && venv\Scripts\python.exe -m uvicorn app.main:app --host 127.0.0.1 --port 8000"

timeout /t 3 /nobreak >nul

:: 5. Start Spring Boot Enterprise Backend (Port 8080)
echo [2/3] Starting Spring Boot Enterprise Backend on port 8080...
if exist "%~dp0backend\target\lmchecker-1.0.0.jar" (
    start "SIH26006 Spring Boot Backend [Port 8080]" cmd /k "cd /d "%~dp0backend" && set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10" && set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%" && set "DB_FILE_PATH=%~dp0data\lmchecker" && java -DDB_FILE_PATH="%~dp0data\lmchecker" -Dserver.port=8080 -jar target\lmchecker-1.0.0.jar"
) else (
    start "SIH26006 Spring Boot Backend [Port 8080]" cmd /k "cd /d "%~dp0backend" && set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10" && set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%" && set "DB_FILE_PATH=%~dp0data\lmchecker" && mvn spring-boot:run"
)

timeout /t 8 /nobreak >nul

:: 6. Start Frontend Web Portal (Vite React on port 3000)
echo [3/3] Starting Frontend Web Portal on port 3000...
start "SIH26006 Frontend Portal [Port 3000]" cmd /k "cd /d "%~dp0frontend" && npm run dev"

echo.
echo =================================================================
echo  All services have been initiated!
echo  Open your browser to: http://localhost:3000/login
echo.
echo  Primary Problem Statement (SIH26006):
echo    - Maritime Freight Intelligence: http://localhost:3000/freight
echo    - 14-Day Freight Forecasts     : http://localhost:3000/freight/forecasts
echo    - Vessel Optimization          : http://localhost:3000/freight/chartering
echo    - Landed Cost Calculation      : http://localhost:3000/freight/procurement
echo.
echo  Secondary Preserved Module:
echo    - Legal Metrology Compliance   : http://localhost:3000/dashboard
echo.
echo  Seeded Demo Credentials:
echo    - Analyst   : analyst / Analyst@123     (Primary Freight Evaluator)
echo    - Admin     : admin / Admin@123         (System Administrator)
echo    - Regulator : regulator / Regulator@123 (Legal Metrology Officer)
echo    - Reviewer  : reviewer / Reviewer@123   (Compliance Inspector)
echo =================================================================
pause
