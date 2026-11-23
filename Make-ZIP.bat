@echo off
title Creating Zip Package
cd /d "%~dp0"
echo Synchronizing latest UI changes...
call Sync-Files.bat
echo Creating ConnectFlow-Desktop-App.zip ...
powershell -Command "Compress-Archive -Path '.\ConnectFlow-Desktop-App\*' -DestinationPath '.\ConnectFlow-Desktop-App.zip' -Force"
echo.
echo ========================================================
echo SUCCESS: ConnectFlow-Desktop-App.zip is ready!
echo Location: %~dp0ConnectFlow-Desktop-App.zip
echo ========================================================
echo.
pause