# Copy assets to Flutter project

$sourceIcon = "C:\Users\HM\Documents\AppLimit\logisiel\icon.png"
$sourceVideo = "C:\Users\HM\Documents\AppLimit\logisiel\Developer options USB debugging.mp4"
$destAssets = "C:\Users\HM\Documents\AppLimit\logisiel\flutter_app\assets"

Write-Host "Copying assets..." -ForegroundColor Yellow

if (Test-Path $sourceIcon) {
    Copy-Item $sourceIcon -Destination $destAssets -Force
    Write-Host "✅ icon.png copied" -ForegroundColor Green
} else {
    Write-Host "❌ icon.png not found at: $sourceIcon" -ForegroundColor Red
}

if (Test-Path $sourceVideo) {
    Copy-Item $sourceVideo -Destination $destAssets -Force
    Write-Host "✅ video copied" -ForegroundColor Green
} else {
    Write-Host "❌ video not found at: $sourceVideo" -ForegroundColor Red
}

Write-Host ""
Write-Host "Assets in flutter_app/assets:" -ForegroundColor Cyan
Get-ChildItem $destAssets | ForEach-Object { Write-Host "  - $($_.Name)" -ForegroundColor White }

Write-Host ""
Write-Host "Done!" -ForegroundColor Green
