package de.yisrime.lrcget.hook.app

import de.yisrime.lyricsuite.hook.BaseHook
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.isApi
import cn.xiaowine.xkt.Tool.isNot

object Salt : BaseHook() {

    override fun init() {
        super.init()
        if (config.saltUseFlyme) {
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