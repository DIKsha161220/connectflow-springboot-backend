@echo off
chcp 65001 >nul
echo [ConnectFlow CRM] Synchronizing UI Files across all locations...
copy /y "src\main\resources\static\index.html" "ConnectFlow-Desktop.html"
copy /y "src\main\resources\static\index.html" "ConnectFlow-Desktop-App\app\index.html"
copy /y "src\main\resources\static\index.html" "target\classes\static\index.html"
echo.
echo [OK] All UI files synchronized with 1-Click Login header!
