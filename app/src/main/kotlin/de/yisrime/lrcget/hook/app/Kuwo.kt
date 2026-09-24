package de.yisrime.lrcget.hook.app

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.dexKitBridge
import de.yisrime.lrcget.tool.HookTools.eventTools
import cn.xiaowine.xkt.LogTool.log
import cn.xiaowine.xkt.Tool.isNot
import cn.xiaowine.xkt.Tool.isNotNull
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClass
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClassOrNull
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import java.util.Timer
import java.util.TimerTask

@SuppressLint("StaticFieldLeak")
object Kuwo : BaseHook() {
    val audioManager by lazy { context.getSystemService(Context.AUDIO_SERVICE) as AudioManager }

    lateinit var context: Context

    private var timer: Timer? = null
    private var isRunning = false
    private fun startTimer() {
        if (isRunning) return
        timer = Timer()
        timer?.schedule(object : TimerTask() {
            override fun run() {
                if (!audioManager.isMusicActive) {
                    eventTools.cleanLyric()
                    stopTimer()
                }
            }
        }, 0, 1000)
        isRunning = true
    }

    private fun stopTimer() {
        if (!isRunning) return
        timer?.cancel()
        isRunning = false
    }


    override fun init() {
        super.init()
        HookTools.getApplication {
            context = it
            loadClassOrNull("cn.kuwo.mod.playcontrol.RemoteControlLyricMgr").isNotNull { clazz ->
                clazz.methodFinder().first { name == "updateLyricText" }.createHook {
                    after { param ->
                        startTimer()
                        eventTools.sendLyric(param.args[0].toString())
                    }
                }
            }.isNot {
                dexKitBridge(context.classLoader) { dexKitBridge ->
                    val result = dexKitBridge.findMethod {
                        matcher {
                            addEqString("bluetooth_car_lyric")
                            returnType = "void"

                        }
                        excludePackages = arrayListOf("cn.kuwo.ui")
                    }
                    result.forEach { res ->
                        res.declaredClassName.log()
                        loadClass(res.declaredClassName).methodFinder().first { name == res.name }.createHook {
                            after { hookParam ->
                                startTimer()
                                eventTools.sendLyric(hookParam.args[0].toString())
                            }
                        }
                        HookTools.openBluetoothA2dpOn()
                    }
                }
            }
        }
    }
}