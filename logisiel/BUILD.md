# Build Instructions for AppLimit Grayscale Controller

## Prerequisites
- Visual Studio 2022 or later (with .NET 6.0 support)
- OR: .NET 6.0 SDK (dotnet CLI)
- Windows 10 or later

## Option 1: Build with Visual Studio

1. Open `GrayscaleDesktopApp.csproj` in Visual Studio
2. Right-click solution and select "Build Solution" (Ctrl+Shift+B)
3. Output will be in `bin\Release\` or `bin\Debug\`
4. Run the `.exe` file

## Option 2: Build with .NET CLI

```powershell
cd C:\Users\HM\Documents\logisiel

# Restore dependencies
dotnet restore

# Build debug version
dotnet build

# Build release version (optimized)
dotnet build -c Release

# Run directly
dotnet run

# Publish as standalone executable
dotnet publish -c Release -f net6.0-windows -r win-x64 --self-contained
```

## Output Artifacts

### Debug Build
- Location: `bin\Debug\net6.0-windows\`
- Files: `GrayscaleDesktopApp.dll`, `GrayscaleDesktopApp.exe`

### Release Build
- Location: `bin\Release\net6.0-windows\`
- Files: Optimized executable

### Published Standalone
- Location: `bin\Release\net6.0-windows\win-x64\publish\`
- Files: Complete application (can run on any Windows machine without .NET SDK)

## NuGet Dependencies

The project automatically downloads:
- **SharpAdbClient** (3.4.0) - ADB communication

## Troubleshooting Build Issues

### "The project does not compile"
- Ensure .NET 6.0 is installed: `dotnet --version`
- Clean and rebuild: `dotnet clean && dotnet build`

### "Could not find package SharpAdbClient"
- Check internet connection
- Run: `dotnet restore`

### "WPF Designer not working in Visual Studio"
- This is normal - designer support is limited for some WPF features
- Use XAML text editor or run the app to preview

## Creating Redistributable Package

For distribution to other machines:

```powershell
# Create self-contained release (doesn't require .NET runtime)
dotnet publish -c Release -f net6.0-windows -r win-x64 --self-contained -p:SelfContained=true -p:PublishSingleFile=true

# Result: Single .exe file in publish folder
```

## Version Information
- Target Framework: .NET 6.0
- Platform: Windows only (WPF)
- Architecture: x64 and x86 compatible

---

**Last Updated**: February 2026
