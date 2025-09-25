# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# to ensure the line breaks are preserved:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Keep application classes
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-keep public class com.android.vending.licensing.ILicensingService

# Keep Kotlin metadata
-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers class * {
    @org.jetbrains.annotations.NotNull <fields>;
    @org.jetbrains.annotations.Nullable <fields>;
    @org.jetbrains.annotations.NotNull <methods>;
    @org.jetbrains.annotations.Nullable <methods>;
}

# Keep Room database
-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.room.Entity
-keepclassmembers class * {
    @androidx.room.* <methods>;
}

# Keep Parcelable implementations
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Keep Serializable implementations
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep ViewModels
-keep class * extends androidx.lifecycle.ViewModel {
    protected void onCleared();
}

# Keep @Keep annotations
-keep @androidx.annotation.Keep class * {*;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}

# Keep data binding
-keep class android.databinding.** { *; }
-keep class * extends android.databinding.DataBinderMapper
-keep class * extends android.databinding.DataBinderMapper {
    <init>();
}
-keep class * extends android.databinding.ViewDataBinding
-keep class * extends androidx.databinding.DataBinderMapper
-keep class * extends androidx.databinding.ViewDataBinding
-keep class * extends androidx.databinding.DataBinderMapper {
    <init>();
}

# Keep ML Kit models
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.tasks.** { *; }

# Keep accessibility service
-keep public class * extends android.accessibilityservice.AccessibilityService
-keepclassmembers class * {
    @android.view.accessibility.AccessibilityServiceInfo public *;
}

# Keep custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep enums
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep the support library
-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class androidx.** { *; }
-keep interface androidx.** { *; }

# Keep the custom code
-keep class com.kmcounty.app.** { *; }
-keep interface com.kmcounty.app.** { *; }

# Remove logging in release builds
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
    public static int e(...);
}

# Keep annotations
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes EnclosingMethod

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve all native method names and the names of their classes.
-keepclasseswithmembernames class * {
    native <methods>;
}

# Keep setters in Views so that animations can still work.
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# Keep the custom application class
-keep public class * extends android.app.Application
-keep public class * extends android.app.Application {
    public void onCreate();
}

# Keep the custom views
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep the custom view constructors
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

# Keep the custom view constructors that are used in layouts
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Keep the custom view constructors that are used in layouts
-keepclassmembers class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * extends android.view.View {
    void set*(***);
    *** get*();
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "") <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", deepExport = true) <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", flagMapping = {}) <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", indexMapping = {}) <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", mapping = {}) <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", prefix = "") <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(resolveId = false) <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(resolveId = true) <methods>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(resolveId = true) <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(resolveId = false) <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "") <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", deepExport = true) <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", flagMapping = {}) <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", indexMapping = {}) <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", mapping = {}) <fields>;
}

# Keep the custom view methods that are called through reflection
-keepclassmembers class * {
    @android.view.ViewDebug.ExportedProperty(category = "", prefix = "") <fields>;
}

# Firebase ProGuard rules
# Keep Firebase Analytics
-keep class com.google.android.gms.analytics.** { *; }
-keep class com.google.android.gms.measurement.** { *; }

# Keep Firebase Crashlytics
-keep class com.google.firebase.crashlytics.** { *; }
-keep class com.crashlytics.** { *; }

# Keep Firebase Performance
-keep class com.google.firebase.perf.** { *; }

# Keep Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }

# Keep Firebase Remote Config
-keep class com.google.firebase.remoteconfig.** { *; }

# Keep Firebase Auth (if used in the future)
-keep class com.google.firebase.auth.** { *; }

# Keep Firebase Firestore (if used in the future)
-keep class com.google.firebase.firestore.** { *; }

# Keep Firebase Realtime Database (if used in the future)
-keep class com.google.firebase.database.** { *; }

# Keep Firebase Storage (if used in the future)
-keep class com.google.firebase.storage.** { *; }

# Keep Firebase In-App Messaging (if used in the future)
-keep class com.google.firebase.inappmessaging.** { *; }

# Keep Firebase Dynamic Links (if used in the future)
-keep class com.google.firebase.dynamiclinks.** { *; }
