# Flutter Installation Script for Windows
# Run this with: PowerShell -ExecutionPolicy Bypass -File install_flutter.ps1

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "   Flutter Installation Script" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Configuration
$flutterUrl = "https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.24.5-stable.zip"
$installPath = "C:\flutter"
$downloadPath = "$env:USERPROFILE\Downloads\flutter_sdk.zip"

Write-Host "This script will:" -ForegroundColor Yellow
Write-Host "1. Download Flutter SDK (~900MB)" -ForegroundColor White
Write-Host "2. Extract to C:\flutter" -ForegroundColor White
Write-Host "3. Add Flutter to your PATH" -ForegroundColor White
Write-Host ""

$confirm = Read-Host "Continue? (Y/N)"
if ($confirm -ne "Y" -and $confirm -ne "y") {
    Write-Host "Installation cancelled" -ForegroundColor Yellow
    exit 0
}

Write-Host ""
Write-Host "Step 1: Downloading Flutter SDK..." -ForegroundColor Cyan
Write-Host "This will take 5-15 minutes depending on your internet speed" -ForegroundColor Yellow
Write-Host "Download URL: $flutterUrl" -ForegroundColor Gray
Write-Host ""

try {
    # Download with progress bar
    $webClient = New-Object System.Net.WebClient
    $webClient.DownloadFile($flutterUrl, $downloadPath)
    
    Write-Host "✅ Download complete!" -ForegroundColor Green
    $fileSize = (Get-Item $downloadPath).Length / 1MB
    Write-Host "Downloaded: $([math]::Round($fileSize, 2)) MB" -ForegroundColor Gray
    Write-Host ""
    
} catch {
    Write-Host "❌ Download failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please download manually:" -ForegroundColor Yellow
    Write-Host "1. Visit: https://docs.flutter.dev/get-started/install/windows" -ForegroundColor White
    Write-Host "2. Click 'Download Flutter SDK'" -ForegroundColor White
    Write-Host "3. Extract to C:\flutter" -ForegroundColor White
    Write-Host "4. Add C:\flutter\bin to your PATH" -ForegroundColor White
    exit 1
}

Write-Host "Step 2: Extracting Flutter SDK..." -ForegroundColor Cyan

try {
    # Remove existing installation
    if (Test-Path $installPath) {
        Write-Host "Removing existing Flutter installation..." -ForegroundColor Yellow
        Remove-Item $installPath -Recurse -Force -ErrorAction SilentlyContinue
    }
    
    # Extract
    Write-Host "Extracting to C:\flutter (this may take a few minutes)..." -ForegroundColor Yellow
    Expand-Archive -Path $downloadPath -DestinationPath "C:\" -Force
    
    Write-Host "✅ Extraction complete!" -ForegroundColor Green
    Write-Host ""
    
    # Clean up
    Write-Host "Cleaning up download file..." -ForegroundColor Yellow
    Remove-Item $downloadPath -Force -ErrorAction SilentlyContinue
    
} catch {
    Write-Host "❌ Extraction failed: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "Please extract manually: $downloadPath to C:\" -ForegroundColor Yellow
    exit 1
}

Write-Host "Step 3: Adding Flutter to PATH..." -ForegroundColor Cyan

try {
    # Get current PATH
    $currentPath = [Environment]::GetEnvironmentVariable("Path", "User")
    
    # Add Flutter if not already there
    if ($currentPath -notlike "*flutter\bin*") {
        $newPath = "$currentPath;C:\flutter\bin"
        [Environment]::SetEnvironmentVariable("Path", $newPath, "User")
        Write-Host "✅ Added C:\flutter\bin to PATH" -ForegroundColor Green
        Write-Host ""
        Write-Host "⚠️  IMPORTANT: Close and reopen your terminal/PowerShell for PATH changes to take effect" -ForegroundColor Yellow
    } else {
        Write-Host "✅ Flutter already in PATH" -ForegroundColor Green
    }
    
} catch {
    Write-Host "⚠️  Could not update PATH automatically" -ForegroundColor Yellow
    Write-Host "Please add C:\flutter\bin to your PATH manually:" -ForegroundColor Yellow
    Write-Host "1. Search 'Environment Variables' in Windows" -ForegroundColor White
    Write-Host "2. Edit 'Path' under User Variables" -ForegroundColor White
    Write-Host "3. Add: C:\flutter\bin" -ForegroundColor White
}

Write-Host ""
Write-Host "Step 4: Testing Flutter installation..." -ForegroundColor Cyan
Write-Host ""

# Add to current session
$env:Path = "C:\flutter\bin;$env:Path"

# Test Flutter
if (Test-Path "C:\flutter\bin\flutter.bat") {
    Write-Host "Running 'flutter doctor'..." -ForegroundColor Yellow
    Write-Host ""
    & "C:\flutter\bin\flutter.bat" doctor
    
    Write-Host ""
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host "   Installation Complete! 🎉" -ForegroundColor Green
    Write-Host "==================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "Next steps:" -ForegroundColor Yellow
    Write-Host "1. Close and reopen your terminal" -ForegroundColor White
    Write-Host "2. Run: flutter doctor" -ForegroundColor White
    Write-Host "3. Fix any issues shown by flutter doctor" -ForegroundColor White
    Write-Host "4. Run your Flutter app with: flutter run -d windows" -ForegroundColor White
    Write-Host ""
    
} else {
    Write-Host "❌ Flutter installation incomplete" -ForegroundColor Red
    Write-Host "Please check C:\flutter directory" -ForegroundColor Yellow
}

Write-Host ""
Write-Host "Press Enter to exit..."
Read-Host
