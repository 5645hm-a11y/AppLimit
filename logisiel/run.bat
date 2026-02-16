@echo off
REM Quick launcher for AppLimit Grayscale Controller
REM This script builds and immediately runs the application

cd /d "%~dp0"

echo Checking for .NET...
dotnet --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: .NET 6.0+ not installed
    echo Download from: https://dotnet.microsoft.com/download
    pause
    exit /b 1
)

echo Building and launching AppLimit Grayscale Controller...
dotnet run -c Release --project GrayscaleDesktopApp.csproj

pause
