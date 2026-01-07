package com.tiktokbypass.simspoof.config;

import java.util.HashMap;
import java.util.Map;

/**
 * 预设的国家/地区信息
 * 包含国家码、运营商代码(MCC+MNC)、运营商名称
 */
public class CountryPresets {

    /**
     * 地区信息类
     */
    public static class RegionInfo {
        public final String countryCode;  // ISO 国家码 (小写)
        public final String operatorCode; // MCC + MNC
        public final String operatorName; // 运营商名称
        public final String displayName;  // 显示名称

        public RegionInfo(String countryCode, String operatorCode, String operatorName, String displayName) {
            this.countryCode = countryCode;
            this.operatorCode = operatorCode;
            this.operatorName = operatorName;
            this.displayName = displayName;
        }
    }

    // 预设地区列表
    private static final Map<String, RegionInfo> PRESETS = new HashMap<>();

    static {
        // 美国 - T-Mobile
        PRESETS.put("us", new RegionInfo("us", "310260", "T-Mobile", "美国 (T-Mobile)"));
        
        // 日本 - NTT DOCOMO
        PRESETS.put("jp", new RegionInfo("jp", "44010", "NTT DOCOMO", "日本 (NTT DOCOMO)"));
        
        // 韩国 - SK Telecom
        PRESETS.put("kr", new RegionInfo("kr", "45005", "SK Telecom", "韩国 (SK Telecom)"));
        
        // 台湾 - 中华电信
        PRESETS.put("tw", new RegionInfo("tw", "46692", "Chunghwa Telecom", "台湾 (中华电信)"));
        
        // 香港 - CSL Mobile
        PRESETS.put("hk", new RegionInfo("hk", "45400", "CSL Mobile", "香港 (CSL Mobile)"));
        
        // 新加坡 - SingTel
        PRESETS.put("sg", new RegionInfo("sg", "52501", "SingTel", "新加坡 (SingTel)"));
        
        // 英国 - O2 UK
        PRESETS.put("gb", new RegionInfo("gb", "23410", "O2 UK", "英国 (O2 UK)"));
        
        // 德国 - T-Mobile DE
        PRESETS.put("de", new RegionInfo("de", "26201", "T-Mobile DE", "德国 (T-Mobile DE)"));
    }

    /**
     * 获取地区信息
     */
    public static RegionInfo getRegionInfo(String countryCode) {
        RegionInfo info = PRESETS.get(countryCode.toLowerCase());
        if (info == null) {
            // 默认返回美国
            info = PRESETS.get("us");
        }
        return info;
    }

    /**
     * 获取运营商代码
     */
    public static String getOperatorCode(String countryCode) {
        return getRegionInfo(countryCode).operatorCode;
    }

    /**
     * 获取运营商名称
     */
    public static String getOperatorName(String countryCode) {
        return getRegionInfo(countryCode).operatorName;
    }

    /**
     * 获取ISO国家码
     */
    public static String getCountryIso(String countryCode) {
        return getRegionInfo(countryCode).countryCode;
    }

    /**
     * 获取所有预设的国家码列表
     */
    public static String[] getAllCountryCodes() {
        return PRESETS.keySet().toArray(new String[0]);
    }

    /**
     * 获取所有预设的显示名称列表
     */
    public static String[] getAllDisplayNames() {
        String[] codes = getAllCountryCodes();
        String[] names = new String[codes.length];
        for (int i = 0; i < codes.length; i++) {
            names[i] = getRegionInfo(codes[i]).displayName;
        }
        return names;
    }
}
