
#!/bin/bash

# Unlock the emulator screen
adb shell input keyevent 82

# Open the Play Store
adb shell monkey -p com.android.vending -c android.intent.category.LAUNCHER 1

# Wait for Play Store to launch
sleep 5

# Search for the app
adb shell input text 'Example%App'
adb shell input keyevent 66  # Enter key

# Wait for search results
sleep 5

# Tap the app (replace with correct coordinates)
adb shell input tap 500 800

# Wait for app page to load
sleep 5

# Tap the install button (replace with correct coordinates)
adb shell input tap 500 1200

# Wait for the app to install
sleep 30

# Verify the installation
adb shell pm list packages | grep '<package_name>'
