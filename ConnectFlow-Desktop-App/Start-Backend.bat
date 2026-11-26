@echo off
title ConnectFlow CRM - Spring Boot Backend (Port 8080)

:: Detect correct directory with mvnw.cmd and pom.xml
set "BASE_DIR=%~dp0"
if not exist "%BASE_DIR%mvnw.cmd" (
    if exist "%BASE_DIR%..\mvnw.cmd" (
        set "BASE_DIR=%~dp0..\"
    )
)
cd /d "%BASE_DIR%"

echo ========================================================
echo   ConnectFlow CRM - Spring Boot Backend & PostgreSQL
echo ========================================================
echo Working Dir   : %BASE_DIR%
echo Database URL  : jdbc:postgresql://localhost:5432/connectflow_crm
echo API Endpoint  : http://localhost:8080/api
echo Web Dashboard : http://localhost:5173
echo ========================================================
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
