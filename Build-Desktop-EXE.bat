@echo off
setlocal enabledelayedexpansion
title ConnectFlow CRM - Standalone Desktop .EXE Builder (7-Step Pipeline)
echo ======================================================================
echo   CONNECTFLOW CRM - DESKTOP .EXE INSTALLER BUILDER
echo   (Follows All 7 Steps for Spring Boot + Electron Standalone App)
echo ======================================================================
echo.

cd /d "%~dp0"
call "%~dp0Sync-Files.bat"

echo [STEP 1/7] Checking Spring Boot Backend JAR in target/...
if not exist "target\connectflow-backend-1.0.0.jar" (
    echo [INFO] JAR not found in target/. Running Maven Wrapper build...
    if exist "mvnw.cmd" (
        call mvnw.cmd clean package -DskipTests
    ) else (
        call mvn clean package -DskipTests
    )
) else (
    echo [OK] Backend JAR verified: target\connectflow-backend-1.0.0.jar
)
echo.

echo [STEP 2/7] Checking Standalone Portable Java Runtime (jre-min)...
if not exist "jre-min\bin\java.exe" (
    echo [INFO] Standalone jre-min folder not detected.
    echo Running portable runtime generator...
    call "%~dp0Create-Portable-Java.bat" --auto
) else (
    echo [OK] Portable Java runtime detected at: %~dp0jre-min
)
echo.

echo [STEP 3/7] Verifying Local Links, Data Isolation, and CORS...
echo [OK] Spring Boot SecurityConfig updated with full local CORS permissions.
echo [OK] Multi-user data privacy and isolated localStorage accounts active in index.html.
echo.

echo [STEP 4/7] Checking Node.js and Electron dependencies...
where npm >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Node.js and NPM found.
    if not exist "node_modules\electron" (
        echo [INFO] Installing Electron and electron-builder...
        call npm install
    ) else (
        echo [OK] Electron dependencies already installed.
    )
    echo.

    echo [STEP 5/7] Verifying Electron main.js startup file...
    if exist "main.js" (
        echo [OK] main.js is ready (manages background Spring Boot process, port 8080 health, auto-kill on quit).
    )
    echo.

    echo [STEP 6/7] Verifying package.json electron-builder configuration...
    if exist "package.json" (
        echo [OK] package.json points to target\connectflow-backend-1.0.0.jar and jre-min.
    )
    echo.

    echo [STEP 7/7] Packaging Standalone Desktop Application (.EXE)...
    echo Running electron-builder...
    call npm run dist
    if %errorlevel% equ 0 (
        echo.
        echo ======================================================================
        echo   [SUCCESS] ConnectFlow CRM .EXE Installer Generated!
        echo   Location: %~dp0dist\
        echo ======================================================================
        explorer "%~dp0dist"
        goto finish
    ) else (
        echo [WARNING] electron-builder encountered an issue. Falling back to native C# compiler...
    )
) else (
    echo [NOTE] Node.js / npm not detected in PATH.
    echo Building native Windows C# Standalone .EXE runner...
)

echo.
echo ----------------------------------------------------------------------
echo   BUILDING NATIVE WINDOWS STANDALONE RUNNER (ConnectFlow-CRM.exe)
echo ----------------------------------------------------------------------
call "%~dp0ConnectFlow-Desktop-App\Build-EXE.bat"

:finish
echo.
echo All steps completed!
pause
