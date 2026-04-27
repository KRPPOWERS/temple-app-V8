# 📋 Complete Setup Guide
## How to Upload to GitHub & Get the Android APK

This guide is written for someone who has **never used GitHub or Android Studio** before.  
Follow every step in order.

---

## PART 1 — Create a GitHub Account (skip if you already have one)

1. Open your browser and go to **https://github.com**
2. Click **Sign up** (top-right corner)
3. Enter your email, create a password, choose a username
4. Verify your email
5. Done ✅

---

## PART 2 — Create a New Repository on GitHub

1. Log in to GitHub
2. Click the **+** button (top-right) → **New repository**
3. Fill in:
   - **Repository name:** `temple-android`
   - **Description:** `Arulmigu Kallichi Amman Temple App`
   - **Visibility:** Private *(recommended — keeps your code private)*
4. Leave everything else as-is → click **Create repository**

---

## PART 3 — Upload the Files to GitHub

### Option A — Using GitHub website (easiest, no software needed)

1. On your new empty repository page, click **uploading an existing file** (shown in the centre)
2. Drag and drop **ALL** the files and folders you received:
   ```
   .github/
   app/
   gradle/
   .gitignore
   build.gradle
   gradle.properties
   settings.gradle
   README.md
   SETUP_GUIDE.md
   ```
3. **Important:** Also drag `temple_v8.html` into the upload — you need to
   place it at:  `app/src/main/assets/temple_v8.html`

   GitHub does not support folder uploads from the drag-drop.  
   👉 Use **Option B** below if you want to maintain the folder structure properly.

### Option B — Using GitHub Desktop (recommended)

1. Download **GitHub Desktop**: https://desktop.github.com
2. Install and sign in with your GitHub account
3. Click **Clone a repository** → select `temple-android`
4. Choose a folder on your PC to save the files (e.g., `C:\temple-android`)
5. Copy all the files you received into that folder, maintaining the folder structure
6. Make sure `temple_v8.html` is placed at:
   ```
   temple-android\app\src\main\assets\temple_v8.html
   ```
7. In GitHub Desktop, you'll see all the files listed as "changes"
8. In the bottom-left box, type a message like `Initial upload`
9. Click **Commit to main** → then **Push origin**
10. Files are now on GitHub ✅

---

## PART 4 — Get the Android APK (Automatic Build)

Once the files are pushed to GitHub:

1. Go to your repository on GitHub
2. Click the **Actions** tab (at the top)
3. You'll see a workflow called **"Build Temple Android APK"** running
4. Wait for it to complete (usually **3–5 minutes**) — a green ✅ means success
5. Click the workflow run → scroll down to **Artifacts**
6. Download **`temple-app-debug`** — this is a ZIP containing the APK

### Install the APK on Android

1. Unzip the downloaded file — you'll find `app-debug.apk`
2. Transfer to your Android phone (WhatsApp to yourself, email, USB cable)
3. On the phone:
   - Go to **Settings → Security** (or **Privacy**)
   - Enable **"Install from Unknown Sources"** or **"Install Unknown Apps"**
4. Open the APK file on your phone → tap **Install**
5. Open the app from your home screen — it will be named **"Kallichi Amman Temple"**

---

## PART 5 — Updating the App (When HTML Changes)

When you get a new version of `temple_v8.html`:

1. Open GitHub Desktop
2. Replace the old `temple_v8.html` in `temple-android\app\src\main\assets\`
3. In GitHub Desktop, type a commit message: `Update HTML to latest version`
4. Click **Commit to main** → **Push origin**
5. GitHub Actions will automatically build a new APK in ~5 minutes
6. Download and install the new APK

---

## PART 6 — Build Using Android Studio (for advanced users)

If you want to build locally instead of waiting for GitHub Actions:

1. Download Android Studio: https://developer.android.com/studio
2. Install it (accept all defaults)
3. Open Android Studio → **Open** → select the `temple-android` folder
4. Wait for Gradle sync (first time takes 5–10 minutes, downloads Android SDK)
5. Connect your Android phone via USB (enable Developer Options + USB Debugging)
6. Press **▶️ Run** (green play button) — app installs directly on your phone

---

## ❓ Frequently Asked Questions

**Q: Do I need to pay anything?**  
A: No. GitHub (free plan), GitHub Actions (2000 min/month free), and Android Studio are all free.

**Q: The Actions build failed — what do I do?**  
A: Most likely `temple_v8.html` is missing from `app/src/main/assets/`. Add it and push again.

**Q: Can I share this APK with others in the temple?**  
A: Yes. Send the `app-debug.apk` file via WhatsApp or email. They need to enable "Install Unknown Apps" on their phone.

**Q: How is this different from a Play Store app?**  
A: The debug APK works the same but is not listed in the Play Store. To publish on Play Store, you need a Google Developer account (₹1,750 one-time fee) and a signed release build.

**Q: Will data be lost when the app is updated?**  
A: No. All data is stored in the phone's localStorage AND synced to Google Sheets. Updating the APK does not delete data.

**Q: The app shows a blank screen**  
A: Check that the file is named exactly `temple_v8.html` (not `Temple_v8.html` or `temple v8.html`). File names are case-sensitive.

---

*For technical help, share the error message from the GitHub Actions log.*
