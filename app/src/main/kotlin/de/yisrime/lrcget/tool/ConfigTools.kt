package de.yisrime.lrcget.tool

import android.annotation.SuppressLint
import de.yisrime.lrcget.config.Config

@SuppressLint("StaticFieldLeak")
object ConfigTools {
    val config: Config by lazy { Config() }
    val xConfig: Config by lazy { Config() }
}
