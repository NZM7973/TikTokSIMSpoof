package com.tiktokbypass.simspoof.hooks;

import android.os.Build;
import android.telephony.TelephonyManager;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import com.tiktokbypass.simspoof.config.CountryPresets;
import com.tiktokbypass.simspoof.config.ModuleSettings;

/**
 * TelephonyManager Hook类
 * 拦截所有SIM卡相关的API调用
 * 
 * 支持两种模式：
 * 1. 隐藏模式：让应用认为没有SIM卡
 * 2. 伪装模式：返回伪造的SIM卡信息
 */
public class TelephonyManagerHook {

    private static final String TAG = "TikTokSIMSpoof";

    // 缓存的返回值，避免重复计算
    private static String cachedCountryIso;
    private static String cachedOperatorCode;
    private static String cachedOperatorName;
    private static int cachedSimState;
    private static boolean cachedHasIccCard;

    /**
     * 初始化Hook
     */
    public static void init(LoadPackageParam lpparam, ModuleSettings settings) {
        // 预计算所有返回值，避免Hook回调中计算
        prepareReturnValues(settings);

        // Hook TelephonyManager 类
        Class<?> telephonyManagerClass = XposedHelpers.findClass(
                "android.telephony.TelephonyManager",
                lpparam.classLoader);

        // Hook getSimCountryIso()
        hookGetSimCountryIso(telephonyManagerClass);

        // Hook getNetworkCountryIso()
        hookGetNetworkCountryIso(telephonyManagerClass);

        // Hook getSimOperator()
        hookGetSimOperator(telephonyManagerClass);

        // Hook getSimOperatorName()
        hookGetSimOperatorName(telephonyManagerClass);

        // Hook getSimState()
        hookGetSimState(telephonyManagerClass);

        // Hook hasIccCard()
        hookHasIccCard(telephonyManagerClass);

        // Hook 带 slotIndex 参数的方法（多SIM卡支持）
        hookMultiSimMethods(telephonyManagerClass);

        XposedBridge.log(TAG + ": TelephonyManager 所有方法已Hook");
    }

    /**
     * 预计算返回值
     */
    private static void prepareReturnValues(ModuleSettings settings) {
        if (settings.isHideMode()) {
            // 隐藏模式：返回空值或表示无SIM卡的状态
            cachedCountryIso = "";
            cachedOperatorCode = "";
            cachedOperatorName = "";
            cachedSimState = TelephonyManager.SIM_STATE_ABSENT;
            cachedHasIccCard = false;
        } else {
            // 伪装模式：返回伪造的信息
            CountryPresets.RegionInfo info = CountryPresets.getRegionInfo(settings.getCountryCode());
            cachedCountryIso = info.countryCode;
            cachedOperatorCode = info.operatorCode;
            cachedOperatorName = info.operatorName;
            cachedSimState = TelephonyManager.SIM_STATE_READY;
            cachedHasIccCard = true;
        }

        XposedBridge.log(TAG + ": 返回值已预计算 - CountryIso=" + cachedCountryIso +
                ", Operator=" + cachedOperatorCode);
    }

    /**
     * Hook getSimCountryIso()
     */
    private static void hookGetSimCountryIso(Class<?> clazz) {
        // 无参数版本
        XposedHelpers.findAndHookMethod(clazz, "getSimCountryIso",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return cachedCountryIso;
                    }
                });

        // 带 slotIndex 参数的版本 (API 24+)
        try {
            XposedHelpers.findAndHookMethod(clazz, "getSimCountryIso", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedCountryIso;
                        }
                    });
        } catch (Throwable ignored) {
            // 某些版本可能没有这个方法
        }
    }

    /**
     * Hook getNetworkCountryIso()
     */
    private static void hookGetNetworkCountryIso(Class<?> clazz) {
        // 无参数版本
        XposedHelpers.findAndHookMethod(clazz, "getNetworkCountryIso",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return cachedCountryIso;
                    }
                });

        // 带 slotIndex 参数的版本
        try {
            XposedHelpers.findAndHookMethod(clazz, "getNetworkCountryIso", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedCountryIso;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook getSimOperator()
     */
    private static void hookGetSimOperator(Class<?> clazz) {
        XposedHelpers.findAndHookMethod(clazz, "getSimOperator",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return cachedOperatorCode;
                    }
                });

        // 带 subId 参数的版本
        try {
            XposedHelpers.findAndHookMethod(clazz, "getSimOperator", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedOperatorCode;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook getSimOperatorName()
     */
    private static void hookGetSimOperatorName(Class<?> clazz) {
        XposedHelpers.findAndHookMethod(clazz, "getSimOperatorName",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return cachedOperatorName;
                    }
                });

        // 带 subId 参数的版本
        try {
            XposedHelpers.findAndHookMethod(clazz, "getSimOperatorName", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedOperatorName;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook getSimState()
     */
    private static void hookGetSimState(Class<?> clazz) {
        // 无参数版本
        XposedHelpers.findAndHookMethod(clazz, "getSimState",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return cachedSimState;
                    }
                });

        // 带 slotIndex 参数的版本
        try {
            XposedHelpers.findAndHookMethod(clazz, "getSimState", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedSimState;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook hasIccCard()
     */
    private static void hookHasIccCard(Class<?> clazz) {
        XposedHelpers.findAndHookMethod(clazz, "hasIccCard",
                new XC_MethodReplacement() {
                    @Override
                    protected Object replaceHookedMethod(MethodHookParam param) {
                        return cachedHasIccCard;
                    }
                });

        // 带 slotId 参数的版本
        try {
            XposedHelpers.findAndHookMethod(clazz, "hasIccCard", int.class,
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedHasIccCard;
                        }
                    });
        } catch (Throwable ignored) {
        }
    }

    /**
     * Hook 多SIM卡相关方法
     */
    private static void hookMultiSimMethods(Class<?> clazz) {
        // getNetworkOperator()
        try {
            XposedHelpers.findAndHookMethod(clazz, "getNetworkOperator",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedOperatorCode;
                        }
                    });
        } catch (Throwable ignored) {
        }

        // getNetworkOperatorName()
        try {
            XposedHelpers.findAndHookMethod(clazz, "getNetworkOperatorName",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return cachedOperatorName;
                        }
                    });
        } catch (Throwable ignored) {
        }

        // getSubscriberId() - 返回空字符串，避免泄露真实IMSI
        try {
            XposedHelpers.findAndHookMethod(clazz, "getSubscriberId",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return "";
                        }
                    });
        } catch (Throwable ignored) {
        }

        // getSimSerialNumber() - 返回空字符串
        try {
            XposedHelpers.findAndHookMethod(clazz, "getSimSerialNumber",
                    new XC_MethodReplacement() {
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) {
                            return "";
                        }
                    });
        } catch (Throwable ignored) {
        }
    }
}
