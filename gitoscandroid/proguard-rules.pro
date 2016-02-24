# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/kymjs/developer/android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
}

-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-dontoptimize
-keepattributes *Annotation*
-keepattributes Signature

-dontwarn org.kymjs.kjframe.**
-keep class org.kymjs.kjframe.** {
*;
}

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v4.app.Fragment{ *; }
-keep class android.support.** { *; } 

-dontwarn butterknife.internal.**
-keep class **$$ViewInjector { *; }
-keepnames class * { @butterknife.InjectView *;}

-keep class net.oschina.gitapp.bean.** { *; }
-keep class net.oschina.gitapp.util.SourceEditor { *; }
-keep class uk.co.senab.photoview.** { *; } 
-keep class com.umeng.** { *; }
-keep class com.facebook.** { *; }
-keep class com.blueware.agent.** { *; }
-keep class com.tencent.weibo.** { *; }
-keep class com.kymjs.crash.** { *; }
-keep class com.fasterxml.jackson.** { *; } 
-keep class org.codehaus.jackson.** { *; }

-dontwarn uk.co.senab.photoview.**
-dontwarn com.umeng.**
-dontwarn com.facebook.**
-dontwarn com.blueware.agent.**
-dontwarn com.tencent.weibo.**
-dontwarn com.kymjs.crash.**
-dontwarn com.fasterxml.jackson.**
-dontwarn org.codehaus.jackson.**

-dontwarn com.squareup.okhttp.internal.huc.**
-dontwarn okio.**
-dontwarn rx.**

-dontwarn com.squareup.okhttp.internal.http.*
-keepnames class com.levelup.http.okhttp.** { *; }
-keepnames interface com.levelup.http.okhttp.** { *; }
-keepnames class com.squareup.okhttp.** { *; }
-keepnames interface com.squareup.okhttp.** { *; }