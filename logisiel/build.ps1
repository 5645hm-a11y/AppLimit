#!/usr/bin/env pwsh
# AppLimit Grayscale Controller - Build Script (PowerShell)

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "AppLimit Grayscale Controller Builder" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if dotnet is installed
$dotnetCheck = & {
    try {
        $version = dotnet --version 2>&1
        return $version
    } catch {
        return $null
    }
}

if ($null -eq $dotnetCheck) {
    Write-Host "✗ ERROR: .NET SDK not found!" -ForegroundColor Red
    Write-Host "Please install .NET 6.0 or later from: https://dotnet.microsoft.com/download"
    exit 1
} else {
    Write-Host "✓ .NET SDK found: $dotnetCheck" -ForegroundColor Green
}


$projectPath = Join-Path $PSScriptRoot "GrayscaleDesktopApp.csproj"

Write-Host ""
Write-Host "[1/3] Restoring dependencies..." -ForegroundColor Yellow
dotnet restore $projectPath
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Restore failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "[2/3] Building Release version..." -ForegroundColor Yellow
dotnet build -c Release $projectPath
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed!" -ForegroundColor Red
    exit 1
}

Write-Host ""
Write-Host "✓ Build completed successfully!" -ForegroundColor Green
Write-Host ""
Write-Host "Built files location:" -ForegroundColor Cyan
Write-Host "  Debug:   bin\Debug\net6.0-windows\"
Write-Host "  Release: bin\Release\net6.0-windows\"
Write-Host ""

$response = Read-Host "Do you want to run the application now? (Y/N)"
if ($response -eq 'Y' -or $response -eq 'y') {
    Write-Host ""
    Write-Host "Running application..." -ForegroundColor Green
    dotnet run --no-build -c Release
}

Write-Host ""
Write-Host "Goodbye!" -ForegroundColor Green