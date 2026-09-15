@echo off
setlocal
cd /d "%~dp0"

javac Main.java
if %ERRORLEVEL% NEQ 0 (
    echo Compilation failed.
    pause
    exit /b
)

java Main %*

pause
endlocal
