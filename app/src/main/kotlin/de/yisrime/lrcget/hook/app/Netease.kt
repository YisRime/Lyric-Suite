package de.yisrime.lrcget.hook.app

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.content.edit
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.ConfigTools.xConfig
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.dexKitBridge
import de.yisrime.lrcget.tool.HookTools.eventTools
import de.yisrime.lrcget.tool.HookTools.mediaMetadataCompatLyric
import de.yisrime.lrcget.tool.MeiZuNotification
import de.yisrime.lrcget.tool.Tools.getVersionCode
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNot
import cn.xiaowine.xkt.Tool.isNotNull
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.setStaticObject
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createBeforeHook
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import java.lang.reflect.Method

private fun Method.replaceName() {
    this.createHook {
        after {
            when (it.args[0].toString()) {
                "FLAG_ALWAYS_SHOW_TICKER" -> it.result =
                    MeiZuNotification::class.java.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK")

                "FLAG_ONLY_UPDATE_TICKER" -> it.result =
                    MeiZuNotification::class.java.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK")
            }
        }
    }
}

@SuppressLint("StaticFieldLeak")
object Netease : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    override fun init() {
        super.init()

        if (xConfig.fuckWyy2) {
            try {
                loadClass("android.app.Instrumentation").methodFinder()
                    .filterByName("newApplication")
                    .filterByParamTypes(
                        ClassLoader::class.java,
                        String::class.java,
                        Context::class.java
                    )
                    .single()
                    .createBeforeHook {
                        if ("com.netease.nis.wrapper.MyApplication" == it.args[1]) {
                            it.args[1] = "com.netease.cloudmusic.CloudMusicApplication"
                            "Hooked netease wrapper class".log()
                        }
                    }
            } catch (_: ClassNotFoundException) {
                "Netease wrapper application not found".log()
            }
        }

        loadClass("android.os.SystemProperties").methodFinder().first { name == "get" }.createHook {
            after {
                setStaticObject(Build::class.java, "DISPLAY", "Flyme")
            }
        }

        Class::class.java.methodFinder().first { name == "getField" }.replaceName()
        Class::class.java.methodFinder().first { name == "getDeclaredField" }.replaceName()
        HookTools.getApplication {
            val verCode =
                it.packageManager?.getPackageInfo(it.packageName, 0)?.getVersionCode() ?: 0
            if (verCode >= 8000041 || it.packageName == "com.hihonor.cloudmusic") {
                dexKitBridge(it.classLoader) { dexKitBridge ->
                    loadClass(
                        "androidx.core.app.NotificationManagerCompat",
                        it.classLoader
                    ).methodFinder().first { name == "notify" }.createHook {
                        after {
                            val notification = it.args[1] as Notification
                            val charSequence = notification.tickerText
                            val isLyric =
                                notification.flags and MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER != 0 || notification.flags and MeiZuNotification.FLAG_ONLY_UPDATE_TICKER != 0
                            if (isLyric) {
                                charSequence.isNotNull {
                                    eventTools.sendLyric(charSequence.toString())
                                }.isNot {
                                    eventTools.cleanLyric()
                                }
                            }
                        }
                    }
                    if (xConfig.fuckWyy) {
                        val sbNetease = dexKitBridge.findClass {
                            matcher {
                                usingStrings("com/netease/cloudmusic/module/lyric/flyme/StatusBarLyricSettingManager.class:setSwitchStatus:(Z)V")
                            }
                        }.single()
                        loadClass(sbNetease.name).methodFinder().filterByParamCount(0)
                            .filterByName("a").first().createHook {
                                after { hookParam ->
                                    val a = hookParam.result as SharedPreferences
                                    a.edit(commit = true) {
                                        putBoolean(
                                            "status_bar_lyric_setting_key",
                                            xConfig.fuckWyy
                                        )
                                    }
                                }
                            }
                    }
                }
            } else {
                mediaMetadataCompatLyric(it.classLoader)
            }
        }
    }
}
