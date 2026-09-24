package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools.eventTools
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import java.util.Arrays

object Huawei : BaseHook() {
    override fun init() {
        super.init()
        //// 让程序以为连接了蓝牙
        loadClass("com.android.mediacenter.localmusic.VehicleLyricControl").methodFinder()
            .first { name == "isEnableRefreshShowLyric" }
            .createHook {
                after { param ->
                    val resultIntent = param.result as Boolean
                    //resultIntent.log()
                }
                before {
                    val field = it.thisObject.javaClass.getDeclaredField("mIsBluetoothA2dpConnect")
                    field.isAccessible = true // 确保可以访问私有字段
                    field.setBoolean(it.thisObject, true) // 设置值为 true
                }
            }


        loadClass("com.android.mediacenter.localmusic.MediaSessionController").methodFinder()
            .first { name == "updateLyric" }
            .createHook {
                before {
                    val lyric = it.args
                    val lyricWithoutBrackets = Arrays.toString(lyric).substring(1, Arrays.toString(lyric).length - 1)
                    //lyricWithoutBrackets.log()
                    eventTools.sendLyric(lyricWithoutBrackets)
                }
            }
    }
}
