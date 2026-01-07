# TikTokSIMSpoof

一个LSPosed/Xposed模块，用于绕过TikTok的SIM卡检测。

## 功能

- **隐藏SIM卡模式**：让TikTok认为设备没有插入SIM卡
- **伪装SIM卡模式**：将SIM卡信息伪装成其他地区（美国、日本、韩国等）

## 特点

- ✅ 支持TikTok全版本
- ✅ 支持Android 8.0 ~ Android 16
- ✅ 省电设计：无后台服务，仅在TikTok调用API时响应
- ✅ 简洁的中文界面

## 支持的地区

| 地区 | 国家码 | 运营商 |
|------|--------|--------|
| 美国 | us | T-Mobile |
| 日本 | jp | NTT DOCOMO |
| 韩国 | kr | SK Telecom |
| 台湾 | tw | 中华电信 |
| 香港 | hk | CSL Mobile |
| 新加坡 | sg | SingTel |
| 英国 | gb | O2 UK |
| 德国 | de | T-Mobile DE |

## 安装

1. 确保设备已Root并安装LSPosed
2. 安装本模块APK
3. 在LSPosed管理器中启用模块
4. 选择TikTok作为目标应用
5. 打开模块设置界面，选择工作模式
6. 重启TikTok

## 下载

从 [Releases](https://github.com/NZM7973/TikTokSIMSpoof/releases) 页面下载最新APK。

## 编译

```bash
# 克隆项目
git clone https://github.com/NZM7973/TikTokSIMSpoof.git
cd TikTokSIMSpoof

# 编译Debug版本
./gradlew assembleDebug

# 编译Release版本
./gradlew assembleRelease
```

## 原理

本模块通过Hook Android系统的 `TelephonyManager` 和 `SubscriptionManager` API，拦截TikTok对SIM卡信息的查询请求，返回伪造的信息或空值。

由于是Hook系统级API而非TikTok内部代码，所以理论上支持TikTok的所有版本。

## 许可证

MIT License

