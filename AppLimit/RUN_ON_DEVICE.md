# 🚀 SafeTimeGuard - Run on Device Guide

## דרך 1: בנייה דרך Terminal (כלי מוביל)

### שלב 1: Navigate to Project
```bash
cd c:\Users\HM\Documents\AppLimit\AppLimit
```

### שלב 2: Build Debug APK
```bash
.\gradlew.bat assembleDebug
```

### שלב 3: Locate APK
קובץ ה-APK יהיה בנתיב:
```
app/build/outputs/apk/debug/app-debug.apk
```

### שלב 4: Install on Connected Device
```bash
.\gradlew.bat installDebug
```

### שלב 5: Run App
```bash
.\gradlew.bat runDebug
```

---

## דרך 2: בנייה דרך Android Studio (אם יש)

1. **Open Project** - `c:\Users\HM\Documents\AppLimit\AppLimit`
2. **Build > Make Project** (Ctrl + F9)
3. **Run > Run 'app'** (Shift + F10)
4. בחר device/emulator

---

## דרך 3: Installation Manual

### אם APK כבר בנוי:

```bash
# הנח את ה-APK בנתיב
adb install app\build\outputs\apk\debug\app-debug.apk

# או
adb install-multiple app\build\outputs\apk\debug\app-debug.apk
```

### אם צריך Uninstall תחילה:

```bash
adb uninstall com.example.safetimeguard
```

---

## 🔧 Troubleshooting

### Issue: "No connected devices"
```bash
# בדוק devices
adb devices

# אם Device מופיע כ- offline:
adb kill-server
adb start-server
adb devices
```

### Issue: "Permission denied"
```bash
# אם צריך elevated permissions:
adb root
```

### Issue: Build Failed
```bash
# נקה ובנה שוב
.\gradlew.bat clean assembleDebug
```

---

## 📱 Expected App Features

After installation, you should see:
- ✅ SafeTimeGuard splash screen
- ✅ Dashboard with Material 3 design
- ✅ App blocking rules
- ✅ Grayscale schedule
- ✅ Settings screen
- ✅ Material You colors (blue, purple, cyan)

---

## 📝 Next Steps After Running

1. **Set PIN** - Setup security PIN in Settings
2. **Add Rules** - Add app blocking rules
3. **Enable Service** - Activate Accessibility Service permission
4. **Enable Device Admin** - Grant Device Admin access

---

**Status**: Ready for deployment on device

