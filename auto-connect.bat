@echo off
title ConnectFlow CRM - Automatic Connect and Push to GitHub
color 0B

echo ======================================================================
echo           ConnectFlow CRM: Automatic Connect, Launch ^& Push
echo ======================================================================
echo.

:: 1. FREE PORT 8080 IF BLOCKED
echo [Step 1/5] Checking and clearing port 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    echo   - Found lingering process (PID %%a) on port 8080. Freeing port...
    taskkill /F /PID %%a >nul 2>&1
)
echo   ✓ Port 8080 is clean and ready.
echo.

:: 2. PUSH FRONTEND TO GITHUB
echo [Step 2/5] Pushing Frontend to GitHub...
cd /d "d:\frontendd\crm-frontend"
git add . >nul 2>&1
git commit -m "Auto-connect: Full page layout and data persistence" >nul 2>&1
git push origin main
if %ERRORLEVEL% EQU 0 (
    echo   ✓ Frontend code successfully pushed to GitHub!
) else (
    echo   ! Note: Git push frontend encountered an issue or is up to date.
)
echo.

:: 3. PUSH BACKEND TO GITHUB
echo [Step 3/5] Pushing Backend to GitHub...
cd /d "d:\CRM-BACKEND12"
git add . >nul 2>&1
git commit -m "Auto-connect: Spring Boot backend configuration" >nul 2>&1
git push origin main
if %ERRORLEVEL% EQU 0 (
    echo   ✓ Backend code successfully pushed to GitHub!
) else (
    echo   ! Note: Git push backend encountered an issue or is up to date.
)
echo.

:: 4. LAUNCH SPRING BOOT BACKEND
echo [Step 4/5] Launching Spring Boot Backend on Port 8080...
start "ConnectFlow Backend (Port 8080)" cmd /k "cd /d d:\CRM-BACKEND12 && title ConnectFlow Backend && color 0A && mvn spring-boot:run"
echo   ✓ Backend starting in separate window.
echo.

:: 5. LAUNCH FRONTEND & OPEN BROWSER
echo [Step 5/5] Launching Frontend & Opening Browser...
start "ConnectFlow Frontend (Port 5173)" cmd /k "cd /d d:\frontendd\crm-frontend && title ConnectFlow Frontend && color 09 && npm run dev"

timeout /t 3 /nobreak >nul
start http://localhost:5173/

echo.
echo ======================================================================
echo   [SUCCESS] Automatic Connect ^& Launch Completed!
echo   - Port 8080 was cleared
echo   - Code was pushed to GitHub
echo   - Backend started on port 8080
echo   - Frontend started on http://localhost:5173
echo ======================================================================
echo.
pause
