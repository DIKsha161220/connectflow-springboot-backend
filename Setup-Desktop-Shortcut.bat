@echo off
title Creating ConnectFlow CRM Desktop Shortcut
cls
cd /d "%~dp0"
cscript //nologo Create-Shortcut.vbs
echo.
echo Desktop shortcut created! You can now launch ConnectFlow CRM directly from your Desktop.
echo.
pause
