@echo off
title ConnectFlow CRM Master Launcher
echo ====================================================
echo   Starting ConnectFlow CRM (Backend 8080 + Frontend 5173)
echo ====================================================
echo.
echo Freeing ports 8080 and 5173...
powershell -NoProfile -Command "Get-Process -Id (Get-NetTCPConnection -LocalPort 8080,5173 -State Listen -ErrorAction SilentlyContinue).OwningProcess -ErrorAction SilentlyContinue | Stop-Process -Force"
echo.
echo Syncing UI files...
copy /Y "D:\CRM-BACKEND12\ConnectFlow-Desktop.html" "D:\CRM-BACKEND12\src\main\resources\static\index.html" >nul
echo.
echo Launching Frontend on Port 5173 in a new window...
start "ConnectFlow Frontend 5173" cmd /k "cd /d D:\frontendd\crm-frontend && npm run dev"
echo.
echo Opening Chrome to both applications...
start chrome "http://localhost:8080/" "http://localhost:5173/"
echo.
echo Starting Backend on Port 8080 in this window...
cd /d "D:\CRM-BACKEND12"
call .\mvnw.cmd spring-boot:run
pause
