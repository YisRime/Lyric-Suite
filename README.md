# Lyric Suite

`de.yisrime.lyricsuite`。将状态栏歌词显示与音乐歌词采集合并为单一 LSPosed 模块，取代此前的 [StatusBarLyric](https://github.com/Block-Network/StatusBarLyric) 与 [Lyric Getter](https://github.com/xiaowine/LyricGetter) 两个模块。

## 功能

- **状态栏歌词**：歌词渲染于系统状态栏，支持宽度、字号、颜色（单色/渐变）、背景、字间距、描边、速度与边距、动画的分区设置
- **歌词采集**：内置 30 余个音乐播放器的专用适配，另支持 MediaSession 通用采集与内嵌 SDK 中继，可输出到系统状态栏、悬浮标题与小米焦点通知
- **行为与图标**：状态栏手势（点击隐藏、长按停止、滑动切歌）、锁屏与息屏策略、图标替换与强制显示
- **系统适配**：MIUI 网速隐藏与平板布局优化、HyperOS 焦点通知与纹理背景（仅对应系统显示）
- **配置管理**：配置经 LSPosed 框架存储与推送，支持备份与恢复（按分组嵌套）

## 安装与使用

1. 安装 APK，在 LSPosed 中激活本模块；作用域为固定名单，勾选 `com.android.systemui` 与所用音乐应用即可
2. 打开模块设置（LSPosed 内点击模块名），进入 **选项 → 锚点选择**：
   - 开启「锚点选择模式」，重启系统界面
   - 点击「获取锚点」，从候选中选择状态栏时钟所在位置，关闭模式后再次重启系统界面
3. 播放音乐即可在状态栏看到歌词；主页右上角按钮可重启系统界面使显示设置立即生效
4. 选项屏提供取词设置（正则屏蔽、重复歌词、息屏输出、网易云强开等）、备份恢复与歌词测试

> 首次安装必须完成锚点选择，否则歌词没有渲染位置。不同机型的锚点不同，换机或系统更新后需重新获取。

## 构建

- JDK 17+ 与 Android Gradle Plugin 8.x；工作区内使用 `gradle-dist/gradle-8.14` 执行 `assembleRelease`
- 产物：`app/build/outputs/apk/release/LyricSuite-<版本>-<versionCode>-release-<时间戳>.apk`

## 许可证

本项目以 [GNU Affero General Public License v3.0](./LICENSE) 或更高版本发布。

## 致谢

- [StatusBarLyric](https://github.com/Block-Network/StatusBarLyric) — 状态栏歌词渲染的原始实现（fkj233 与 Block-Network 贡献者）
- [Lyric Getter](https://github.com/xiaowine/LyricGetter) — 歌词采集架构与播放器适配（xiaowine）
- [LSPosed](https://github.com/LSPosed/LSPosed) — 模块框架
- 所有上游贡献者
