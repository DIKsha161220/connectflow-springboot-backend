@echo off
title ConnectFlow - PostgreSQL Live Data Viewer
cd /d "%~dp0"
color 0b

echo ======================================================================
echo          CONNECTFLOW CRM - POSTGRESQL LIVE DATA VIEWER
echo ======================================================================
echo Database : connectflow_crm (PostgreSQL 18)
echo Table    : institutes
echo ======================================================================
echo.

set PGPASSWORD=root
"C:\Program Files\PostgreSQL\18\bin\psql.exe" -h localhost -p 5432 -U postgres -d connectflow_crm -c "SELECT institute_id AS ID, institute_name AS \"INSTITUTE NAME\", lead_status AS STATUS, priority AS PRIORITY, created_at AS \"CREATED DATE\" FROM institutes ORDER BY institute_id DESC;"

echo.
echo ======================================================================
echo   Direct Web Dashboard View: http://localhost:5173
echo ======================================================================
echo.
pause
