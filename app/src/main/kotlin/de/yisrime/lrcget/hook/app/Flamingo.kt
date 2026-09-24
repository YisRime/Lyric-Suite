package de.yisrime.lrcget.hook.app


import de.yisrime.lyricsuite.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools.isApi

object Flamingo : BaseHook() {

    override fun init() {
        super.init()
        isApi {
            Api.hook()
        }
    }
}
