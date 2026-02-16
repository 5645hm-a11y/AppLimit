# Installation & Setup Guide

## Desktop Application Setup

### 1. Build the Application

#### Using PowerShell (Recommended)
```powershell
cd C:\Users\HM\Documents\logisiel
.\build.ps1
```

#### Using Command Prompt
```cmd
cd C:\Users\HM\Documents\logisiel
build.bat
```

#### Using Visual Studio
1. Open `GrayscaleDesktopApp.csproj` in Visual Studio 2022
2. Press `Ctrl+Shift+B` to build
3. Press `F5` to run

---

## Android Device Preparation

### Step 1: Enable Developer Options

1. Open **Settings** on your Android device
2. Scroll down to **About Phone** (or **About Device**)
3. Find **Build Number** field
4. **Tap Build Number 7 times** rapidly
5. You should see: *"You are now a developer!"*
6. Go back to Settings

### Step 2: Enable USB Debugging

1. Go to **Settings > System > Developer Options** (or **Settings > Developer Options**)
2. Find and toggle **USB Debugging** → **ON**
3. A confirmation dialog may appear - tap **OK** to authorize
4. Keep your device connected via USB

### Step 3: Authorize Computer (if prompted)

1. When you connect the device to your computer for the first time via USB
2. A dialog on the Android device will appear asking: *"Allow USB debugging?"*
3. **Check the box** that says *"Always allow from this computer"*
4. Tap **Allow**

---

## Running the Desktop Application

### First Time Setup

1. **Launch the application**
   - Double-click `GrayscaleDesktopApp.exe`
   - Or run via `dotnet run` if building from source

2. **Splash Screen** (2.5 seconds)
   - AppLimit logo animates on screen
   - Building... message appears

### Step 1: Enable Device Debugging

- Read the on-screen instructions
- Follow the steps above on your Android device
- Click **"I've Enabled It"** once complete

### Step 2: Connect Device

- Connect your Android device via USB cable
- Application automatically detects connected device
- When detected, a green ✓ checkmark appears
- Click **"Continue"** to proceed

### Step 3: Enable Grayscale

- Click **"Start"** button
- Progress bar will animate from 0% to 100%
- Application executes ADB commands:
  ```
  adb shell settings put secure accessibility_display_daltonizer_enabled 1
  adb shell settings put secure accessibility_display_daltonizer 0
  ```

### Step 4: Success

- Green checkmark ✓ and "Success!" message appears
- Your Android device's screen is now in **grayscale mode**
- Click **"Done"** to close the application

---

## Verifying Grayscale Mode is Active

### On Android Device
- Your display should appear in **black and white**
- All colors have been removed
- Visual appearance: like a vintage TV or old photographs

### Checking via ADB (Command Line)
```powershell
adb shell settings get secure accessibility_display_daltonizer_enabled
# Output: 1 (enabled) or 0 (disabled)

adb shell settings get secure accessibility_display_daltonizer
# Output: 0 (grayscale) or 1-7 (color blindness modes)
```

---

## Disabling Grayscale Mode

### Option 1: Via Desktop Application (TBD)
- Future version will include "Disable" functionality

### Option 2: Manual ADB Command
```powershell
# Open PowerShell and run:
$adb = "$env:LOCALAPPDATA\Android\Sdk\platform-tools\adb.exe"
& $adb shell settings put secure accessibility_display_daltonizer_enabled 0
```

### Option 3: On Android Device Manually
1. **Settings > Accessibility > Display**
2. Toggle **Grayscale** or **Color correction** → **OFF**
3. Or toggle **Daltonizer** → **OFF**

---

## Troubleshooting

### Problem: "ADB path not found"
**Solution:**
- Ensure Android SDK Platform Tools are installed
- Expected location: `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`
- If not found, download from: [Android SDK Platform Tools](https://developer.android.com/studio/releases/platform-tools)

### Problem: "Device not detected"
**Solution:**
1. Check USB cable connection (try different cable)
2. Verify USB Debugging is **ON** in Device Settings
3. Disconnect and reconnect USB
4. Authorize the computer on device prompt
5. Restart application

### Problem: "Permission Denied" error in app
**Solution:**
1. Device may have denied ADB permission
2. Go to device **Settings > Developer Options**
3. Revoke USB debugging authorizations
4. Disconnect and reconnect
5. Authorize again when prompted

### Problem: "No device attached" in ADB
**Solution:**
1. Install/update Android USB drivers
2. Windows: Download drivers from device manufacturer
3. Test with: `adb devices` in PowerShell

### Problem: Grayscale mode doesn't activate
**Solution:**
1. Check device Android version (requires Android 5.0+)
2. Verify accessibility_display_daltonizer is supported
3. Restart device after executing commands
4. Try manual activation in **Settings > Accessibility**

---

## What Happens Behind the Scenes

The desktop application executes these ADB commands in sequence:

1. **Detect Device**
   ```
   adb devices
   ```

2. **Enable Grayscale (30% progress)**
   ```
   adb shell settings put secure accessibility_display_daltonizer_enabled 1
   ```

3. **Set Grayscale Type (60% progress)**
   ```
   adb shell settings put secure accessibility_display_daltonizer 0
   ```

4. **Complete (100% progress)**
   - Success message displayed
   - Device display mode updated

---

## Security Notes

- ✅ All commands are read-only or non-destructive
- ✅ No personal data is accessed
- ✅ No apps are installed or modified
- ✅ ADB requires explicit device authorization
- ✅ USB Debugging must be enabled (opt-in)

---

## System Requirements

| Component | Requirement |
|-----------|-------------|
| Operating System | Windows 10 or later (64-bit) |
| .NET Runtime | .NET 6.0 or later |
| Android Device | Android 5.0 (API 21) or later |
| USB Cable | Standard USB 2.0 or 3.0 |
| Internet | Required only for initial setup |

---

## Next Steps

- ✓ Device grayscale mode activated!
- ☐ Explore AppLimit Android app for scheduling
- ☐ Configure grayscale schedule in app
- ☐ Set up app blocking rules
- ☐ Configure PIN protection

---

## Support

For issues or questions:
- Check troubleshooting section above
- Review AppLimit Android app documentation
- Enable USB Debugging in desktop app (if available)

**Enjoy your grayscale experience!** 🎯

Last Updated: February 2026
