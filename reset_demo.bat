@echo off
setlocal EnableDelayedExpansion

echo =================================================================
echo  REGTECH SENTINEL - DEMO DATABASE RESET UTILITY
echo =================================================================
echo.

echo [*] Resetting statutory database to clean 200-SKU baseline...

powershell -ExecutionPolicy Bypass -Command "& { try { $resp = Invoke-RestMethod -Uri 'http://localhost:8080/api/v1/system/reset-demo' -Method Post -TimeoutSec 15; Write-Host ('[SUCCESS] ' + $resp.message) -ForegroundColor Green; exit 0 } catch { Write-Host '  (Live service reset endpoint not reachable. Performing offline file cleanup...)' -ForegroundColor Yellow; exit 1 } }"

if %ERRORLEVEL% NEQ 0 (
    echo [*] Checking offline database files...
    if exist "backend\data\lmchecker.mv.db" (
        del /f /q "backend\data\lmchecker*" 2>nul
        echo [SUCCESS] Removed backend\data\lmchecker files.
    )
    if exist "data\lmchecker.mv.db" (
        del /f /q "data\lmchecker*" 2>nul
        echo [SUCCESS] Removed data\lmchecker files.
    )
    echo [INFO] Database will automatically re-seed on next backend startup.
)

echo.
echo =================================================================
echo  DEMO STATE RESET COMPLETE
echo  - ~200 Monitored SKUs: 55%% Compliant, 25%% Non-Compliant, 15%% Review, 5%% Pending
echo  - Rules LM001 to LM010 Active
echo  - Clean Ground-Truth State for SIH26006 Final Demo
echo =================================================================
echo.
