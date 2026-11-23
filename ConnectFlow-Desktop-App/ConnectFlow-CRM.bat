@echo off
title ConnectFlow CRM Launcher
cd /d "%~dp0"

REM Launching in Native Standalone Desktop Window (Microsoft Word/Excel Style)
if exist "%ProgramFiles(x86)%\Microsoft\Edge\Application\msedge.exe" (
    start "" "%ProgramFiles(x86)%\Microsoft\Edge\Application\msedge.exe" --app="%~dp0app\index.html"
    exit
)

if exist "%ProgramFiles%\Microsoft\Edge\Application\msedge.exe" (
    start "" "%ProgramFiles%\Microsoft\Edge\Application\msedge.exe" --app="%~dp0app\index.html"
    exit
)

REM Fallback if Edge is not found in standard paths
start "" "%~dp0app\index.html"
exit
