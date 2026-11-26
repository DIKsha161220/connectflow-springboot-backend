@echo off
chcp 65001 >nul
echo [ConnectFlow CRM] Synchronizing UI Files across all locations...
copy /y "ConnectFlow-Desktop.html" "src\main\resources\static\index.html"
copy /y "ConnectFlow-Desktop.html" "ConnectFlow-Desktop-App\ConnectFlow-Desktop.html"
copy /y "ConnectFlow-Desktop.html" "ConnectFlow-Desktop-App\app\index.html"
copy /y "ConnectFlow-Desktop.html" "target\classes\static\index.html"
echo.
echo [OK] All UI files synchronized with Real-time 2-Way Sync!
