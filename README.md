# Lyric Suite

`de.yisrime.lyricsuite`。状态栏歌词显示与歌词采集取词的单一模块，取代 `StatusBarLyric`（`de.yisrime.lrcbar`）与 `Lyric Getter`（`de.yisrime.lrcget`）两颗模块。构建入口为工作区 `D:/Work/LSPosed`，规则见该目录 `agents.md`。

## 形态

一个 `:app` 模块，`applicationId` 与 `namespace` 同为 `de.yisrime.lyricsuite`，`minSdk 30`，版本 `1.0.<修订号>` 配 `versionCode = 100 + 修订号`。显示侧默认锚点内置 `de.yisrime.lrcbar` 在 PGKM10（ColorOS）上验证过的状态栏时钟组合（`com.oplus.systemui.statusbar.widget.StatClock`，`0x7f0a0274`，父容器 `android.widget.LinearLayout` `0x7f0a0a34`，index 0）与歌词色 `#AA81D8CF`，开箱即显示；锚点匹配只看类名、资源 id、父容器与子视图位次，不比对样式，系统字体缩放不会使其失效。其余机型或系统更新导致 id 变动时走锚点选择模式重新获取，选中的值仍写同名配置键。界面为 Lyrics Getter 的 ViewBinding + Material3 壳：底部导航三段按序为主页、歌词、选项，`StatusBarLyric` 侧的全部条目（总开关、图标样式、扩展选项、系统特供、锚点页）集中在「歌词」屏，「选项」屏只留取词侧条目，应用规则经其中的入口页进入。工程不声明桌面启动入口，唯一入口是 LSPosed 管理器按 `MODULE_SETTINGS` 意图打开的 `MainActivity`。`src/main/kotlin` 下按来源保留 `de/yisrime/lrcbar`（显示与钩子侧）与 `de/yisrime/lrcget`（取词侧与设置页），`de/yisrime/lyricsuite` 存放合并后的新成员：模块入口、基类、配置存储与合并后的功能屏。包根合并未完成，届时 `cn/lyric/getter` 是唯一豁免项。

## 构建

必须用 `gradle-dist/gradle-8.14/bin/gradle.bat` 与 `JAVA_HOME="C:/Program Files/BellSoft/LibericaJDK-21-Full"`。产物名 `LyricSuite-<versionName>-<versionCode>-<variant>-<构建时间>.apk`。

## 冻结契约

- 框架按类名反射构造模块入口，`app/src/main/resources/META-INF/xposed/java_init.list` 必须逐字等于 `de.yisrime.lyricsuite.hook.MainHook`，与 `app/proguard-rules.pro` 中该类的 keep 行同批修改。
- 歌词数据以 `Lyric_Data` 广播传 `cn.lyric.getter.api.data.LyricData`，跨进程 Parcelable 按类名反查，因此 `LyricData` 与 `ExtraData` 的全限定名与 `writeInt(type) + writeString(lyric) + writeBundle(extra)` 的写入顺序不得改动。
- `OperateType` 只以序号上线，类名可被混淆，但声明顺序 `STOP`、`UPDATE`、`MEDIA_DATA` 是契约的一部分，插入或重排会让接收侧静默丢弃事件。
- `app/libs/Lyric-Getter-Api-7.0.0.aar` 原样随仓并附校验文件，不再从远端取；本模块只保留对宿主内嵌 SDK 的中继，不再向第三方发布该 API。
- 应用规则的唯一真相是 `app/src/main/assets/app_rules.json`，随仓分发。不存在远端规则版本比对、规则下载与「新规则需重启」提示，也不做版本更新检测；清单不声明 `INTERNET` 权限，产物内不得出现 HTTP 客户端与 markdown 渲染库类，此点以 `dexdump` 与 `unzip -l assets` 取证。
- `hook/app/Api.kt` 读取的是宿主 APK 内已发布的字节码字段 `API_VERSION`，该字段只用于与宿主内已发布副本比对，改动等于对已装宿主断供；`app_rules.json` 中 `useApi` 条目必须与该实现同步。
- 配置落在框架守护进程的 `configs(module_pkg_name, user_id, group, key)`，两侧各占一个 group：显示侧 `config`，取词侧 `getter`，挂载点见 `GroupStore` 的 `DisplayStore` 与 `GetterStore`。清除为「选项」屏单一入口，两组一并清空并重启应用；备份与恢复入口同在该屏，备份导出按组嵌套、恢复时按组回填（无组层的旧扁平文件仅在「顶层值全为原始类型且至少一个键命中两侧配置键」时按取词侧回填，其余文件拒绝并提示，防止误选任意 JSON 污染配置组）。换 `applicationId` 等于清空全部已存配置与作用域并需重新登记。

## 判据

激活状态取「框架服务已绑定」且「`getScope()` 含 `com.android.systemui`」两条同时成立，见 `LyricApplication` 的 `ready` 与 `scoped`；显示侧不再有总开关：注入并匹配到锚点即启用。首页的激活位未收到过回调时按未激活显示。设置写入与注入侧读取同一 group，注入侧只读，两个 group 在同一时机各挂载一次；配置生效依赖框架的变更推送，不再有自造的刷新广播。

配置的唯一存储是框架侧的 `RemotePreferences`，它只在模块被注入的进程里可达。框架只为「自身在作用域内」的包注入模块，`module.prop` 为 `staticScope=true`（2026-09-27 起）：作用域由 `scope.list` 固定，名单内应用（含 `com.android.systemui` 与本包名自己）始终挂载，管理器不可逐条关闭；因此作用域不再是逐应用的开关，也不要再写「至少勾选某两项」之类的装机步骤。

## 与旧两仓的关系

旧两仓保留为归档，本仓不携带其 git 历史，首两笔提交分别对应 `StatusBarLyric@47a79d9` 与 `Lyric-Getter@28c4ccd` 的快照。`de.yisrime.lrcbar` 的模块仓库登记欠账由 `de.yisrime.lyricsuite` 的一次新登记取代。

## 已知未完成

- 真机验证已于 2026-09-27 在 PGKM10 完成：显示侧锚点、作用域固定、Salt（内嵌 API）与网易云（32 位 dexkit）取词链路均实测通过。
- `Lyric_Data` 无法定向收紧：注入侧的接收器注册在 `com.android.systemui` 名下，设置页的监视器注册在本包名下，单条 `setPackage` 只能命中其一，故该广播对全机可见与可伪造的现状保持不变。要收紧需先把两端之一改为非广播传输。

资源残影已清零：按「`res/**` + `AndroidManifest.xml` + `R.<type>.<name>` + ViewBinding 生成类」的口径全量扫描，字符串、布局、drawable、mipmap、style、array 等各类型均无孤儿（`activity_main`、`items_app` 仅经 ViewBinding 类引用，非孤儿）。
