# Advanced PNG to ICO converter with proper format
Add-Type -AssemblyName System.Drawing

$pngPath = "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\assets\icon.png"
$icoPath = "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\windows\runner\resources\app_icon.ico"

# Load the PNG image
$originalBitmap = [System.Drawing.Image]::FromFile($pngPath)

# Create a bitmap with proper format
$bitmap = New-Object System.Drawing.Bitmap($originalBitmap, 256, 256)

# Convert to appropriate pixel format (32bppArgb for ICO)
$converted = New-Object System.Drawing.Bitmap(256, 256, [System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
$graphics = [System.Drawing.Graphics]::FromImage($converted)
$graphics.DrawImage($bitmap, 0, 0, 256, 256)
$graphics.Dispose()

# Save as ICO
$converted.Save($icoPath, [System.Drawing.Imaging.ImageFormat]::Icon)

# Cleanup
$converted.Dispose()
$bitmap.Dispose()
$originalBitmap.Dispose()

Write-Host "Icon converted successfully with proper format!"
Write-Host "Source: $pngPath"
Write-Host "Target: $icoPath"
Write-Host "Format: 256x256 32bppArgb"
