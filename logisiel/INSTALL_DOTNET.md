# 🚨 .NET 6.0 SDK Installation Required

## Current Status
❌ .NET SDK not found on your system

## What You Need
The AppLimit Grayscale Desktop App requires `.NET 6.0 SDK` to build and run.

## Download & Installation (2 minutes)

### Step 1: Download .NET 6.0
👉 Go to: **https://dotnet.microsoft.com/download/dotnet/6.0**

You'll see this page with download links.

### Step 2: Download SDK Installer
Look for: **".NET 6.0 SDK"** (not Runtime)

Choose your Windows version:
- **Windows x64** (most common) ← Choose this
- Windows Arm64
- Windows x86

### Step 3: Run the Installer
1. Download the `.exe` file (~180MB)
2. Double-click to run
3. Follow prompts:
   - Accept license
   - Choose installation path (default is fine)
   - Wait for completion (~2 minutes)

### Step 4: Restart Command Prompt
Close and reopen PowerShell/Command Prompt

### Step 5: Verify Installation
```powershell
dotnet --version
```
Should show: `6.0.xxx` or higher

---

## Then Run the App

After .NET is installed:

```powershell
cd C:\Users\HM\Documents\logisiel
.\build.ps1
```

Or double-click `run.bat`

---

## Quick Links

🔗 **Direct Download Links:**

- **Windows x64 SDK**: https://aka.ms/dotnet/6.0/sdk-windows-x64-installer
- **Windows x86 SDK**: https://aka.ms/dotnet/6.0/sdk-windows-x86-installer

---

## Need Help?

- Installation guide: https://learn.microsoft.com/dotnet/core/install/windows
- System requirements: https://github.com/dotnet/core/blob/main/release-notes/6.0/supported-os.md

---

## After Installation

Run the app:
```powershell
.\build.ps1
```

Then follow the 4-step on-screen wizard to enable grayscale on your Android device!

---

**Estimated time: ~5 minutes (download + install + restart)**
