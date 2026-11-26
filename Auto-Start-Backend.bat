@echo off
title ConnectFlow CRM Auto-Starter
cd /d "%~dp0"

echo ===================================================
echo [1/3] Checking and clearing Port 8080...
echo ===================================================
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo Killing existing process on port 8080 (PID: %%a)...
    taskkill /f /pid %%a >nul 2>&1
)

echo Port 8080 is clear!
echo.
echo ===================================================
echo [2/3] Starting Spring Boot Backend...
echo ===================================================
call mvnw.cmd spring-boot:run

pause
