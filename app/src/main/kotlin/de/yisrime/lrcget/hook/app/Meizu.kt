package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.isQQLite
import cn.xiaowine.xkt.Tool.isNot

object Meizu : BaseHook() {

    override fun init() {
        super.init()
        isQQLite {
            HookTools.QQLite()
        }.isNot {
            HookTools.MockFlyme().mock().notificationLyric()
        }
    }
}