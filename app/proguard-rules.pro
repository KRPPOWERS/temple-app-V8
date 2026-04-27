# Add project specific ProGuard rules here.

# Keep WebView JavaScript interface (AndroidBridge)
-keepclassmembers class com.kallichitemple.app.MainActivity$* {
    @android.webkit.JavascriptInterface <methods>;
}
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Keep Kotlin coroutines / lambdas
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }

# AndroidX WebKit
-keep class androidx.webkit.** { *; }

# Suppress warnings for unused classes that may not be in older API levels
-dontwarn android.webkit.**
