package de.yisrime.lrcbar.tools

// 日志功能已移除，调用点保留为无操作；排查问题直接看 LSPosed 自身日志
object LogTools {
    fun Any?.log(): Any? = this
}
