@echo off
title Push Full CRM (Frontend + Backend) to GitHub
color 0B
echo ========================================================
echo     Pushing ConnectFlow CRM to GitHub
echo ========================================================
echo.

echo --------------------------------------------------------
echo 1. PUSHING FRONTEND (d:\frontendd\crm-frontend)...
echo --------------------------------------------------------
cd /d "d:\frontendd\crm-frontend"
git add .
git commit -m "Update CRM frontend: full page layout, date autofill, and data persistence"
git push origin main
echo Frontend push finished.

echo.
echo --------------------------------------------------------
echo 2. PUSHING BACKEND (d:\CRM-BACKEND12)...
echo --------------------------------------------------------
cd /d "d:\CRM-BACKEND12"
git add .
git commit -m "Update backend services and APIs"
git push origin main
echo Backend push finished.

echo.
echo ========================================================
echo   [DONE] All updates pushed to GitHub!
echo ========================================================
echo.
pause
