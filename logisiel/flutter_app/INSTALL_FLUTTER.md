# התקנת Flutter - מדריך מהיר

## שלב 1: הורדה
1. גש ל: https://docs.flutter.dev/get-started/install/windows
2. הורד את Flutter SDK (כ-1GB)
3. חלץ את הקובץ ל: `C:\flutter`

## שלב 2: הוספה ל-PATH
1. חפש "Environment Variables" ב-Windows
2. לחץ על "Edit the system environment variables"
3. לחץ על "Environment Variables"
4. תחת "System variables", מצא את "Path"
5. לחץ "Edit" ואז "New"
6. הוסף: `C:\flutter\bin`
7. לחץ "OK" על כל החלונות

## שלב 3: בדיקה
פתח PowerShell חדש והרץ:
```powershell
flutter doctor
```

## שלב 4: תיקון בעיות אפשריות
אם `flutter doctor` מראה שגיאות:

### Android toolchain
```powershell
flutter doctor --android-licenses
```

### Visual Studio (לבניית Windows apps)
הורד Visual Studio 2022 Community:
https://visualstudio.microsoft.com/downloads/

התקן עם "Desktop development with C++"

## שלב 5: הרצת הפרויקט
```powershell
cd "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app"
flutter pub get
flutter run -d windows
```

## פתרון בעיות נפוצות

### "flutter לא מזוהה"
- סגור וש PowerShell מחדש אחרי שינוי PATH
- וודא שהנתיב הוא `C:\flutter\bin` ולא `C:\flutter`

### "No devices found"
```powershell
flutter config --enable-windows-desktop
```

### בעיות ב-pub get
```powershell
flutter pub cache repair
flutter clean
flutter pub get
```

## הרצה מהירה ללא Flutter מותקן

אם Flutter לא מותקן, אפשר להריץ באמצעות קבצי EXE שנבנו מראש:
1. בקש מישהו עם Flutter מותקן לבנות: `flutter build windows --release`
2. קח את התיקייה: `build\windows\runner\Release\`
3. העתק אותה למחשב אחר
4. הרץ את `applimit_desktop.exe`

## קישורים שימושיים
- תיעוד Flutter: https://docs.flutter.dev
- Material Design 3: https://m3.material.io
- Dart documentation: https://dart.dev/guides
