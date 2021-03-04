# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-keep class androidx.** { *; }
#-keep class kotlinx.android.** { *; }
#-keep class com.bumptech.** { *; }
#-keep class kotlin.coroutines.** { *; }
#-keep class kotlin.** { *; }
#-keep class kotlinx.** { *; }
#-keep class com.google.android.material.** { *; }

-keep class  danbroid.busapp.metlinkold.* { <fields>; }
-keep class  danbroid.busapp.gtfs.* { <fields>; }
-keep class  danbroid.busapp.gtfs.rt.* { <fields>; }
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep class com.bumptech.glide.GeneratedAppGlideModuleImpl