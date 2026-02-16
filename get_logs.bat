@echo off
setlocal enabledelayedexpansion
set "ADB_PATH=C:\Users\HM\AppData\Local\Android\Sdk\platform-tools\adb.exe"
"%ADB_PATH%" logcat *:S AppBlockedScreen:V AppBlockerAccessibilityService:V -v threadtime
