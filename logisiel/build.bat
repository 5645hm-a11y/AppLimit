@echo off
REM AppLimit Grayscale Controller - Build Script
REM This script builds and runs the desktop application

setlocal enabledelayedexpansion

echo.
echo =====================================
echo AppLimit Grayscale Controller Builder
echo =====================================
echo.

REM Check if dotnet is installed
dotnet --version >nul 2>&1
if errorlevel 1 (
    echo ERROR: .NET SDK not found! Please install .NET 6.0 or later.
    echo Download from: https://dotnet.microsoft.com/download
    pause
    exit /b 1
)

cd /d "%~dp0"

echo [1] Restore dependencies...
dotnet restore GrayscaleDesktopApp.csproj
if errorlevel 1 (
    echo ERROR: Restore failed!
    pause
    exit /b 1
)

echo.
echo [2] Building Release version...
dotnet build -c Release GrayscaleDesktopApp.csproj
if errorlevel 1 (
    echo ERROR: Build failed!
    pause
    exit /b 1
)

echo.
echo [3] Build completed successfully!
echo.
echo Built files location:
echo   Debug:   bin\Debug\net6.0-windows\
echo   Release: bin\Release\net6.0-windows\
echo.

choice /C YN /M "Do you want to run the application now?"
if errorlevel 2 goto :END
if errorlevel 1 (
    echo.
    echo Running application...
    dotnet run --no-build -c Release
    goto :END
)

:END
echo.
echo Goodbye!
pause
