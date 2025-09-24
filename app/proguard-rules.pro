# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.kts.

# Keep all classes used by AccessibilityService
-keep class * extends android.accessibilityservice.AccessibilityService { *; }
-keep class com.kmcounty.ridepricing.accessibility.** { *; }

# Keep overlay classes
-keep class com.kmcounty.ridepricing.overlay.** { *; }

# Keep data classes and models
-keep class com.kmcounty.ridepricing.data.** { *; }
-keepclassmembers class com.kmcounty.ridepricing.data.** { *; }

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-dontwarn androidx.room.paging.**

# ML Kit
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.internal.mlkit_** { *; }

# Hilt
-keep class dagger.hilt.** { *; }

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# Compose
-keep class androidx.compose.runtime.** { *; }

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
