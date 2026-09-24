package de.yisrime.lrcget.hook.app

import de.yisrime.lyricsuite.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools

object Mimicry : BaseHook() {
    override fun init() {
        // super.init()
        HookTools.MockFlyme().notificationLyric()
    }
}