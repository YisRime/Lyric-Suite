package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools.eventTools
import de.yisrime.lrcget.tool.HookTools.extractValues
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder

object Poweramp : BaseHook() {

    override fun init() {
        super.init()
        val clazz = loadClass("com.maxmpz.widget.player.list.LyricsFastTextView")
        val method = MethodFinder.fromClass(clazz)
            .filter {
                val params = parameterTypes
                params.size >= 4 && params[1] == Boolean::class.java && params[2] == Int::class.java && params[3] == Int::class.java
            }
            .single()
        method.createHook {
            before {
                //"返回 ${it.result}".log()
                //"l0传入 ${Arrays.toString(it.args)}".log()
                val xc = it.args[0] // `XC` 参数，包含歌词信息
                val c = it.args[2] //判断歌词是否为现在的
                val a = xc.toString()
                val b = extractValues(a, "text")
                if (!b.isNullOrEmpty()) {
                    if (b != "null") {
                        if (c != 0) {
                            eventTools.sendLyric(b)
                        }
                    } else {
                        eventTools.cleanLyric()
                    }

                }
            }
        }
    }
}

