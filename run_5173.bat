@echo off
title ConnectFlow Frontend (5173)
echo ====================================================
echo   Starting ConnectFlow Frontend on Port 5173...
echo ====================================================
cd /d "D:\frontendd\crm-frontend"
echo.
echo [1/2] Opening Chrome to http://localhost:5173/ ...
start chrome "http://localhost:5173/"
echo.
echo [2/2] Launching Vite Dev Server...
call npm run dev
pause
