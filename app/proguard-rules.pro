-optimizationpasses 5
-dontskipnonpubliclibraryclassmembers
-allowaccessmodification
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*,!code/allocation/variable
-repackageclasses ztlgc
-obfuscationdictionary proguard-dic-6.txt
-classobfuscationdictionary proguard-dic-6.txt
-packageobfuscationdictionary proguard-dic-6.txt
-adaptresourcefilenames proguard-dic-6.txt
-adaptresourcefilecontents proguard-dic-6.txt

# 框架按类名反射构造模块入口，两个入口都在 java_init.list 里逐字列出
-keep class de.yisrime.lrcbar.hook.MainHook { <init>(); }
-keep class de.yisrime.lrcget.hook.MainHook { <init>(); }

# 跨进程 Parcelable 按类名反查，收发两侧不得改名
-keep class cn.lyric.getter.api.data.* { *; }

# 取词侧按名字反射宿主类与通知实现
-keep class de.yisrime.lrcget.tool.MeiZuNotification { *; }
-keep class de.yisrime.lrcget.hook.app.Netease { *; }
