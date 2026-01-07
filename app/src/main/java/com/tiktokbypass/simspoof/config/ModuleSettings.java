package com.tiktokbypass.simspoof.config;

/**
 * 模块配置类
 * 存储用户的设置选项
 */
public class ModuleSettings {

    // 工作模式常量
    public static final String MODE_HIDE = "hide";    // 隐藏SIM卡
    public static final String MODE_SPOOF = "spoof";  // 伪装SIM卡

    private final boolean enabled;
    private final String mode;
    private final String countryCode;

    public ModuleSettings(boolean enabled, String mode, String countryCode) {
        this.enabled = enabled;
        this.mode = mode;
        this.countryCode = countryCode;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getMode() {
        return mode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    /**
     * 是否为隐藏SIM卡模式
     */
    public boolean isHideMode() {
        return MODE_HIDE.equals(mode);
    }

    /**
     * 是否为伪装SIM卡模式
     */
    public boolean isSpoofMode() {
        return MODE_SPOOF.equals(mode);
    }

    /**
     * 获取运营商代码 (MCC+MNC)
     */
    public String getOperatorCode() {
        return CountryPresets.getOperatorCode(countryCode);
    }

    /**
     * 获取运营商名称
     */
    public String getOperatorName() {
        return CountryPresets.getOperatorName(countryCode);
    }
}
