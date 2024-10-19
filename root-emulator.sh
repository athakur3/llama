#!/bin/bash

# Start the Android Emulator (make sure your emulator name matches)
EMULATOR_NAME="emulator-5554" # Replace with your actual emulator ID

echo "Starting the Android Emulator..."
adb start-server
adb devices

# Wait for the emulator to boot up
echo "Waiting for emulator to boot..."
adb -s $EMULATOR_NAME wait-for-device

# Restart ADB as root
echo "Restarting adb as root..."
adb -s $EMULATOR_NAME root

# Remount system partition to be writable
echo "Remounting system as writable..."
adb -s $EMULATOR_NAME remount

# Download the Magisk APK (if you don't have it already)
# You can comment this if you already have the APK file locally.
MAGISK_VERSION="26.1" # Replace with desired Magisk version
MAGISK_APK="Magisk-v${MAGISK_VERSION}.apk"
MAGISK_URL="https://github.com/topjohnwu/Magisk/releases/download/v${MAGISK_VERSION}/${MAGISK_APK}"

# Download Magisk APK if not present
if [ ! -f "$MAGISK_APK" ]; then
    echo "Downloading Magisk APK..."
    wget "$MAGISK_URL"
fi

# Push Magisk APK to emulator
echo "Pushing Magisk APK to emulator..."
adb -s $EMULATOR_NAME push "$MAGISK_APK" /data/local/tmp/

# Install Magisk APK via shell (no manual intervention required)
echo "Installing Magisk APK through shell..."
adb -s $EMULATOR_NAME shell pm install /data/local/tmp/$MAGISK_APK

# Verify installation
echo "Verifying Magisk installation..."
adb -s $EMULATOR_NAME shell pm list packages | grep magisk

# Optional: Push patched boot image or other root files (if needed)
# echo "Pushing patched boot image..."
# adb -s $EMULATOR_NAME push /path/to/patched_boot.img /sdcard/patched_boot.img

# Optional: Flash boot image if using custom patched image (replace with actual boot image path)
# adb -s $EMULATOR_NAME shell dd if=/sdcard/patched_boot.img of=/dev/block/platform/bootdevice/by-name/boot

echo "Rooting process completed. Please verify with a root checker or by checking Magisk Manager."
