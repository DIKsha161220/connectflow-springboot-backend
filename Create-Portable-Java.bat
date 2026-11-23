@echo off
setlocal enabledelayedexpansion
title ConnectFlow CRM - Step 2: Generate Portable Java Runtime (jlink)
echo ======================================================================
echo   CONNECTFLOW CRM - GENERATE PORTABLE JAVA RUNTIME (STEP 2)
echo ======================================================================
echo.

set "OUTPUT_DIR=%~dp0jre-min"

if exist "%OUTPUT_DIR%" (
    attrib -r -s -h "%OUTPUT_DIR%\*.*" /s /d >nul 2>&1
    rd /s /q "%OUTPUT_DIR%" >nul 2>&1
)

set "JLINK_EXE="

:: 1. Check JAVA_HOME
if defined JAVA_HOME (
    if exist "%JAVA_HOME%\bin\jlink.exe" (
        set "JLINK_EXE=%JAVA_HOME%\bin\jlink.exe"
    )
)

:: 2. Search in PATH
if not defined JLINK_EXE (
    where jlink.exe >nul 2>&1
    if %errorlevel% equ 0 (
        set "JLINK_EXE=jlink"
    )
)

:: 3. Common Java paths
if not defined JLINK_EXE (
    for /d %%d in ("C:\Program Files\Java\jdk*" "C:\Program Files\Eclipse Adoptium\jdk*" "C:\Program Files\Amazon Corretto\jdk*") do (
        if exist "%%d\bin\jlink.exe" set "JLINK_EXE=%%d\bin\jlink.exe"
    )
)

if not defined JLINK_EXE (
    echo [WARNING] jlink.exe could not be found automatically.
    echo Please make sure Java JDK 17+ is installed and JAVA_HOME is configured.
    echo.
    pause
    exit /b 1
)

echo [OK] Using jlink tool: %JLINK_EXE%
echo [INFO] Generating standalone stripped runtime in .\jre-min...
echo.

"%JLINK_EXE%" --add-modules java.base,java.desktop,java.logging,java.management,java.naming,java.net.http,java.security.jgss,java.sql,jdk.unsupported --output "%OUTPUT_DIR%" --strip-debug --no-man-pages --no-header-files --compress=2

if %errorlevel% equ 0 (
    echo.
    echo ======================================================================
    echo   [SUCCESS] Portable Java runtime generated in:
    echo   %OUTPUT_DIR%
    echo   Size is optimized and completely standalone!
    echo ======================================================================
) else (
    echo.
    echo [ERROR] jlink failed with error code %errorlevel%.
)

:done
echo.
if "%~1"=="--auto" goto end_script
pause
:end_script
