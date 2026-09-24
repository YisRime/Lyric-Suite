package de.yisrime.lrcget.hook.app

import android.view.View
import android.widget.TextView
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder

object Luna : BaseHook() {
    private var rightLyric: String = ""
    override fun init() {
        super.init()
        TextView::class.java.methodFinder().filterByName("setText").first().createHook {
            after {
                if (it.thisObject::class.java.simpleName in listOf("LyricTextView", "MarqueeLastLineLyricTextView")) {
                    if (rightLyric.isNotEmpty()) {
                        HookTools.eventTools.sendLyric(rightLyric)
                    }
                    rightLyric = it.args[0].toString()
                }
            }
        }
        HookTools.dexKitBridge {
            val clazz = it.findMethod {
                searchPackages = listOf("com.luna.biz.playing.lyric.floatinglyrics.view")
                matcher {
                    addAnnotation {
                        addEqString("android.view.LayoutInflater")
                    }
                    paramCount = 3
                    returnType = View::class.java.name
                }
            }.single()
            loadClass(clazz.declaredClassName).methodFinder().filterByName(clazz.name).first().createHook {
                after { param ->
                    (param.result as View).visibility = View.GONE
                }
            }
        }
    }
}