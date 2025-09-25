# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

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

# Keep ML Kit models and related classes
-keep class com.google.mlkit.** { *; }
-keep class com.google.android.gms.tasks.** { *; }
-keep class com.google.android.gms.vision.** { *; }
-keep class com.google.firebase.ml.** { *; }
-keep class com.google.firebase.ml.vision.** { *; }
-keep class com.google.firebase.ml.common.** { *; }
-keep class com.google.firebase.ml.vision.common.** { *; }
-keep class com.google.firebase.ml.vision.text.** { *; }
-keep class com.google.firebase.ml.vision.document.** { *; }
-keep class com.google.firebase.ml.vision.label.** { *; }
-keep class com.google.firebase.ml.vision.barcode.** { *; }
-keep class com.google.firebase.ml.vision.face.** { *; }
-keep class com.google.firebase.ml.vision.image.** { *; }
-keep class com.google.firebase.ml.vision.object.** { *; }
-keep class com.google.firebase.ml.vision.automl.** { *; }
-keep class com.google.firebase.ml.vision.face.internal.** { *; }
-keep class com.google.firebase.ml.vision.text.internal.** { *; }
-keep class com.google.firebase.ml.vision.common.internal.** { *; }
-keep class com.google.firebase.ml.vision.document.internal.** { *; }
-keep class com.google.firebase.ml.vision.label.internal.** { *; }
-keep class com.google.firebase.ml.vision.barcode.internal.** { *; }
-keep class com.google.firebase.ml.vision.object.internal.** { *; }
-keep class com.google.firebase.ml.vision.automl.internal.** { *; }
-keep class com.google.firebase.ml.vision.image.internal.** { *; }
-keep class com.google.firebase.ml.vision.face.internal.** { *; }

# Keep the custom code
-keep class com.kmcounty.ml.** { *; }
-keep interface com.kmcounty.ml.** { *; }

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
