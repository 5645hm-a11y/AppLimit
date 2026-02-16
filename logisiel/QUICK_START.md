# Quick Start Guide

## ⚡ 30-Second Setup

### 1. Prepare Your Android Device
- **Settings > About Phone** → Tap "Build Number" 7 times
- **Settings > Developer Options** → Enable "USB Debugging"
- Connect via USB cable to computer

### 2. Build & Run Desktop App
```powershell
cd C:\Users\HM\Documents\logisiel
.\build.ps1
```

### 3. Follow On-Screen Steps
- Step 1: Confirm debugging is enabled
- Step 2: Connect device (auto-detected)
- Step 3: Click "Start" and wait for progress to reach 100%
- Step 4: Success! Your phone is now in grayscale

---

## 📱 That's It!

Your Android device's display is now **grayscale (black & white)**.

### To Disable Grayscale Later
```powershell
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb shell settings put secure accessibility_display_daltonizer_enabled 0
```

---

## 🆘 Having Issues?

1. **Device not detected?**
   - Verify USB cable is working
   - Check "USB Debugging" is ON in Settings
   - Authorize the computer on device

2. **Build fails?**
   - Install .NET 6.0: https://dotnet.microsoft.com/download
   - Close Visual Studio if open
   - Run: `dotnet clean && dotnet restore`

3. **Still stuck?**
   - Check full guide: [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md)
   - Review [Troubleshooting](INSTALLATION_GUIDE.md#troubleshooting) section

---

## 📚 More Information

- **[Full Installation Guide](INSTALLATION_GUIDE.md)** - Detailed setup instructions
- **[Build Instructions](BUILD.md)** - Advanced build options
- **[README](README.md)** - Project overview and features

---

**Ready? Run `.\build.ps1` and get started!** 🚀
