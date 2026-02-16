# SafeTimeGuard - Installation Guide

## ✅ App Build Status
- **Build**: ✅ Successful
- **APK**: `app/build/outputs/apk/debug/app-debug.apk` (20.3 MB)
- **API Level**: Android 15+ (API 35+)

---

## 📱 Installation Methods

### Option 1: Physical Device (Recommended)
1. **Enable USB Debugging** on your Android phone:
   - Settings > About Phone > tap Build Number 7 times
   - Settings > Developer Options > Enable USB Debugging
   
2. **Connect via USB cable** to your computer

3. **Run the app**:
   ```powershell
   cd c:\Users\MH\AndroidStudioProjects\AppLimit
   .\gradlew.bat installDebug
   ```

### Option 2: Android Emulator
1. **Open Android Studio**
2. **Create a virtual device**:
   - Device Manager > Create Device
   - Select: Pixel 5 or Pixel 6
   - System Image: Android 15 (API 35)
   - Click "Finish"

3. **Start the emulator** from Android Studio

4. **Install the app**:
   ```powershell
   cd c:\Users\MH\AndroidStudioProjects\AppLimit
   .\gradlew.bat installDebug
   ```

### Option 3: Manual APK Installation
1. Copy the APK file to your device:
   - File: `app\build\outputs\apk\debug\app-debug.apk`
   
2. Transfer to phone and tap to install

---

## 🚀 First Launch

### Initial Setup (Onboarding)
1. **Welcome Screen** - Overview of SafeTimeGuard
2. **PIN Setup** - Create a 6+ digit PIN
   - This PIN encrypts all app data
   - Required for accessing settings
3. **Language Selection** - Choose your preferred language
4. **Permissions** - Grant required permissions:
   - Query All Packages (to see installed apps)
   - Package Usage Stats (to monitor app usage)
   - Draw Over Other Apps (for blocking overlay)
   - Notifications (for alerts)

### Device Admin Activation
- When prompted, tap "Activate" to grant Device Admin access
- This allows SafeTimeGuard to control app launching

---

## 🔐 Security Features Implemented

✅ **Encrypted PIN Storage**
- AES-256-GCM encryption
- PBKDF2 hashing (10,000 iterations)
- 32-byte salt per PIN
- Anti-brute-force: 5 attempts → 5 min lockout

✅ **App Blocking System**
- Real-time app interception via AccessibilityService
- Beautiful blocking screen with animations
- Shows blocked app name and unlock time
- Prevents dismissal via back button

✅ **Material 3 Design**
- Professional blue color scheme
- Smooth animations and transitions
- Dark/Light theme support

---

## 📋 Features Ready

✅ Secure PIN onboarding
✅ App blocking overlay  
✅ AccessibilityService integration
✅ Professional dashboard
✅ Material 3 UI

---

## ⚠️ Troubleshooting

### "No connected devices"
```powershell
# Check connected devices
$env:ANDROID_HOME = "$env:USERPROFILE\AppData\Local\Android\Sdk"
$env:PATH = "$env:ANDROID_HOME\platform-tools;$env:PATH"
adb devices
```

### Device not recognized
- Restart ADB: `adb kill-server && adb start-server`
- Try a different USB cable
- Update USB drivers

### Build fails
```powershell
cd c:\Users\MH\AndroidStudioProjects\AppLimit
.\gradlew.bat clean
.\gradlew.bat assembleDebug
```

---

## 📞 Support
For questions or issues, check the build logs:
```powershell
.\gradlew.bat assembleDebug --stacktrace
```

---

**Generated**: January 23, 2026
**Project**: SafeTimeGuard - App Blocker with PIN Protection
