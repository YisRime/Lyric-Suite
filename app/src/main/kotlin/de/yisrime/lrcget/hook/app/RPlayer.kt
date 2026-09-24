package de.yisrime.lrcget.hook.app

import android.content.Context
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import cn.xiaowine.xkt.Tool.isNotNull
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil.loadClassOrNull
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder
import de.yisrime.lrcget.tool.ConfigTools.xConfig as config

object RPlayer : BaseHook() {

    override fun init() {
        super.init()
        loadClassOrNull("com.stub.StubApp").isNotNull {
            it.methodFinder().first { name == "attachBaseContext" }.createHook {
                after { param ->
                    val context = param.args[0] as Context
                    val classLoader = context.classLoader
                    HookTools.mediaMetadataCompatLyric(classLoader)
                    if (config.allowSomeSoftwareToOutputAfterTheScreen) HookTools.lockNotStopLyric(classLoader)
                }
            }
        }
    }
}