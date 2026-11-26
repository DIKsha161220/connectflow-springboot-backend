@echo off
title ConnectFlow Backend (Port 8080)
cd /d "D:\CRM-BACKEND12"
echo =======================================================
echo   CONNECTFLOW CRM - BACKEND AUTO LAUNCHER
echo =======================================================
echo.
echo [1/3] Syncing Desktop UI files to Backend static...
copy /Y "D:\CRM-BACKEND12\ConnectFlow-Desktop.html" "D:\CRM-BACKEND12\src\main\resources\static\index.html" >nul
echo       UI synced!
echo.
echo [2/3] Freeing Port 8080 (Killing old instances)...
powershell -NoProfile -Command "Get-Process -Id (Get-NetTCPConnection -LocalPort 8080 -State Listen -ErrorAction SilentlyContinue).OwningProcess -ErrorAction SilentlyContinue | Stop-Process -Force"
echo       Port 8080 is clear!
echo.
echo [3/3] Starting Spring Boot Server on http://localhost:8080 ...
start chrome "http://localhost:8080/"
call .\mvnw.cmd spring-boot:run
pause
