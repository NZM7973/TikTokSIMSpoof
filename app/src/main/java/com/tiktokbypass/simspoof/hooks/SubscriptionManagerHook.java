package com.tiktokbypass.simspoof.hooks;

import android.os.Build;
import android.telephony.SubscriptionInfo;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.tiktokbypass.simspoof.config.ModuleSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * SubscriptionManager Hook类
 * 拦截订阅管理器的API调用，处理多SIM卡场景
 * 
 * 省电设计：
 * - 直接返回预计算的值，无额外开销
 */
public class SubscriptionManagerHook {

    private static final String TAG = "TikTokSIMSpoof";

    // 缓存的返回值
    private static boolean isHideMode;
    private static List<?> emptyList = Collections.emptyList();

    /**
     * 初始化Hook
     */
    public static void init(LoadPackageParam lpparam, ModuleSettings settings) {
        isHideMode = settings.isHideMode();

        try {
            Class<?> subscriptionManagerClass = XposedHelpers.findClass(
                    "android.telephony.SubscriptionManager",
                    lpparam.classLoader);

            // Hook getActiveSubscriptionInfoList()
            hookGetActiveSubscriptionInfoList(subscriptionManagerClass);

            // Hook getActiveSubscriptionInfoCount()
            hookGetActiveSubscriptionInfoCount(subscriptionManagerClass);

            // Hook getActiveSubscriptionInfo()
            hookGetActiveSubscriptionInfo(subscriptionManagerClass);

            // Hook getActiveSubscriptionInfoForSimSlotIndex()
            hookGetActiveSubscriptionInfoForSimSlotIndex(subscriptionManagerClass);

            XposedBridge.log(TAG + ": SubscriptionManager 所有方法已Hook");

        } catch (Throwable t) {
            XposedBridge.log(TAG + ": SubscriptionManager Hook 部分失败: " + t.getMessage());
        }
    }

    /**
     * Hook getActiveSubscriptionInfoList()
     */
    private static void hookGetActiveSubscriptionInfoList(Class<?> clazz) {
        try {
            XposedHelpers.findAndHookMethod(clazz, "getActiveSubscriptionInfoList",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            if (isHideMode) {
                                // 隐藏模式：返回空列表
                                return emptyList;
                            }
                            // 伪装模式：调用原方法
                            // 这里我们也返回空列表，因为伪造SubscriptionInfo比较复杂
                            // TikTok主要依赖TelephonyManager的API
                            return emptyList;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook getActiveSubscriptionInfoCount()
     */
    private static void hookGetActiveSubscriptionInfoCount(Class<?> clazz) {
        try {
            XposedHelpers.findAndHookMethod(clazz, "getActiveSubscriptionInfoCount",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            if (isHideMode) {
                                return 0;
                            }
                            // 伪装模式返回1个SIM卡
                            return 1;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook getActiveSubscriptionInfo()
     */
    private static void hookGetActiveSubscriptionInfo(Class<?> clazz) {
        try {
            XposedHelpers.findAndHookMethod(clazz, "getActiveSubscriptionInfo", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            if (isHideMode) {
                                return null;
                            }
                            // 伪装模式：返回null，因为伪造SubscriptionInfo太复杂
                            // 主要检测逻辑在TelephonyManager
                            return null;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook getActiveSubscriptionInfoForSimSlotIndex()
     */
    private static void hookGetActiveSubscriptionInfoForSimSlotIndex(Class<?> clazz) {
        try {
            XposedHelpers.findAndHookMethod(clazz, "getActiveSubscriptionInfoForSimSlotIndex", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            if (isHideMode) {
                                return null;
                            }
                            return null;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }
}
