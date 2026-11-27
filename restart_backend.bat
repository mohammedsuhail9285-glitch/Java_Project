@echo off
title Restart Spring Boot Backend [Port 8080]
echo =================================================================
echo  Restarting Spring Boot Backend...
echo =================================================================

cd /d "%~dp0"

:: Set Java 21 LTS
if exist "C:\Program Files\Java\jdk-21.0.10\bin\java.exe" (
    set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10"
    set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%"
)

:: Free Port 8080
echo [1/3] Terminating any process on port 8080...
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr :8080 ^| findstr LISTENING') do (
    echo Releasing port 8080 (PID %%a)...
    taskkill /F /PID %%a >nul 2>&1
)

:: Clear stale database locks
echo [2/3] Checking database lock files...
del /f /q "%~dp0data\*.lock.db" >nul 2>&1
del /f /q "%~dp0backend\data\*.lock.db" >nul 2>&1

:: Start Spring Boot
echo [3/3] Starting Spring Boot Backend on port 8080...
if exist "%~dp0backend\target\lmchecker-1.0.0.jar" (
    start "SIH26006 Spring Boot Backend [Port 8080]" cmd /k "cd /d "%~dp0backend" && set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10" && set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%" && java -DDB_FILE_PATH="%~dp0data\lmchecker" -Dserver.port=8080 -jar target\lmchecker-1.0.0.jar"
) else (
    start "SIH26006 Spring Boot Backend [Port 8080]" cmd /k "cd /d "%~dp0backend" && set "JAVA_HOME=C:\Program Files\Java\jdk-21.0.10" && set "PATH=C:\Program Files\Java\jdk-21.0.10\bin;%PATH%" && mvn spring-boot:run"
)

echo.
echo =================================================================
echo  Spring Boot Backend launch initiated!
echo  Check the newly opened command window for startup progress.
echo  Once started, verify: http://localhost:8080/api/auth/config
echo =================================================================
timeout /t 5
