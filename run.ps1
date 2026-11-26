Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host "   CONNECTFLOW CRM - BACKEND AUTO LAUNCHER" -ForegroundColor Green
Write-Host "=======================================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "[1/2] Checking & Freeing Port 8080..." -ForegroundColor Yellow
$connections = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($connections) {
    foreach ($conn in $connections) {
        if ($conn.OwningProcess) {
            Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        }
    }
    Start-Sleep -Seconds 1
}
Write-Host "      Port 8080 is clear and ready!" -ForegroundColor Green
Write-Host ""

Write-Host "[2/2] Starting Spring Boot Server on http://localhost:8080 ..." -ForegroundColor Cyan
& .\mvnw.cmd spring-boot:run
