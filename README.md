# 🛕 Arulmigu Kallichi Amman Temple — Android App

A native Android wrapper for the Temple Management System web app.  
The HTML file runs inside a full-featured WebView with:

- ✅ Local data storage (localStorage)
- ✅ Internet sync with Google Sheets
- ✅ Camera access for bill/receipt photo uploads
- ✅ File downloads (JPG receipts, PDF, Excel)
- ✅ Share via WhatsApp / Email
- ✅ Back-button navigation
- ✅ Offline support
- ✅ Portrait + landscape support

---

## 📁 Repository Structure

```
temple-android/
├── app/
│   ├── src/main/
│   │   ├── assets/
│   │   │   └── temple_v8.html          ← PUT YOUR HTML FILE HERE
│   │   ├── java/com/kallichitemple/app/
│   │   │   ├── MainActivity.kt
│   │   │   └── FileProvider.kt
│   │   ├── res/
│   │   │   ├── drawable/               ← App icons (replace with temple logo)
│   │   │   ├── layout/activity_main.xml
│   │   │   ├── values/strings.xml
│   │   │   └── xml/file_paths.xml
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── gradle/wrapper/
├── .github/workflows/build.yml         ← Auto-build APK on every push
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🚀 Quick Start (First-Time Setup)

### Prerequisites
| Tool | Version | Download |
|------|---------|----------|
| Android Studio | Hedgehog (2023.1) or newer | [Download](https://developer.android.com/studio) |
| Java JDK | 17 | Bundled with Android Studio |
| Git | Any | [Download](https://git-scm.com/) |

### Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/YOUR_USERNAME/temple-android.git
   cd temple-android
   ```

2. **Add the HTML file**
   - Copy `temple_v8.html` into `app/src/main/assets/`
   - File must be named exactly `temple_v8.html`

3. **Open in Android Studio**
   - Open Android Studio → *Open* → select the `temple-android` folder
   - Wait for Gradle sync to complete (takes 2–5 min first time)

4. **Run on a device or emulator**
   - Connect Android phone via USB (enable USB Debugging in Developer Options)
   - Press the ▶️ **Run** button (Shift+F10)

5. **Build a release APK** (for distribution)
   - In Android Studio: *Build → Generate Signed Bundle/APK*
   - Choose APK → create or use existing keystore → build

---

## 🤖 Automated Builds (GitHub Actions)

Every `git push` to `main` automatically builds a **debug APK**.

- Go to your repo → **Actions** tab → latest workflow run
- Download the `temple-app-debug.apk` artifact
- Install on any Android 5.0+ device

To enable: make sure `.github/workflows/build.yml` is committed.

---

## 📱 Installing the APK on Android

1. Transfer `temple-app-debug.apk` to the phone (via USB, WhatsApp, email, etc.)
2. On the phone: **Settings → Security → Unknown Sources** → Enable
3. Open the APK file and tap **Install**
4. Open **Temple System** from the app drawer

---

## 🔧 Customisation

### Change App Name
Edit `app/src/main/res/values/strings.xml`:
```xml
<string name="app_name">Kallichi Amman Temple</string>
```

### Change App Icon
Replace the `ic_launcher.png` files in each `mipmap-*` folder:
| Folder | Size |
|--------|------|
| mipmap-mdpi | 48×48 px |
| mipmap-hdpi | 72×72 px |
| mipmap-xhdpi | 96×96 px |
| mipmap-xxhdpi | 144×144 px |
| mipmap-xxxhdpi | 192×192 px |

Or use Android Studio: right-click `res` → *New → Image Asset*

### Change Package Name
Edit `app/build.gradle`:
```gradle
applicationId "com.kallichitemple.app"   ← change this
```
Also rename the folder path:
`app/src/main/java/com/kallichitemple/app/`

### Update the HTML File
- Replace `app/src/main/assets/temple_v8.html` with the new version
- Commit and push — GitHub Actions will build a new APK automatically

---

## 🔒 Permissions Explained

| Permission | Why |
|-----------|-----|
| `INTERNET` | Google Sheets sync |
| `CAMERA` | Capture bill/invoice photos |
| `READ_EXTERNAL_STORAGE` | Pick images from gallery |
| `WRITE_EXTERNAL_STORAGE` | Save downloaded receipts/PDFs |
| `VIBRATE` | Haptic feedback (optional) |

---

## 🐛 Troubleshooting

| Problem | Solution |
|---------|----------|
| Blank screen on launch | Check that `temple_v8.html` is in `app/src/main/assets/` |
| Google Sheets not syncing | Ensure internet permission is in Manifest; check phone has internet |
| Camera not working | Grant camera permission when prompted; check AndroidManifest |
| Download not working | Grant storage permission on Android 9 and below |
| App crashes on back press | Usually means the HTML loaded incorrectly; check the file name |

---

## 📦 Minimum Requirements

- **Android 5.0** (API 21) and above
- Works on Android 5.0 → 14
- Recommended: Android 8.0+ for best performance

---

## 🔑 Release Keystore (Important)

When building for Play Store or permanent distribution:
1. Generate a keystore once: *Build → Generate Signed Bundle/APK → Create new*
2. Save the `.jks` file safely — **losing it means you cannot update the app**
3. Never commit the keystore to GitHub

---

*Built with ❤️ for Arulmigu Kallichi Amman Temple*
