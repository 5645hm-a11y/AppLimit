# AppLimit Grayscale Controller - Desktop App

## 📱 Overview

Beautiful, animated Windows desktop application to control Android grayscale mode via ADB. The app features a phone-like UI with smooth animations and a step-by-step guided process.

## 🎯 Features

- **Phone-like Interface**: Vertical rectangular window design matching mobile app aesthetics
- **Animated Splash Screen**: Logo animation on startup
- **Step-by-step Wizard**:
  1. Enable debugging instructions
  2. Device connection detection with visual feedback
  3. Grayscale enabling with progress indicator
  4. Success confirmation screen
- **Smooth Animations**: Fade-in/out and scale transitions throughout
- **Dark Theme**: Professional dark colors matching the Android app (#FF00D4AA primary, #FF0F1419 background)
- **Auto-detection**: Automatically detects connected Android device

## 📋 Prerequisites

- Windows 10 or later
- .NET 6.0 or later runtime
- Android SDK Platform Tools (ADB)
- Connected Android device with USB debugging enabled

## 🚀 Installation & Running

### Option 1: Build from Source
1. Install Visual Studio 2022 with .NET 6.0 support
2. Open `GrayscaleDesktopApp.csproj` in Visual Studio
3. Build the solution (Debug or Release)
4. Run the compiled executable

### Option 2: Using Command Line
```powershell
cd c:\Users\HM\Documents\logisiel
dotnet build -c Release
dotnet run
```

## 🎮 How to Use

### Step 1: Enable Device Debugging
- Follow the on-screen instructions
- Enable USB Debugging on your Android device:
  - Go to Settings > About Phone
  - Tap Build Number 7 times (Developer Options will appear)
  - Go to Developer Options
  - Enable USB Debugging
- Click "I've Enabled It" after completing this step

### Step 2: Connect Device
- Connect your Android device via USB
- The app will detect your device automatically
- Once detected, a green checkmark will appear
- Click "Continue" to proceed

### Step 3: Enable Grayscale
- Click "Start" to begin the process
- The app will run ADB commands to enable grayscale mode
- Watch the progress bar as it executes
- System will update in real-time

### Step 4: Success
- Once complete, you'll see the success screen
- Your device's display is now in grayscale mode
- Click "Done" to close the application

## 🔧 Technical Details

### ADB Commands Executed
```
adb shell settings put secure accessibility_display_daltonizer_enabled 1
adb shell settings put secure accessibility_display_daltonizer 0
```

### Architecture
- **Language**: C# with WPF (Windows Presentation Foundation)
- **Target Framework**: .NET 6.0
- **UI Pattern**: MVVM-inspired step-based navigation
- **ADB Protocol**: Direct Windows ADB integration via SharpAdbClient library

## 📝 File Structure

```
logisiel/
├── GrayscaleDesktopApp.csproj   # Project configuration
├── App.xaml                      # Application resources and styles
├── App.xaml.cs                   # Application code-behind
├── MainWindow.xaml               # UI definition with 4 steps
├── MainWindow.xaml.cs            # Business logic and animations
├── Properties/
│   └── AssemblyInfo.cs          # Assembly metadata
└── README.md                      # This file
```

## 🎨 UI Components

### Phone Frame
- 380×680px viewport (phone-like proportions)
- 40px rounded corners
- Status bar with time display
- Matches Android app dark theme

### Animation Effects
- Splash screen fade-in with scale (800ms)
- Step transitions with fade and ease functions
- Progress bar smooth increment animation
- Check mark pop-in animation

### Button States
- Default: #FF00D4AA (primary teal)
- Hover: #FF00B8A0 (darker teal)
- Disabled: 50% opacity

## 🛠️ Troubleshooting

### "ADB not found"
- Ensure Android SDK Platform Tools are installed
- Verify path: `%LOCALAPPDATA%\Android\Sdk\platform-tools\adb.exe`
- Add to PATH if needed

### "Device not detected"
- Check USB cable connection
- Enable USB Debugging on device:
  - Settings > Developer Options > USB Debugging
- Authorize USB debugging on device when prompted

### "Permission Denied" error
- Device may have denied ADB access
- Disconnect and reconnect USB
- Authorize the computer in device prompt

## 📚 Dependencies

- **SharpAdbClient** (3.4.0): C# ADB client library
- **.NET 6.0**: Windows Presentation Foundation (WPF)

## 📄 License

This application is part of the AppLimit project.

## 👨‍💻 Developer Notes

To disable grayscale mode use:
```
adb shell settings put secure accessibility_display_daltonizer_enabled 0
```

To check current status:
```
adb shell settings get secure accessibility_display_daltonizer_enabled
```

---

**Made with ❤️ for AppLimit**
