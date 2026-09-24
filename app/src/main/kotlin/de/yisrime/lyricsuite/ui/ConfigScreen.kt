package de.yisrime.lyricsuite.ui

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.yisrime.lrcget.tool.Tools.dp2px
import de.yisrime.lrcget.ui.dialog.EditTextDialog
import de.yisrime.lrcget.ui.view.Preferences
import de.yisrime.lrcbar.tools.Tools.shell
import de.yisrime.lyricsuite.R
import kotlin.reflect.KMutableProperty0

class ConfigScreen(private val context: Context) {
    private val container = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
    }
    val root: View = ScrollView(context).apply { addView(container) }

    private var content: ((ConfigScreen) -> Unit)? = null

    fun build(content: (ConfigScreen) -> Unit) {
        this.content = content
        container.removeAllViews()
        content(this)
    }

    fun invalidate() {
        content?.let { build(it) }
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
        val row = row(title, summary)
        row.preferencesButton.isChecked = prop.get()
        row.preferencesButton.setOnCheckedChangeListener { _, checked ->
            prop.set(checked)
            onChange(checked)
        }
        row.setViewClickToggleSwitch()
        container.addView(row.getView())
    }

    fun integer(
        @StringRes title: Int,
        @StringRes tips: Int,
        range: IntRange,
        prop: KMutableProperty0<Int>,
        onWrite: () -> Unit = {}
    ) {
        editable(title, tips, numeric = true, get = { prop.get().toString() }) { text ->
            val value = text.toIntOrNull()
            if (value == null || value !in range) {
                toast(context.getString(R.string.value_error, range.first, range.last))
                false
            } else {
                prop.set(value)
                onWrite()
                true
            }
        }
    }

    fun color(
        @StringRes title: Int,
        @StringRes tips: Int,
        fallback: String,
        prop: KMutableProperty0<String>
    ) {
        editable(title, tips, get = { prop.get() }) { text ->
            if (text.isNotEmpty() && !isColor(text)) {
                toast(context.getString(R.string.color_error))
                false
            } else {
                prop.set(text.ifEmpty { fallback })
                true
            }
        }
    }

    fun gradient(
        @StringRes title: Int,
        @StringRes tips: Int,
        @StringRes hint: Int = 0,
        prop: KMutableProperty0<String>
    ) {
        editable(title, tips, hint, get = { prop.get() }) { text ->
            val parts = text.split(',').map { it.trim() }.filter { it.isNotEmpty() }
            if (parts.any { !isColor(it) }) {
                toast(context.getString(R.string.color_error))
                false
            } else {
                prop.set(parts.joinToString(","))
                true
            }
        }
    }

    fun text(
        @StringRes title: Int,
        @StringRes tips: Int,
        prop: KMutableProperty0<String>
    ) {
        editable(title, tips, blankAsUnset = true, get = { prop.get() }) { text ->
            prop.set(text)
            true
        }
    }

    fun choice(
        @StringRes title: Int,
        options: List<String>,
        prop: KMutableProperty0<Int>
    ) {
        val row = row(title, 0, options[prop.get().coerceIn(options.indices)])
        container.addView(row.getView().apply {
            setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setItems(options.toTypedArray()) { dialog, which ->
                        prop.set(which)
                        row.setSummary(options[which])
                        dialog.dismiss()
                    }
                    .show()
            }
        })
    }

    fun entry(title: String, summary: String, onClick: () -> Unit) {
        val row = Preferences(context)
        row.preferencesTitle.text = title
        row.setSummary(summary)
        row.preferencesButton.visibility = View.GONE
        container.addView(row.getView().apply { setOnClickListener { onClick() } })
    }

    fun action(@StringRes title: Int, @StringRes hint: Int = 0, onClick: () -> Unit) {
        val row = row(title, 0, hint = hint)
        container.addView(row.getView().apply { setOnClickListener { onClick() } })
    }

    fun resetSystemUiRow() {
        action(R.string.reset_system_ui) {
            confirm(R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok) {
                shell("killall com.android.systemui", true)
            }
        }
    }

    fun confirm(
        @StringRes title: Int,
        @StringRes message: Int = 0,
        @StringRes affirmative: Int,
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .apply { if (message != 0) setMessage(message) }
            .setPositiveButton(affirmative) { dialog, _ ->
                dialog.dismiss()
                onConfirm()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun labels(vararg ids: Int): List<String> = ids.map(context::getString)

    private fun editable(
        @StringRes title: Int,
        @StringRes tips: Int,
        @StringRes hint: Int = 0,
        numeric: Boolean = false,
        blankAsUnset: Boolean = false,
        get: () -> String,
        commit: (String) -> Boolean
    ) {
        val shown = { raw: String -> raw.ifEmpty { if (blankAsUnset) context.getString(R.string.none_set) else raw } }
        val row = row(title, tips, shown(get()), hint)
        container.addView(row.getView().apply {
            setOnClickListener {
                val editor = EditTextDialog(context)
                    .setTitle(context.getString(title))
                    .apply { if (tips != 0) setMessage(tips) }
                    .setText(get())
                if (numeric) editor.setNumericInput()
                editor.show { text ->
                    if (commit(text.trim())) row.setSummary(shown(get()).let { summaryOf(it, hint) })
                }
            }
        })
    }

    private fun row(
        @StringRes title: Int,
        @StringRes summary: Int,
        value: String? = null,
        @StringRes hint: Int = 0
    ): Preferences {
        val row = Preferences(context)
        row.preferencesTitle.setText(title)
        val tail = listOfNotNull(
            value ?: if (summary == 0) null else context.getString(summary),
            if (hint == 0) null else context.getString(hint)
        )
        if (tail.isEmpty()) row.preferencesSummary.visibility = View.GONE
        else row.setSummary(tail.joinToString(" · "))
        return row
    }

    private fun summaryOf(value: String, @StringRes hint: Int) =
        if (hint == 0) value else "$value · ${context.getString(hint)}"

    private fun isColor(value: String): Boolean = try {
        value.toColorInt()
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    private fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
