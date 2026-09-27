package de.yisrime.lyricsuite.ui

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.graphics.Typeface
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.color.MaterialColors
import de.yisrime.lrcget.tool.Tools.dp2px
import de.yisrime.lrcget.ui.dialog.EditTextDialog
import de.yisrime.lrcget.ui.view.Preferences
import de.yisrime.lyricsuite.R
import kotlin.reflect.KMutableProperty0

class ConfigScreen(private val context: Context, private val container: LinearLayout) {

    companion object {
        // 渐变色输入的格式示例：起始色,结束色（含 Alpha）
        const val GRADIENT_EXAMPLE = "00000000,FFFFFFFF"
    }


    constructor(context: Context) : this(
        context,
        LinearLayout(context).apply { orientation = LinearLayout.VERTICAL }
    )

    val root: View by lazy {
        ScrollView(context).apply { addView(container) }
    }

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
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
            setTypeface(typeface, Typeface.BOLD)
            setTextColor(MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimary, Color.BLACK))
            val horizontal = dp2px(context, 24f)
            setPadding(horizontal, dp2px(context, 20f), horizontal, dp2px(context, 8f))
        })
    }

    fun switch(
        @StringRes title: Int,
        @StringRes summary: Int = 0,
        prop: KMutableProperty0<Boolean>,
        onChange: (Boolean) -> Unit = {}
    ) {
        val row = row(title, summary)
        row.preferencesButton.visibility = View.VISIBLE
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
        range: IntRange,
        prop: KMutableProperty0<Int>,
        @StringRes unit: Int = 0,
        onWrite: () -> Unit = {}
    ) {
        val message = context.getString(R.string.input_number_prompt, context.getString(title), range.first, range.last)
        editable(title, message, numeric = true, unit = unit, get = { prop.get().toString() }) { text ->
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
        fallback: String,
        prop: KMutableProperty0<String>
    ) {
        val message = context.getString(R.string.input_color_prompt, context.getString(title))
        editable(title, message, get = { prop.get() }) { text ->
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
        prop: KMutableProperty0<String>
    ) {
        val message = context.getString(R.string.input_gradient_prompt, context.getString(title))
        editable(title, message, get = { prop.get() }, emptyPrefill = GRADIENT_EXAMPLE) { text ->
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
        prop: KMutableProperty0<String>
    ) {
        val message = context.getString(R.string.input_text_prompt, context.getString(title))
        editable(title, message, blankAsUnset = true, get = { prop.get() }) { text ->
            prop.set(text)
            true
        }
    }

    fun choice(
        @StringRes title: Int,
        options: List<String>,
        prop: KMutableProperty0<Int>
    ) {
        val row = row(title)
        row.setValue(options[prop.get().coerceIn(options.indices)])
        container.addView(row.getView().apply {
            setOnClickListener {
                MaterialAlertDialogBuilder(context)
                    .setTitle(title)
                    .setItems(options.toTypedArray()) { dialog, which ->
                        prop.set(which)
                        row.setValue(options[which])
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

    fun action(@StringRes title: Int, onClick: () -> Unit) {
        val row = row(title)
        container.addView(row.getView().apply { setOnClickListener { onClick() } })
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
        message: String,
        unit: Int = 0,
        numeric: Boolean = false,
        blankAsUnset: Boolean = false,
        emptyPrefill: String? = null,
        get: () -> String,
        commit: (String) -> Boolean
    ) {
        val shown = { raw: String -> raw.ifEmpty { if (blankAsUnset) context.getString(R.string.none_set) else raw } }
        val row = row(title)
        row.setValue(shown(get()))
        container.addView(row.getView().apply {
            setOnClickListener {
                val editor = EditTextDialog(context)
                    .setTitle(context.getString(title))
                    .apply {
                        if (message.isNotEmpty()) setMessage(message)
                        if (numeric) setNumericInput()
                        if (unit != 0) setUnit(unit)
                    }
                    .setText(get().ifEmpty { emptyPrefill.orEmpty() })
                editor.show { text ->
                    if (commit(text.trim())) row.setValue(shown(get()))
                }
            }
        })
    }

    private fun row(@StringRes title: Int, @StringRes summary: Int = 0): Preferences {
        val row = Preferences(context)
        row.preferencesTitle.setText(title)
        row.preferencesButton.visibility = View.GONE
        // 开关行的警示语保留在页面，值行的说明只在弹窗中展示
        if (summary != 0) row.setSummary(context.getString(summary))
        return row
    }

    private fun isColor(value: String): Boolean = try {
        value.toColorInt()
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    private fun toast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}
