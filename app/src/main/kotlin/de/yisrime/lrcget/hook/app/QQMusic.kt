package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools

object QQMusic : BaseHook() {
    override fun init() {
        super.init()
        HookTools.MockFlyme().mock().notificationLyric()
    }
}