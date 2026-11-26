@echo off
title ConnectFlow Backend (Port 8080)
cd /d "D:\CRM-BACKEND12"
echo =======================================================
echo   CONNECTFLOW CRM - BACKEND AUTO LAUNCHER
echo =======================================================
echo.
echo [1/3] Syncing Desktop UI files to Backend static...
copy /Y "D:\CRM-BACKEND12\ConnectFlow-Desktop.html" "D:\CRM-BACKEND12\src\main\resources\static\index.html" >nul 2>&1
echo       UI synced!
echo.
echo [2/3] Freeing Port 8080 (Killing old instances)...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do taskkill /f /pid %%a >nul 2>&1
timeout /t 1 /nobreak >nul
echo       Port 8080 is clear!
echo.
echo [3/3] Starting Spring Boot Server on http://localhost:8080 ...
call .\mvnw.cmd spring-boot:run
pause
