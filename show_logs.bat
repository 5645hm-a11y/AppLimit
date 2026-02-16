@echo off
setlocal enabledelayedexpansion
set "ADB_PATH=C:\Users\HM\AppData\Local\Android\Sdk\platform-tools\adb.exe"
REM Clear previous logs
"%ADB_PATH%" logcat -c
REM Show new logs
echo Capturing logs - Test the grace period feature on your device...
echo Press Ctrl+C to stop logging.
echo.
"%ADB_PATH%" logcat -v threadtime AppBlockedScreen:V *:S
pause
