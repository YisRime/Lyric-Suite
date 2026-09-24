package de.yisrime.lrcget.hook.app

import android.content.Context
import android.content.Intent
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.eventTools
import de.yisrime.lrcget.tool.HookTools.fuckTinker
import de.yisrime.lrcget.tool.HookTools.getProcessName
import de.yisrime.lrcget.tool.Tools.getVersionCode
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.core.ClassLoaderProvider.classLoader
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import io.github.kyuubiran.ezxhelper.core.util.ObjectUtil.getObject

object Kugou : BaseHook() {
    override fun init() {
        super.init()
        fuckTinker()
        HookTools.openBluetoothA2dpOn()
        HookTools.getApplication { app ->
            val verCode: Int = app.packageManager?.getPackageInfo(app.packageName, 0)?.getVersionCode() ?: 0
            when (app.packageName) {
                "com.kugou.android" -> {
                    if (getProcessName(app) == "com.kugou.android.support") {
                        when {
                            verCode <= 10000 -> hookCarLyric()
                        }
                    }
                    if (getProcessName(app) == "com.kugou.android.support") return@getApplication
                    when {
                        verCode <= 10000 -> HookTools.MockFlyme().mock()
                        verCode <= 12009 -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                        }

                        else -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                            fixProbabilityCollapse()
                        }
                    }
                }

                "com.kugou.android.lite" -> {
                    if (getProcessName(app) == "com.kugou.android.lite.support") return@getApplication
                    when {
                        verCode <= 10935 -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("android.support.v4.content.LocalBroadcastManager")
                            fixProbabilityCollapse()
                        }

                        else -> {
                            HookTools.MockFlyme().mock()
                            hookLocalBroadcast("androidx.localbroadcastmanager.content.LocalBroadcastManager")
                            fixProbabilityCollapse()
                        }
                    }
                }
            }
        }
    }


    private fun hookCarLyric() {
        loadClass("com.kugou.framework.player.c").methodFinder()
            .filterByParamTypes(HashMap::class.java).first { name == "a" }
            .createHook {
                after {
                    val hashMap = it.args[0] as HashMap<*, *>
                    eventTools.sendLyric(hashMap[0].toString())
                }
            }
    }

    // 非常神奇的崩溃点
    private fun fixProbabilityCollapse() {
        loadClass("com.kugou.framework.hack.ServiceFetcherHacker\$FetcherImpl").methodFinder()
            .first { name == "createServiceObject" }
            .createHook {
                after {
                    val mServiceName = getObject(it.thisObject, "serviceName")
                    if (mServiceName == Context.WIFI_SERVICE && it.throwable != null) { // 当有错误抛出时才使用替代方法，防止软件崩溃。
                        it.throwable = null
                        it.result = null
                    }
                }
            }
    }

    private fun hookLocalBroadcast(className: String) {
        loadClass(className, classLoader).methodFinder()
            .first { name == "sendBroadcast" }
            .createHook {
                before {
                    val intent = it.args[0] as Intent
                    val action = intent.action
                    val message = intent.getStringExtra("lyric")
                    //action.log()
                    if (action == "com.kugou.android.update_meizu_lyric") {
                        message?.let { it3 ->
                            eventTools.sendLyric(it3)
                        }
                    }
                }
            }
    }
}
