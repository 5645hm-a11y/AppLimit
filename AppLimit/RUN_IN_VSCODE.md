# הרצת SafeTimeGuard בתוך VS Code

## 🚀 דרכים להרצה:

### **דרך 1: Run & Debug (Ctrl+Shift+D)**
1. לחץ על "Run & Debug" בצד שמאל
2. בחר "Build, Install & Run"
3. לחץ על Play (▶)

### **דרך 2: Command Palette (Ctrl+Shift+P)**
1. הקד "Tasks: Run Task"
2. בחר "Build, Install & Run"
3. הקד Enter

### **דרך 3: Terminal**
```powershell
# שלב 1: בנייה
.\gradlew.bat assembleDebug

# שלב 2: התקנה
.\gradlew.bat installDebug

# שלב 3: הרצה
$adb = $env:ANDROID_SDK_ROOT + '\platform-tools\adb.exe'
& $adb shell am start -n com.example.safetimeguard/.MainActivity
```

### **דרך 4: Quick Build (Ctrl+Shift+B)**
- Shift+Ctrl+B = Build Debug APK

---

## 📋 Tasks זמינים:

| Task | Command | תיאור |
|------|---------|--------|
| **Build, Install & Run** | Ctrl+Shift+P > Run Task | בנה, התקן והרץ |
| **Build Debug APK** | Ctrl+Shift+B | בנה APK בלבד |
| **Install Debug APK** | Ctrl+Shift+P > Run Task | התקן APK |
| **Run App on Emulator** | Ctrl+Shift+P > Run Task | הרץ אפליקציה קיימת |
| **View Logcat** | Ctrl+Shift+P > Run Task | צפה בלוגים |
| **Clean Project** | Ctrl+Shift+P > Run Task | ניקוי בנייה |

---

## ✅ דרישות מקדימות:

- [x] Android SDK installed
- [x] Gradle configured
- [x] ANDROID_SDK_ROOT environment variable set
- [x] Emulator running or device connected

---

## 🔧 Troubleshooting:

**שגיאה: "adb" לא נמצא**
```powershell
$env:ANDROID_SDK_ROOT = "C:\Android\sdk"
```

**שגיאה: אין emulator מחובר**
1. פתח Android Studio
2. AVD Manager > Run emulator
3. חכה להפעלה

**שגיאה: Build fails**
```powershell
.\gradlew.bat clean
.\gradlew.bat build
```

---

## 📊 Output:

- ✅ Build output: Terminal בתוך VS Code
- ✅ APK location: `app/build/outputs/apk/debug/app-debug.apk`
- ✅ Logs: Terminal (Logcat view)

---

**בחר את דרך הרצה מהאפשרויות למעלה!** 🎯
