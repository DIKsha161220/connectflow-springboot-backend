@echo off
title Compiling ConnectFlow-CRM.exe
cd /d "%~dp0"

echo Finding Windows .NET C# Compiler (csc.exe)...
set CSC="C:\Windows\Microsoft.NET\Framework64\v4.0.30319\csc.exe"
if not exist %CSC% set CSC="C:\Windows\Microsoft.NET\Framework\v4.0.30319\csc.exe"

if not exist %CSC% (
    echo Error: .NET Framework compiler not found!
    pause
    exit /b 1
)

echo Compiling Launcher.cs into ConnectFlow-CRM.exe ...
%CSC% /nologo /target:winexe /out:"ConnectFlow-CRM.exe" ConnectFlow-Desktop-App\Launcher.cs
copy /y "ConnectFlow-CRM.exe" "ConnectFlow-Desktop-App\ConnectFlow-CRM.exe" >nul

if exist "ConnectFlow-CRM.exe" (
    echo.
    echo ========================================================
    echo SUCCESS: ConnectFlow-CRM.exe has been created!
    echo Location: %~dp0ConnectFlow-CRM.exe
    echo ========================================================
    echo.
) else (
    echo Compilation failed.
)
pause
