@echo off
title Free Port 8080
color 0C
echo ========================================================
echo        Checking and Freeing Port 8080...
echo ========================================================
echo.

set FOUND=0
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080 ^| findstr LISTENING') do (
    set FOUND=1
    echo [INFO] Found process with PID: %%a listening on port 8080.
    echo [INFO] Stopping process (taskkill /F /PID %%a)...
    taskkill /F /PID %%a
)

echo.
if "%FOUND%"=="1" (
    color 0A
    echo ========================================================
    echo   [SUCCESS] Port 8080 has been freed successfully!
    echo   You can now run: mvn spring-boot:run
    echo ========================================================
) else (
    color 0E
    echo ========================================================
    echo   No process was found listening on port 8080.
    echo ========================================================
)

echo.
pause
