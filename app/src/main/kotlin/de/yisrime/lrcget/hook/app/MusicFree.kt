package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder

object MusicFree : BaseHook() {

    override fun init() {
        super.init()
        val loadClass = loadClass("fun.upup.musicfree.lyricUtil.LyricView")
        loadClass.methodFinder().filterByName("setText").first().createHook {
            before {
                HookTools.eventTools.sendLyric(it.args[0].toString())
                it.result = null
            }
        }
        loadClass.methodFinder().filterByName("showLyricWindow").first().createHook {
            returnConstant(null)
        }
    }
}