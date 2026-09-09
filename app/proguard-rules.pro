# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Preserve line numbers and source file for Google Play Console stack trace symbolication
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve annotations, signatures and inner classes
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Allow R8 to repackage obfuscated classes into a compact package for maximum obfuscation ratio
-repackageclasses 'o'
-allowaccessmodification

# Room Database
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public void clearAllTables();
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Keep data models so Room SQLite column mapping remains intact
-keep class com.example.data.models.** {
    <fields>;
    <init>(...);
}

# Retrofit, OkHttp and Moshi rules
-dontwarn retrofit2.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn com.squareup.moshi.**
-keepclassmembers class * {
    @com.squareup.moshi.Json <fields>;
}

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**
