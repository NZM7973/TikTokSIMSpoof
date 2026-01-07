# TikTok SIM Spoof 模块 ProGuard 规则

# 保留 Xposed 相关类
-keep class de.robv.android.xposed.** { *; }
-keep class com.tiktokbypass.simspoof.MainHook { *; }
-keep class com.tiktokbypass.simspoof.hooks.** { *; }
-keep class com.tiktokbypass.simspoof.config.** { *; }

# 保留 UI 类
-keep class com.tiktokbypass.simspoof.ui.** { *; }

# 保留方法名（用于反射）
-keepclassmembers class * {
    @de.robv.android.xposed.* <methods>;
}
