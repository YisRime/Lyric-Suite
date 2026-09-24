package de.yisrime.lrcget.hook.app


import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools.isApi

object Flamingo : BaseHook() {

    override fun init() {
        super.init()
        isApi {
            Api.hook()
        }
    }
}
