# AppLimit Flutter - Build and Run Script

Write-Host "==================================" -ForegroundColor Cyan
Write-Host "  AppLimit Desktop - Flutter App" -ForegroundColor Cyan
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

# Check if Flutter is installed
Write-Host "Checking Flutter installation..." -ForegroundColor Yellow
$flutterInstalled = Get-Command flutter -ErrorAction SilentlyContinue

if (-not $flutterInstalled) {
    Write-Host "❌ Flutter not found!" -ForegroundColor Red
    Write-Host ""
    Write-Host "Please install Flutter first:" -ForegroundColor Yellow
    Write-Host "1. Read INSTALL_FLUTTER.md for instructions" -ForegroundColor White
    Write-Host "2. Or visit: https://docs.flutter.dev/get-started/install/windows" -ForegroundColor White
    Write-Host ""
    Read-Host "Press Enter to exit"
    exit 1
}

Write-Host "✅ Flutter found!" -ForegroundColor Green
Write-Host ""

# Run flutter doctor
Write-Host "Running Flutter doctor..." -ForegroundColor Yellow
flutter doctor

Write-Host ""
Write-Host "==================================" -ForegroundColor Cyan
Write-Host "Choose an option:" -ForegroundColor Cyan
Write-Host "1. Install dependencies (flutter pub get)" -ForegroundColor White
Write-Host "2. Run in debug mode" -ForegroundColor White
Write-Host "3. Build release EXE" -ForegroundColor White
Write-Host "4. Clean project" -ForegroundColor White
Write-Host "5. Exit" -ForegroundColor White
Write-Host "==================================" -ForegroundColor Cyan
Write-Host ""

$choice = Read-Host "Enter your choice (1-5)"

switch ($choice) {
    "1" {
        Write-Host ""
        Write-Host "Installing dependencies..." -ForegroundColor Yellow
        flutter pub get
        Write-Host ""
        Write-Host "✅ Dependencies installed!" -ForegroundColor Green
    }
    "2" {
        Write-Host ""
        Write-Host "Enabling Windows desktop..." -ForegroundColor Yellow
        flutter config --enable-windows-desktop
        
        Write-Host "Installing dependencies..." -ForegroundColor Yellow
        flutter pub get
        
        Write-Host ""
        Write-Host "Starting app in debug mode..." -ForegroundColor Yellow
        flutter run -d windows
    }
    "3" {
        Write-Host ""
        Write-Host "Installing dependencies..." -ForegroundColor Yellow
        flutter pub get
        
        Write-Host "Building release EXE..." -ForegroundColor Yellow
        flutter build windows --release
        
        Write-Host ""
        Write-Host "✅ Build complete!" -ForegroundColor Green
        Write-Host ""
        Write-Host "EXE location:" -ForegroundColor Cyan
        Write-Host "build\windows\runner\Release\applimit_desktop.exe" -ForegroundColor White
        
        # Ask if user wants to open the folder
        $openFolder = Read-Host "Open build folder? (Y/N)"
        if ($openFolder -eq "Y" -or $openFolder -eq "y") {
            explorer.exe "build\windows\runner\Release\"
        }
    }
    "4" {
        Write-Host ""
        Write-Host "Cleaning project..." -ForegroundColor Yellow
        flutter clean
        Write-Host ""
        Write-Host "✅ Project cleaned!" -ForegroundColor Green
    }
    "5" {
        Write-Host ""
        Write-Host "Goodbye! 👋" -ForegroundColor Cyan
        exit 0
    }
    default {
        Write-Host ""
        Write-Host "❌ Invalid choice!" -ForegroundColor Red
        exit 1
    }
}

Write-Host ""
Read-Host "Press Enter to exit"
