package com.tiktokbypass.simspoof;

import android.content.Context;
import android.content.SharedPreferences;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.tiktokbypass.simspoof.hooks.TelephonyManagerHook;
import com.tiktokbypass.simspoof.hooks.SubscriptionManagerHook;
import com.tiktokbypass.simspoof.config.ModuleSettings;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * LSPosed模块主入口类
 * 
 * 省电设计：
 * - 无后台服务
 * - 启动时一次性读取配置并缓存
 * - 仅Hook目标应用
 */
public class MainHook implements IXposedHookLoadPackage {

    private static final String TAG = "TikTokSIMSpoof";
    
    // TikTok 相关包名 - 支持全版本
    private static final Set<String> TARGET_PACKAGES = new HashSet<>(Arrays.asList(
        "com.zhiliaoapp.musically",      // TikTok 国际版
        "com.ss.android.ugc.trill",       // TikTok Lite
        "com.ss.android.ugc.aweme",       // 抖音
        "com.ss.android.ugc.aweme.lite"   // 抖音极速版
    ));

    // 缓存的配置，避免重复读取
    private static ModuleSettings cachedSettings = null;

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        // 仅处理目标应用，跳过其他应用以节省资源
        if (!TARGET_PACKAGES.contains(lpparam.packageName)) {
            return;
        }

        XposedBridge.log(TAG + ": 检测到目标应用: " + lpparam.packageName);

        // 读取并缓存配置（仅首次读取）
        if (cachedSettings == null) {
            cachedSettings = loadSettings();
            XposedBridge.log(TAG + ": 配置已加载 - 模式: " + cachedSettings.getMode() + 
                           ", 地区: " + cachedSettings.getCountryCode());
        }

        // 如果模块被禁用，直接返回
        if (!cachedSettings.isEnabled()) {
            XposedBridge.log(TAG + ": 模块已禁用，跳过Hook");
            return;
        }

        // 初始化各个Hook
        try {
            TelephonyManagerHook.init(lpparam, cachedSettings);
            XposedBridge.log(TAG + ": TelephonyManager Hook 成功");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": TelephonyManager Hook 失败: " + t.getMessage());
        }

        try {
            SubscriptionManagerHook.init(lpparam, cachedSettings);
            XposedBridge.log(TAG + ": SubscriptionManager Hook 成功");
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": SubscriptionManager Hook 失败: " + t.getMessage());
        }

        XposedBridge.log(TAG + ": 所有Hook初始化完成");
    }

    /**
     * 加载模块配置
     * 使用 XSharedPreferences 读取设置界面保存的配置
     */
    private ModuleSettings loadSettings() {
        try {
            XSharedPreferences prefs = new XSharedPreferences(
                "com.tiktokbypass.simspoof", 
                "module_settings"
            );
            prefs.makeWorldReadable();
            prefs.reload();

            boolean enabled = prefs.getBoolean("enabled", true);
            String mode = prefs.getString("mode", ModuleSettings.MODE_HIDE);
            String countryCode = prefs.getString("country_code", "us");

            return new ModuleSettings(enabled, mode, countryCode);
        } catch (Throwable t) {
            XposedBridge.log(TAG + ": 读取配置失败，使用默认值: " + t.getMessage());
            // 默认启用，隐藏模式
            return new ModuleSettings(true, ModuleSettings.MODE_HIDE, "us");
        }
    }
}
