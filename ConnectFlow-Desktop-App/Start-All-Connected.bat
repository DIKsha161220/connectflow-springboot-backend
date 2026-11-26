@echo off
title ConnectFlow CRM - Auto Launcher (Backend + Desktop App)
cd /d "%~dp0"

echo ======================================================================
echo          CONNECTFLOW CRM - LIVE SYSTEM INITIALIZATION
echo ======================================================================
echo 1. Connecting Spring Boot Backend to PostgreSQL (Port 5432)...
echo 2. Starting REST API on http://localhost:8080/api ...
echo 3. Launching Standalone Desktop Application...
echo 4. Synchronizing Live Data with Dashboard 5173 and PostgreSQL...
echo ======================================================================
echo.

:: Launch the Spring Boot backend in a dedicated background window
start "ConnectFlow CRM Backend Service" cmd /k "cd /d "%~dp0" && call Start-Backend.bat"

echo Backend initialization started in background window.
echo Waiting 4 seconds for services to warm up...
timeout /t 4 /nobreak >nul

:: Launch the Desktop App
echo Starting ConnectFlow CRM Desktop App...
start "" "%~dp0ConnectFlow-CRM.exe"

echo.
echo ======================================================================
echo  ConnectFlow CRM is now RUNNING!
echo  - Standalone Desktop App : Active Window
echo  - Spring Boot Backend    : Running on http://localhost:8080
echo  - PostgreSQL Database    : Connected (Auto-Saving all records)
echo  - Web Dashboard 5173     : Synchronized
echo ======================================================================
echo  Keep the Backend Service window open while working.
echo.
timeout /t 6
