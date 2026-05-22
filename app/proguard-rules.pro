# Add project specific ProGuard rules here.

# Keep Gemini AI SDK
-keep class com.google.ai.client.** { *; }
-keep class com.google.generativeai.** { *; }

# Keep Gson for JSON parsing
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep data models
-keep class com.ainotes.data.model.** { *; }

# Keep Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Keep Hilt
-keep class dagger.hilt.** { *; }

# ML Kit
-keep class com.google.mlkit.** { *; }

# PdfBox
-keep class com.tom_roush.** { *; }
