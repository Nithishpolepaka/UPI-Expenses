# UPI Pulse — Real-Time Android 14, 15 & 16 APK Project

This folder contains the complete native **Android 14–16 (`minSdk 34`, `targetSdk 35`)** source project for **UPI Pulse**.

## What Makes This Work on Android 14, 15 & 16?
1. **`UpiSmsBroadcastReceiver.kt`**: Listens in the background for `android.provider.Telephony.SMS_RECEIVED`, filters for UPI debit/credit bank messages, queues them safely in `SharedPreferences` even when the app is closed, and pushes them into the ledger UI in real time.
2. **`UpiNotificationListener.kt`**: Captures incoming payment notifications from Google Messages (RCS), GPay, PhonePe, Paytm, and Bank apps.
3. **`MainActivity.kt` (`Context.RECEIVER_NOT_EXPORTED` & `@JavascriptInterface`)**: Complies with strict Android 14+ broadcast receiver flags (`RECEIVER_NOT_EXPORTED` for internal receivers and `android:exported="true"` in `AndroidManifest.xml` for system SMS receivers).

## How to Get Your `.apk` File (2 Easy Ways)

### Method 1: Build in the Cloud for Free (No PC or Android Studio Needed)
1. Create a free repository on [GitHub](https://github.com/new) and upload the contents of this zip folder.
2. Click the **Actions** tab at the top of your GitHub repository.
3. The included `.github/workflows/build-apk.yml` workflow will automatically compile your app and give you a downloadable **`UPI-Pulse-Android14-16-APK.zip`** containing `app-debug.apk` in ~2 minutes.

### Method 2: Build in Android Studio
1. Open this folder in **Android Studio (Jellyfish / Koala / Ladybug or newer)**.
2. Click **Build → Build Bundle(s) / APK(s) → Build APK(s)**.
3. Copy `app/build/outputs/apk/debug/app-debug.apk` to your phone and tap to install.

## Required One-Time Setting on Android 14, 15 & 16 ("Restricted Settings")
Because Android 14+ blocks sideloaded APKs from reading SMS until you confirm ownership:
1. Install the APK on your phone.
2. Open your phone's **Settings → Apps → UPI Pulse**.
3. Tap the **⋮ (Three Dots)** icon in the top-right corner and select **Allow restricted settings** (confirm with your fingerprint or PIN).
4. Grant **SMS** permission (and optionally enable **UPI Pulse** under **Settings → Notification access**).
