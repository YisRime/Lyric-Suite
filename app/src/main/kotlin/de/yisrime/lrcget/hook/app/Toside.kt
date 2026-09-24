package de.yisrime.lrcget.hook.app

import android.app.Activity
import android.view.View
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.eventTools
import de.yisrime.lrcget.tool.HookTools.getApplication
import cn.xiaowine.xkt.Tool.isNotNull
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClassOrNull
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import de.yisrime.lrcget.tool.ConfigTools.xConfig as config

object Toside : BaseHook() {

    override fun init() {
        super.init()
        val lyricModuleClass = loadClassOrNull("cn.toside.music.mobile.lyric.LyricModule")
        lyricModuleClass.isNotNull { clazz ->
            if (config.allowSomeSoftwareToOutputAfterTheScreen) {
                getApplication {
                    HookTools.lockNotStopLyric(it.classLoader, arrayListOf("MusicModule"))
                }
            }
            clazz.methodFinder().filterByName("pause").first().createHook {
                after {
                    eventTools.cleanLyric()
                }
            }
            val lyricField = clazz.declaredFields.first { it.name == "lyric" }
            val lyricViewField = lyricField.type.declaredFields.first { it.type.superclass == Activity::class.java }
            val lyricMethod = lyricViewField.type.declaredMethods.first { method -> method.parameterCount == 2 && method.parameterTypes[0] == String::class.java && method.parameterTypes[1] == ArrayList::class.java }
            lyricMethod.createHook {
                after {
                    val lyric = it.args[0] as String
                    eventTools.sendLyric(lyric)
                }
            }

            loadClass("android.view.WindowManagerImpl").methodFinder().first { name == "addView" }.createHook {
                after { view ->
                    if (view.args[0]!!::class.java.name.contains("cn.toside.music.mobile.lyric")) {
                        (view.args[0] as View).visibility = View.GONE
                    }
                }
            }
        }
    }
}