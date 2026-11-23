@echo off
title ConnectFlow CRM - Spring Boot Backend (Port 8080)
cd /d "%~dp0"
echo ========================================================
echo   ConnectFlow CRM - Spring Boot Backend & PostgreSQL
echo ========================================================
echo Database URL : jdbc:postgresql://localhost:5432/connectflow_crm
echo API Endpoint : http://localhost:8080/api
echo Web Dashboard: http://localhost:5173
echo ========================================================
echo.
echo Starting Spring Boot application on port 8080...
echo.
call mvnw.cmd spring-boot:run
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Backend failed to start.
    echo Please make sure PostgreSQL is running on port 5432 with database 'connectflow_crm'.
    echo.
)
pause
