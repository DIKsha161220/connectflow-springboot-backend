@echo off
title ConnectFlow CRM - Standalone Desktop System
cls
echo ====================================================================
echo                 ConnectFlow CRM - Desktop Launcher                  
echo ====================================================================
echo.
echo [1/2] Launching Local Desktop Interface in your browser...
start "" "%~dp0ConnectFlow-Desktop.html"

echo [2/2] Checking and starting ConnectFlow Backend Server...
cd /d "%~dp0"

REM If mvnw exists, run backend in background
if exist "%~dp0mvnw.cmd" (
    echo Starting Spring Boot Server on http://localhost:8080 ...
    call "%~dp0mvnw.cmd" spring-boot:run
) else (
    echo Standalone Offline Mode Active.
    pause
)
