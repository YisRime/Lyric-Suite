package de.yisrime.lrcget.hook.app

import android.content.Context
import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import cn.xiaowine.xkt.Tool.isNotNull
import io.github.kyuubiran.ezxhelper.core.util.ClassUtil
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory.`-Static`.createHook
import io.github.kyuubiran.ezxhelper.core.finder.MethodFinder.`-Static`.methodFinder

object Qinalt : BaseHook() {

    override fun init() {
        super.init()
        ClassUtil.loadClassOrNull("com.stub.StubApp").isNotNull {
            it.methodFinder().first { name == "attachBaseContext" }.createHook {
                after { param ->
                    val context = param.args[0] as Context
                    val classLoader = context.classLoader
                    HookTools.MockFlyme(classLoader).mock().notificationLyric()
                }
            }
        }
    }
}