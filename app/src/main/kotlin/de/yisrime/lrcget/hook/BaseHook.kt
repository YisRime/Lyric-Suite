package de.yisrime.lrcget.hook

abstract class BaseHook {
    var isInit: Boolean = false
    open fun init() {}
}
