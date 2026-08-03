-keepattributes Signature
-keepattributes *Annotation*

-keep class com.example.fianancetracker.core.data.network.dto.** { *; }

-keepclassmembers,allowobfuscation class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
