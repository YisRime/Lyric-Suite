package de.yisrime.lrcget.hook.app

import android.os.Build
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.dexKitBridge
import de.yisrime.lrcget.tool.Tools.getVersionCode
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.setStaticObject
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder

object Oppo : BaseHook() {
    init {
        System.loadLibrary("dexkit")
    }

    override fun init() {

        /** 绕过oppo设备限制 */
        loadClass("android.os.SystemProperties").methodFinder().first { name == "get" }.createHook {
            after {
                setStaticObject(Build::class.java, "BRAND", "oppo")
                setStaticObject(Build::class.java, "MANUFACTURER", "Oppo")
                setStaticObject(Build::class.java, "DISPLAY", "Color")

            }
        }
        HookTools.mediaMetadataCompatLyric()
        HookTools.getApplication { app ->
            /** 版本号获取预留 不同软件包名分离方便未来调整hook规则*/
            val verCode: Int = app.packageManager?.getPackageInfo(app.packageName, 0)?.getVersionCode() ?: 0
            dexKitBridge(app.classLoader) { dexKitBridge ->
                when (app.packageName) {
                    "com.heytap.music" -> {
                        /** 强开蓝牙歌词 */
                        loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first { name == "l" }.createHook {
                            after { params ->
                                params.result = true
                            }
                        }
                    }

                    "com.oppo.music" -> {
                        /** 强开蓝牙歌词 */
                        loadClass("com.allsaints.music.player.thirdpart.MediaSessionHelper").methodFinder().first { name == "l" }.createHook {
                            after { params ->
                                params.result = true
                            }
                        }
                    }
                }
            }
        }
    }
}