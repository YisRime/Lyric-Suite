# Lyric Suite

`de.yisrime.lyricsuite`。状态栏歌词显示与歌词采集取词的单一模块，取代 `StatusBarLyric`（`de.yisrime.lrcbar`）与 `Lyric Getter`（`de.yisrime.lrcget`）两颗模块。构建入口为工作区 `D:/Work/LSPosed`，规则见该目录 `agents.md`。

## 形态

一个 `:app` 模块，`applicationId` 与 `namespace` 同为 `de.yisrime.lyricsuite`，`minSdk 30`，版本 `1.0.<修订号>` 配 `versionCode = 100 + 修订号`。界面为 Lyrics Getter 的 ViewBinding + Material3 壳：底部导航三段（首页、应用规则、设置），设置页下挂各功能屏，均在 `res/navigation/nav_graph.xml` 声明。`src/main/kotlin` 下按来源保留 `de/yisrime/lrcbar`（显示与钩子侧）与 `de/yisrime/lrcget`（取词侧与设置页），`de/yisrime/lyricsuite` 存放合并后的新成员：模块入口、基类、配置存储与合并后的功能屏。包根合并未完成，届时 `cn/lyric/getter` 是唯一豁免项。

## 构建

必须用 `gradle-dist/gradle-8.14/bin/gradle.bat` 与 `JAVA_HOME="C:/Program Files/BellSoft/LibericaJDK-21-Full"`。产物名 `LyricSuite-<versionName>-<versionCode>-<variant>-<构建时间>.apk`。

## 冻结契约

- 框架按类名反射构造模块入口，`app/src/main/resources/META-INF/xposed/java_init.list` 必须逐字等于 `de.yisrime.lyricsuite.hook.MainHook`，与 `app/proguard-rules.pro` 中该类的 keep 行同批修改。
- 歌词数据以 `Lyric_Data` 广播传 `cn.lyric.getter.api.data.LyricData`，跨进程 Parcelable 按类名反查，因此 `LyricData` 与 `ExtraData` 的全限定名与 `writeInt(type) + writeString(lyric) + writeBundle(extra)` 的写入顺序不得改动。
- `OperateType` 只以序号上线，类名可被混淆，但声明顺序 `STOP`、`UPDATE`、`MEDIA_DATA` 是契约的一部分，插入或重排会让接收侧静默丢弃事件。
- `app/libs/Lyric-Getter-Api-7.0.0.aar` 原样随仓并附校验文件，不再从远端取；本模块只保留对宿主内嵌 SDK 的中继，不再向第三方发布该 API。
- `hook/app/Api.kt` 读取的是宿主 APK 内已发布的字节码字段 `API_VERSION`，`BuildConfig.API_VERSION` 与 `AndroidManifest` 无关于对端版本，改动等于对已装宿主断供；`app_rules.json` 中 `useApi` 条目必须与该实现同步。
- 配置落在框架守护进程的 `configs(module_pkg_name, user_id, group, key)`，本模块只用一个 group `config`。换 `applicationId` 等于清空全部已存配置与作用域并需重新登记。

## 判据

激活状态取「框架服务已绑定」且「`getScope()` 含 `com.android.systemui`」两条同时成立，见 `LyricApplication` 的 `ready` 与 `scoped`；任一不成立时总开关被写为 `false`。设置写入与注入侧读取同一 group，注入侧只读，配置生效依赖框架的变更推送，不再有自造的刷新广播。

## 与旧两仓的关系

旧两仓保留为归档，本仓不携带其 git 历史，首两笔提交分别对应 `StatusBarLyric@47a79d9` 与 `Lyric-Getter@28c4ccd` 的快照。`de.yisrime.lrcbar` 的模块仓库登记欠账由 `de.yisrime.lyricsuite` 的一次新登记取代。

## 已知未完成

- 真机验证未做过，全部结论来自构建与产物静态核验。
- `Lyric_Data` 无法定向收紧：注入侧的接收器注册在 `com.android.systemui` 名下，设置页的监视器注册在本包名下，单条 `setPackage` 只能命中其一，故该广播对全机可见与可伪造的现状保持不变。要收紧需先把两端之一改为非广播传输。
- `resources` 与 `res/values` 中仍有少量库内部串的引用残影，属扫描口径限制，不影响构建。
