# Setup Script - Upload DNI Client Configuration
# Run this to initialize the backend with a DNI client config

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "DNI Client Configuration Setup" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Check if backend is running
Write-Host "[1/3] Checking backend..." -ForegroundColor Yellow
try {
    $health = Invoke-WebRequest -Uri "http://localhost:8080/q/health" -UseBasicParsing -ErrorAction Stop
    Write-Host "✓ Backend is running" -ForegroundColor Green
} catch {
    Write-Host "✗ Backend is not running!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please start the backend first:" -ForegroundColor Yellow
    Write-Host "  mvn quarkus:dev" -ForegroundColor Cyan
    Write-Host ""
    exit 1
}

# Upload DNI configuration
Write-Host "[2/3] Uploading DNI configuration..." -ForegroundColor Yellow
try {
    $configPath = "config/dni-client-config.json"
    if (-not (Test-Path $configPath)) {
        Write-Host "✗ Config file not found: $configPath" -ForegroundColor Red
        exit 1
    }

    $dniConfig = Get-Content $configPath -Raw
    $response = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/config" `
        -Method POST `
        -ContentType "application/json" `
        -Body $dniConfig `
        -UseBasicParsing

    Write-Host "✓ DNI configuration uploaded successfully" -ForegroundColor Green
} catch {
    Write-Host "✗ Failed to upload configuration" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
    exit 1
}

# Verify
Write-Host "[3/3] Verifying configuration..." -ForegroundColor Yellow
try {
    $configs = Invoke-WebRequest -Uri "http://localhost:8080/api/v1/config" -UseBasicParsing
    $configJson = $configs.Content | ConvertFrom-Json

    if ($configJson.PSObject.Properties.Count -gt 0) {
        Write-Host "✓ Found $($configJson.PSObject.Properties.Count) client configuration(s)" -ForegroundColor Green
        Write-Host ""
        Write-Host "Available clients:" -ForegroundColor Cyan
        foreach ($prop in $configJson.PSObject.Properties) {
            $client = $prop.Value
            Write-Host "  - $($client.clientName) ($($client.clientId))" -ForegroundColor White
        }
    } else {
        Write-Host "✗ No configurations found" -ForegroundColor Red
    }
} catch {
    Write-Host "✗ Failed to verify configuration" -ForegroundColor Red
    Write-Host "Error: $($_.Exception.Message)" -ForegroundColor Red
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Setup completed!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
Write-Host ""
Write-Host "Next steps:" -ForegroundColor Cyan
Write-Host "1. Start the frontend: cd frontend && npm start" -ForegroundColor White
Write-Host "2. Open http://localhost:4200" -ForegroundColor White
Write-Host "3. Select 'DNI - Dienst Niet Ingezetenen' from the dropdown" -ForegroundColor White
Write-Host "4. Upload an MTOM file and convert!" -ForegroundColor White
Write-Host ""
