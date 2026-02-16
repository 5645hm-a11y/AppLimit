#!/usr/bin/env pwsh
Set-Location "c:\Users\HM\Documents\AppLimit\AppLimit"
Write-Host "Starting Debug APK Build..."
Write-Host "Command: .\gradlew.bat assembleDebug"
Write-Host ""

$process = Start-Process -FilePath ".\gradlew.bat" -ArgumentList "assembleDebug" -NoNewWindow -Wait -PassThru

Write-Host ""
Write-Host "Build exit code: $($process.ExitCode)"

if (Test-Path "app\build\outputs\apk\debug\app-debug.apk") {
    Write-Host "SUCCESS: APK created at app/build/outputs/apk/debug/app-debug.apk"
    Get-Item "app\build\outputs\apk\debug\app-debug.apk" | Select-Object FullName, Length, LastWriteTime
} else {
    Write-Host "ERROR: APK not found"
    Get-ChildItem "app\build\outputs\" -Recurse -Include "*.apk" -EA SilentlyContinue
}
