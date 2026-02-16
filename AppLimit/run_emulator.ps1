# SafeTimeGuard - הרצה בעמולטור
param([string]$emuName = "")
$ErrorActionPreference = "Stop"

Write-Host "=== SafeTimeGuard Setup ===" -ForegroundColor Yellow

$sdkPath = "C:\Users\MH\AppData\Local\Android\Sdk"
$adbPath = "$sdkPath\platform-tools\adb.exe"
$emulatorPath = "$sdkPath\emulator\emulator.exe"

# Check paths
if (-not (Test-Path $sdkPath)) {
    Write-Host "[ERROR] SDK not found" -ForegroundColor Red
    exit 1
}

Write-Host "[OK] SDK found" -ForegroundColor Green

# Get emulators
Write-Host "[*] Checking emulators..." -ForegroundColor Cyan
$emulators = @(& $emulatorPath -list-avds 2>$null)

if ($emulators.Count -eq 0) {
    Write-Host "[ERROR] No emulators" -ForegroundColor Red
    exit 1
}

Write-Host "[OK] Emulators found:" -ForegroundColor Green
$emulators | ForEach-Object { Write-Host "     $_" }

if ($emuName -eq "") { $emuName = $emulators[0] }

Write-Host "[*] Using: $emuName" -ForegroundColor Cyan
Write-Host "[*] Starting emulator..." -ForegroundColor Cyan
Start-Process -FilePath $emulatorPath -ArgumentList "-avd", $emuName, "-no-snapshot-load" -WindowStyle Hidden

Write-Host "[*] Waiting for boot..." -ForegroundColor Cyan
for ($i = 0; $i -lt 60; $i++) {
    try {
        $status = & $adbPath shell getprop sys.boot_completed 2>$null
        if ($status -eq "1") {
            Write-Host "[OK] Ready!" -ForegroundColor Green
            break
        }
    } catch { }
    Write-Host -NoNewline "."
    Start-Sleep -Seconds 2
}

Write-Host "`n[*] Building..." -ForegroundColor Cyan
cd "c:\Users\MH\AndroidStudioProjects\AppLimit"
& .\gradlew.bat installDebug

Write-Host "`n[*] Launching..." -ForegroundColor Cyan
& $adbPath shell am start -n com.example.safetimeguard/.MainActivity

Write-Host "[OK] Done!`n" -ForegroundColor Green
& $adbPath logcat SafeTimeGuard*:* *:E

