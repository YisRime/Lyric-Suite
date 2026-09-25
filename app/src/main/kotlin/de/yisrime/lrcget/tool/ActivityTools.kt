package de.yisrime.lrcget.tool

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import de.yisrime.lrcget.data.AppRules
import de.yisrime.lrcget.tool.JsonTools.parseJSON

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    lateinit var context: Context
    lateinit var application: Application

    val rules: AppRules by lazy {
        context.assets.open("app_rules.json").use { it.reader().parseJSON<AppRules>() }
    }
}
