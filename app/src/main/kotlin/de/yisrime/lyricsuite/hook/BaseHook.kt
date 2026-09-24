package de.yisrime.lyricsuite.hook

abstract class BaseHook {
    var isInit: Boolean = false
    open fun init() {}
}
