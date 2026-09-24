package de.yisrime.lyricsuite.ui

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.StringRes
import de.yisrime.lrcget.tool.Tools.dp2px
import de.yisrime.lyricsuite.R
import kotlin.reflect.KMutableProperty0

class ConfigScreen(private val context: Context) {
    val rows = ConfigRows(context)
    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }
    val root: View = ScrollView(context).apply {
        addView(container)
    }

    private var block: ((ConfigScreen) -> Unit)? = null

    fun build(content: (ConfigScreen) -> Unit) {
        block = content
        container.removeAllViews()
        content(this)
    }

    fun invalidate() {
        block?.let { build(it) }
    }

    fun header(@StringRes title: Int) {
        container.addView(TextView(context).apply {
            setText(title)
            setTypeface(typeface, Typeface.BOLD)
            val padding = dp2px(context, 16f)
            setPadding(padding, padding, padding, padding / 2)
        })
    }

    fun switch(
        @StringRes title: Int,
        @StringRes summary: Int = 0,
        prop: KMutableProperty0<Boolean>,
        onChange: (Boolean) -> Unit = {}
    ) {
        container.addView(
            rows.switch(title, summary, get = { prop.get() }, set = {
                prop.set(it)
                onChange(it)
            })
        )
    }

    fun action(@StringRes title: Int, onClick: () -> Unit) {
        container.addView(rows.action(title, onClick = onClick))
    }

    fun integer(
        @StringRes title: Int,
        @StringRes tips: Int,
        range: IntRange,
        prop: KMutableProperty0<Int>,
        onWrite: () -> Unit = {}
    ) {
        container.addView(
            rows.integer(title, tips, range.first, range.last, get = { prop.get() }, set = {
                prop.set(it)
                onWrite()
            })
        )
    }

    fun color(
        @StringRes title: Int,
        @StringRes tips: Int,
        fallback: String,
        prop: KMutableProperty0<String>
    ) {
        container.addView(
            rows.color(title, tips, fallback, get = { prop.get() }, set = { prop.set(it) })
        )
    }

    fun gradient(
        @StringRes title: Int,
        @StringRes tips: Int,
        @StringRes hint: Int = 0,
        prop: KMutableProperty0<String>
    ) {
        container.addView(
            rows.gradient(title, tips, hint, get = { prop.get() }, set = { prop.set(it) })
        )
    }

    fun text(
        @StringRes title: Int,
        @StringRes tips: Int,
        prop: KMutableProperty0<String>
    ) {
        container.addView(rows.text(title, tips, get = { prop.get() }, set = { prop.set(it) }))
    }

    fun choice(
        @StringRes title: Int,
        options: List<String>,
        prop: KMutableProperty0<Int>
    ) {
        container.addView(
            rows.choice(title, options, get = { prop.get() }, set = { prop.set(it) })
        )
    }

    fun confirm(
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes affirmative: Int,
        onConfirm: () -> Unit
    ) = rows.confirm(title, message, affirmative, onConfirm)

    fun labels(vararg ids: Int): List<String> = ids.map(context::getString)
}
