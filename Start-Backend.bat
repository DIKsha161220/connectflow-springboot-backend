@echo off
title ConnectFlow CRM - Spring Boot Backend (Port 8080)
cd /d "%~dp0"
echo ========================================================
echo   ConnectFlow CRM - Spring Boot Backend & PostgreSQL
echo ========================================================
echo Database URL  : jdbc:postgresql://localhost:5432/connectflow_crm
echo API Endpoint  : http://localhost:8080/api
echo Web Dashboard : http://localhost:5173
echo ========================================================
echo.

echo Freeing Port 8080 if currently occupied...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    taskkill /f /pid %%a >nul 2>&1
)
echo Port 8080 cleared!
echo.
echo Starting Spring Boot application on port 8080...
echo.

if exist "target\connectflow-backend-1.0.0.jar" (
    echo Launching pre-built JAR...
    java -jar target\connectflow-backend-1.0.0.jar --server.port=8080
) else (
    echo Building & starting via Maven Wrapper...
    call mvnw.cmd spring-boot:run
)

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] Backend failed to start.
    echo Please make sure PostgreSQL is running on port 5432 with database 'connectflow_crm'.
    echo.
)
pause
