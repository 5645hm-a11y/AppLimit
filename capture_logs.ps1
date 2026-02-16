param(
    [int]$DurationSeconds = 120
)

$adbPath = "C:\Users\HM\AppData\Local\Android\Sdk\platform-tools\adb.exe"
$logFile = "C:\Users\HM\Documents\AppLimit\logs.txt"

Write-Output "Starting logcat capture for $DurationSeconds seconds..."
Write-Output "Logs will be saved to: $logFile"

# Start logcat in background
$process = Start-Process -FilePath $adbPath -ArgumentList @("logcat", "*:S", "AppBlockedScreen:V", "AppBlockerAccessibilityService:V", "-v", "threadtime") `
    -RedirectStandardOutput $logFile -NoNewWindow -PassThru

Write-Output "Logcat PID: $($process.Id)"
Write-Output "Now test the grace period feature on your device..."
Write-Output "Press Ctrl+C in 2 minutes to stop."

# Wait for duration
Start-Sleep -Seconds $DurationSeconds

# Stop logcat
Stop-Process -Id $process.Id -Force -ErrorAction SilentlyContinue

Write-Output "Logcat stopped. Check logs at: $logFile"
