#!/bin/bash
# SafeTimeGuard - הרצה בתוך Emulator

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${YELLOW}=== SafeTimeGuard Emulator Setup ===${NC}\n"

# Check if Android SDK exists
SDK_PATH="C:\Users\MH\AppData\Local\Android\Sdk"
if [ ! -d "$SDK_PATH" ]; then
    echo -e "${RED}Error: Android SDK not found at $SDK_PATH${NC}"
    exit 1
fi

ADB="$SDK_PATH/platform-tools/adb.exe"
EMULATOR="$SDK_PATH/emulator/emulator.exe"

echo -e "${GREEN}[1/4] Checking for available emulators...${NC}"
emulator_list=$("$EMULATOR" -list-avds)

if [ -z "$emulator_list" ]; then
    echo -e "${RED}No emulators found. Please create one in Android Studio.${NC}"
    exit 1
fi

# Show available emulators
echo -e "${GREEN}Available emulators:${NC}"
echo "$emulator_list" | nl

# Use first emulator
EMU_NAME=$(echo "$emulator_list" | head -1)
echo -e "${YELLOW}Starting emulator: $EMU_NAME${NC}\n"

echo -e "${GREEN}[2/4] Starting emulator...${NC}"
"$EMULATOR" -avd "$EMU_NAME" -no-snapshot-load &
EMULATOR_PID=$!

echo -e "${YELLOW}Waiting for emulator to boot (this may take 1-2 minutes)...${NC}"
for i in {1..60}; do
    if "$ADB" shell getprop sys.boot_completed 2>/dev/null | grep -q 1; then
        echo -e "${GREEN}Emulator ready!${NC}"
        break
    fi
    echo -n "."
    sleep 2
done

echo -e "\n${GREEN}[3/4] Building and installing app...${NC}"
cd "c:\Users\MH\AndroidStudioProjects\AppLimit"
.\gradlew.bat installDebug

if [ $? -eq 0 ]; then
    echo -e "\n${GREEN}[4/4] Starting SafeTimeGuard...${NC}"
    "$ADB" shell am start -n com.example.safetimeguard/.MainActivity
    echo -e "${GREEN}✅ App started successfully!${NC}"
else
    echo -e "${RED}❌ Installation failed${NC}"
    exit 1
fi

echo -e "\n${GREEN}=== Setup Complete ===${NC}"
echo -e "${YELLOW}Logs:${NC}"
"$ADB" logcat SafeTimeGuard*:* *:E
