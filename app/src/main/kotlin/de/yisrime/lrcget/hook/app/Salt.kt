package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.ConfigTools.xConfig
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.isApi
import cn.xiaowine.xkt.Tool.isNot

object Salt : BaseHook() {

    override fun init() {
        super.init()
        if (xConfig.saltUseFlyme) {
            HookTools.MockFlyme().mock().notificationLyric()
        } else {
            isApi {
                Api.hook()
            }.isNot {
                HookTools.MockFlyme().mock().notificationLyric()
            }
        }
    }
}