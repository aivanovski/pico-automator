#!/bin/sh

set -e

rm -f ./pico-automator-android/android-driver/build/outputs/apk/androidTest/debug/android-driver-debug-androidTest.apk
rm -f ./pico-automator-android/android-driver/build/outputs/apk/debug/android-driver-debug.apk

./gradlew :pico-automator-android:android-driver:assembleDebugAndroidTest
./gradlew :pico-automator-android:android-driver:assembleDebug

echo "Installing driver instrumentation apk"
adb install -r ./pico-automator-android/android-driver/build/outputs/apk/androidTest/debug/android-driver-debug-androidTest.apk

echo "Installing driver apk"
adb install -r ./pico-automator-android/android-driver/build/outputs/apk/debug/android-driver-debug.apk

#adb shell am instrument -w \
#    -e debug false \
#    -e class com.github.aivanovski.picoautomator.android.driver.DriverService \
#    com.github.aivanovski.picoautomator.android.driver.test/androidx.test.runner.AndroidJUnitRunner

    # -e debug false \
    # -e class com.github.aivanovski.picoautomator.android.driver.test.DriverService \
