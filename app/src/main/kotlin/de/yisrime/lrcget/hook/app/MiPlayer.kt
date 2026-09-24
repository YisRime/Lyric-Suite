package de.yisrime.lrcget.hook.app

import de.yisrime.lyricsuite.hook.BaseHook
import de.yisrime.lrcget.tool.HookTools
import de.yisrime.lrcget.tool.HookTools.isQQLite

object MiPlayer : BaseHook() {

    override fun init() {
        super.init()
        isQQLite {
            HookTools.QQLite()
        }
    }
}