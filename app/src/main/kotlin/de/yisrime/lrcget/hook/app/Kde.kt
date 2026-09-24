package de.yisrime.lrcget.hook.app

import de.yisrime.lrcget.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.mediaMetadataCompatLyric

object Kde : BaseHook() {
    override fun init() {
        super.init()
        HookTools.getApplication {
            mediaMetadataCompatLyric(it.classLoader)
        }
    }
}