# Build and Deploy Script for Frontend
# Run this on Windows to build locally and deploy to OpenShift

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "MTOM Frontend - Build & Deploy" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Step 1: Install dependencies
Write-Host "[1/5] Installing dependencies..." -ForegroundColor Yellow
npm install --legacy-peer-deps
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to install dependencies" -ForegroundColor Red
    exit 1
}

# Step 2: Build Angular app
Write-Host "[2/5] Building Angular application..." -ForegroundColor Yellow
npm run build -- --configuration production
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build Angular app" -ForegroundColor Red
    exit 1
}

# Step 3: Build Docker image
Write-Host "[3/5] Building Docker image..." -ForegroundColor Yellow
docker build -f Dockerfile.simple -t mtom-frontend:latest .
if ($LASTEXITCODE -ne 0) {
    Write-Host "Failed to build Docker image" -ForegroundColor Red
    exit 1
}

# Step 4: Tag for OpenShift registry
Write-Host "[4/5] Tagging image for OpenShift..." -ForegroundColor Yellow
docker tag mtom-frontend:latest default-route-openshift-image-registry.apps-crc.testing/mtom-parser/mtom-frontend:latest

# Step 5: Get instructions for manual push (requires OC login)
Write-Host "[5/5] Ready to push to OpenShift!" -ForegroundColor Yellow
Write-Host ""
Write-Host "To complete deployment, run these commands:" -ForegroundColor Green
Write-Host ""
Write-Host "  # Login to OpenShift" -ForegroundColor White
Write-Host "  oc login -u developer -p developer https://api.crc.testing:6443" -ForegroundColor Cyan
Write-Host ""
Write-Host "  # Get token and login to Docker registry" -ForegroundColor White
Write-Host "  `$token = oc whoami -t" -ForegroundColor Cyan
Write-Host "  docker login -u developer -p `$token default-route-openshift-image-registry.apps-crc.testing" -ForegroundColor Cyan
Write-Host ""
Write-Host "  # Push image" -ForegroundColor White
Write-Host "  docker push default-route-openshift-image-registry.apps-crc.testing/mtom-parser/mtom-frontend:latest" -ForegroundColor Cyan
Write-Host ""
Write-Host "  # Deploy in OpenShift (if not already deployed)" -ForegroundColor White
Write-Host "  oc new-app --image-stream=mtom-frontend --name=mtom-frontend" -ForegroundColor Cyan
Write-Host "  oc expose svc/mtom-frontend" -ForegroundColor Cyan
Write-Host ""
Write-Host "========================================" -ForegroundColor Green
Write-Host "Build completed successfully!" -ForegroundColor Green
Write-Host "========================================" -ForegroundColor Green
