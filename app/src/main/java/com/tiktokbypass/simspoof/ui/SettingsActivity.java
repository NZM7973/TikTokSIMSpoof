package com.tiktokbypass.simspoof.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tiktokbypass.simspoof.BuildConfig;
import com.tiktokbypass.simspoof.R;
import com.tiktokbypass.simspoof.config.CountryPresets;
import com.tiktokbypass.simspoof.config.ModuleSettings;

/**
 * 模块设置界面
 * 提供模式选择和地区配置功能
 */
public class SettingsActivity extends AppCompatActivity {

    private RadioGroup rgMode;
    private RadioButton rbHide;
    private RadioButton rbSpoof;
    private LinearLayout cardRegion;
    private Spinner spRegion;
    private TextView tvPreviewCountry;
    private TextView tvPreviewOperator;
    private TextView tvPreviewName;
    private Button btnSave;
    private TextView tvVersion;
    private TextView tvStatus;

    private SharedPreferences prefs;
    private String[] regionCodes;
    private String[] regionNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // 初始化SharedPreferences - 使用try-catch处理MODE_WORLD_READABLE在Android 7.0+的限制
        try {
            // 尝试使用MODE_WORLD_READABLE以便LSPosed模块可以读取
            prefs = getSharedPreferences("module_settings", Context.MODE_WORLD_READABLE);
        } catch (SecurityException e) {
            // 如果失败则使用MODE_PRIVATE，配置将通过ContentProvider或文件共享
            prefs = getSharedPreferences("module_settings", Context.MODE_PRIVATE);
        }

        // 确保配置文件可被其他进程读取
        makePrefsWorldReadable();

        // 初始化视图
        initViews();

        // 加载当前配置
        loadSettings();

        // 设置事件监听
        setupListeners();
    }

    /**
     * 使配置文件可被其他进程读取
     */
    private void makePrefsWorldReadable() {
        try {
            // 设置shared_prefs目录和文件权限
            java.io.File prefsDir = new java.io.File(getApplicationInfo().dataDir, "shared_prefs");
            java.io.File prefsFile = new java.io.File(prefsDir, "module_settings.xml");
            if (prefsDir.exists()) {
                prefsDir.setReadable(true, false);
                prefsDir.setExecutable(true, false);
            }
            if (prefsFile.exists()) {
                prefsFile.setReadable(true, false);
            }
        } catch (Exception e) {
            // 忽略权限设置错误
        }
    }

    /**
     * 初始化视图组件
     */
    private void initViews() {
        rgMode = findViewById(R.id.rgMode);
        rbHide = findViewById(R.id.rbHide);
        rbSpoof = findViewById(R.id.rbSpoof);
        cardRegion = findViewById(R.id.cardRegion);
        spRegion = findViewById(R.id.spRegion);
        tvPreviewCountry = findViewById(R.id.tvPreviewCountry);
        tvPreviewOperator = findViewById(R.id.tvPreviewOperator);
        tvPreviewName = findViewById(R.id.tvPreviewName);
        btnSave = findViewById(R.id.btnSave);
        tvVersion = findViewById(R.id.tvVersion);
        tvStatus = findViewById(R.id.tvStatus);

        // 设置版本号
        tvVersion.setText(getString(R.string.about_version, BuildConfig.VERSION_NAME));

        // 初始化地区选择器
        regionCodes = getResources().getStringArray(R.array.region_codes);
        regionNames = getResources().getStringArray(R.array.region_names);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                regionNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spRegion.setAdapter(adapter);
    }

    /**
     * 加载当前配置
     */
    private void loadSettings() {
        String mode = prefs.getString("mode", ModuleSettings.MODE_HIDE);
        String countryCode = prefs.getString("country_code", "us");

        // 设置模式
        if (ModuleSettings.MODE_SPOOF.equals(mode)) {
            rbSpoof.setChecked(true);
            cardRegion.setVisibility(View.VISIBLE);
        } else {
            rbHide.setChecked(true);
            cardRegion.setVisibility(View.GONE);
        }

        // 设置地区
        int regionIndex = findRegionIndex(countryCode);
        spRegion.setSelection(regionIndex);

        // 更新预览
        updatePreview(countryCode);
    }

    /**
     * 设置事件监听
     */
    private void setupListeners() {
        // 模式切换
        rgMode.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbHide) {
                cardRegion.setVisibility(View.GONE);
            } else if (checkedId == R.id.rbSpoof) {
                cardRegion.setVisibility(View.VISIBLE);
            }
        });

        // 地区选择
        spRegion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < regionCodes.length) {
                    updatePreview(regionCodes[position]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 保存按钮
        btnSave.setOnClickListener(v -> saveSettings());
    }

    /**
     * 更新预览信息
     */
    private void updatePreview(String countryCode) {
        CountryPresets.RegionInfo info = CountryPresets.getRegionInfo(countryCode);
        tvPreviewCountry.setText("国家码：" + info.countryCode);
        tvPreviewOperator.setText("运营商代码：" + info.operatorCode);
        tvPreviewName.setText("运营商名称：" + info.operatorName);
    }

    /**
     * 保存设置
     */
    private void saveSettings() {
        String mode = rbSpoof.isChecked() ? ModuleSettings.MODE_SPOOF : ModuleSettings.MODE_HIDE;
        int regionIndex = spRegion.getSelectedItemPosition();
        String countryCode = regionCodes[regionIndex];

        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("enabled", true);
        editor.putString("mode", mode);
        editor.putString("country_code", countryCode);
        editor.apply();

        Toast.makeText(this, R.string.save_success, Toast.LENGTH_SHORT).show();
    }

    /**
     * 查找地区索引
     */
    private int findRegionIndex(String countryCode) {
        for (int i = 0; i < regionCodes.length; i++) {
            if (regionCodes[i].equals(countryCode)) {
                return i;
            }
        }
        return 0; // 默认美国
    }
}
