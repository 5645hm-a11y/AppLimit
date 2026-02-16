# התקנת Flutter - מדריך מהיר (3 דקות)

## דרך 1: הורדה ידנית (מומלץ!)

### 1. הורד את Flutter SDK
🔗 **הדף נפתח בדפדפן** - לחץ על "Download Flutter SDK"
   
או **הורד ישירות מכאן:**
👉 https://storage.googleapis.com/flutter_infra_release/releases/stable/windows/flutter_windows_3.24.5-stable.zip

### 2. חלץ את הקובץ
- חלץ את `flutter_windows_xxx.zip` ל-**C:\** 
- יווצר תיקייה: `C:\flutter`

### 3. הוסף ל-PATH
1. לחץ Win+R, כתוב: `sysdm.cpl` ולחץ Enter
2. לחץ "Advanced" → "Environment Variables"
3. תחת "User variables", בחר "Path" → "Edit"
4. לחץ "New" והוסף: `C:\flutter\bin`
5. לחץ "OK" על הכל

### 4. בדוק שעובד
**פתח PowerShell חדש** והרץ:
```powershell
flutter --version
```

צריך להציג את גרסת Flutter!

---

## דרך 2: הרצת סקריפט אוטומטי

```powershell
cd "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app"
.\install_flutter.ps1
```

הסקריפט יוריד ויתקין הכל אוטומטית (לוקח ~10 דקות)

---

## אחרי שFlutter מותקן

### הרץ את הפרויקט:
```powershell
cd "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app"

# התקן תלויות
flutter pub get

# הפעל Windows desktop
flutter config --enable-windows-desktop

# הרץ את האפליקציה
flutter run -d windows
```

### בנה EXE:
```powershell
flutter build windows --release
```

ה-EXE יהיה ב: `build\windows\runner\Release\applimit_desktop.exe`

---

## פתרון בעיות

### "flutter לא מזוהה"
- ✅ סגור את PowerShell ופתח מחדש
- ✅ וודא ש-`C:\flutter\bin` ב-PATH
- ✅ הרצ: `$env:Path = "C:\flutter\bin;$env:Path"` (זמני)

### "No devices found"
```powershell
flutter config --enable-windows-desktop
```

### בעיות בהורדה
אם ההורדה תקועה:
1. הורד ידנית מ: https://docs.flutter.dev/get-started/install/windows
2. חלץ ל-C:\flutter
3. הוסף ל-PATH
4. הרץ `flutter doctor`

---

## מה הפרויקט כולל?

✅ **4 מסכים מלאים**: Splash, Setup, Detection, Success  
✅ **Material Design 3**: עיצוב Google מלא  
✅ **13 שפות**: עברית, ערבית, אנגלית ועוד  
✅ **אייקונים של Google**: Material Icons  
✅ **גופן Roboto**  
✅ **אנימציות**: fade, scale, rotation  
✅ **ADB Integration**: שליטה במכשיר Android  

---

**זמן הורדה משוער: 5-10 דקות (תלוי במהירות האינטרנט)**

**זמן התקנה: 2 דקות**

**זמן עד הרצה ראשונה: 15 דקות (כולל flutter doctor)**

🎯 **המטרה**: קובץ EXE שעובד על Windows לשליטה ב-Grayscale של Android!
