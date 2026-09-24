package de.yisrime.lyricsuite.ui

import android.content.Context
import android.text.InputType
import android.view.View
import androidx.annotation.StringRes
import androidx.core.graphics.toColorInt
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.yisrime.lyricsuite.R
import de.yisrime.lrcget.ui.dialog.EditTextDialog
import de.yisrime.lrcget.ui.view.Preferences
import android.widget.Toast

class ConfigRows(private val context: Context) {

    private fun summaryOf(value: String, @StringRes hint: Int): String =
        if (hint == 0) value else "$value · ${context.getString(hint)}"

    private fun row(
        @StringRes title: Int,
        @StringRes summary: Int,
        value: String? = null,
        @StringRes hint: Int = 0
    ): Preferences {
        val preferences = Preferences(context)
        preferences.preferencesTitle.setText(title)
        val tail = listOfNotNull(
            value ?: if (summary == 0) null else context.getString(summary),
            if (hint == 0) null else context.getString(hint)
        )
        if (tail.isEmpty()) preferences.preferencesSummary.visibility = View.GONE
        else preferences.setSummary(tail.joinToString(" · "))
        return preferences
    }

    fun switch(
        @StringRes title: Int,
        @StringRes summary: Int = 0,
        get: () -> Boolean,
        set: (Boolean) -> Unit
    ): View {
        val preferences = row(title, summary)
        preferences.setSwitchChecked(get())
        preferences.setSwitchListener { _, checked -> set(checked) }
        preferences.setViewClickToggleSwitch()
        return preferences.getView()
    }

    fun action(
        @StringRes title: Int,
        @StringRes summary: Int = 0,
        onClick: () -> Unit
    ): View {
        val preferences = row(title, summary)
        preferences.preferencesButton.visibility = View.GONE
        preferences.getView().setOnClickListener { onClick() }
        return preferences.getView()
    }

    fun integer(
        @StringRes title: Int,
        @StringRes tips: Int,
        min: Int,
        max: Int,
        get: () -> Int,
        set: (Int) -> Unit
    ): View {
        val preferences = row(title, tips, get().toString())
        preferences.preferencesButton.visibility = View.GONE
        preferences.getView().setOnClickListener {
            editNumber(title, tips, get().toString()) { text ->
                val value = text.trim().toIntOrNull()
                if (value == null || value !in min..max) {
                    Toast.makeText(context, context.getString(R.string.value_error, min, max), Toast.LENGTH_SHORT).show()
                } else {
                    set(value)
                    preferences.setSummary(value.toString())
                }
            }
        }
        return preferences.getView()
    }

    fun color(
        @StringRes title: Int,
        @StringRes tips: Int,
        fallback: String,
        get: () -> String,
        set: (String) -> Unit
    ): View {
        val preferences = row(title, tips, get())
        preferences.preferencesButton.visibility = View.GONE
        preferences.getView().setOnClickListener {
            editNumber(title, tips, get(), numeric = false) { text ->
                val value = text.trim()
                if (value.isNotEmpty() && !isColor(value)) {
                    Toast.makeText(context, R.string.color_error, Toast.LENGTH_SHORT).show()
                } else {
                    val stored = value.ifEmpty { fallback }
                    set(stored)
                    preferences.setSummary(stored)
                }
            }
        }
        return preferences.getView()
    }

    fun confirm(
        @StringRes title: Int,
        @StringRes message: Int,
        @StringRes affirmative: Int,
        onConfirm: () -> Unit
    ) {
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(affirmative) { dialog, _ ->
                dialog.dismiss()
                onConfirm()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    fun text(
        @StringRes title: Int,
        @StringRes tips: Int,
        get: () -> String,
        set: (String) -> Unit
    ): View {
        val preferences = row(title, tips, get().ifEmpty { context.getString(R.string.none_set) })
        preferences.preferencesButton.visibility = View.GONE
        preferences.getView().setOnClickListener {
            editNumber(title, tips, get(), numeric = false) { value ->
                set(value.trim())
                preferences.setSummary(value.trim().ifEmpty { context.getString(R.string.none_set) })
            }
        }
        return preferences.getView()
    }

    fun choice(
        @StringRes title: Int,
        options: List<String>,
        get: () -> Int,
        set: (Int) -> Unit
    ): View {
        val preferences = row(title, 0, options[get().coerceIn(options.indices)])
        preferences.preferencesButton.visibility = View.GONE
        preferences.getView().setOnClickListener {
            MaterialAlertDialogBuilder(context)
                .setTitle(title)
                .setItems(options.toTypedArray()) { dialog, which ->
                    set(which)
                    preferences.setSummary(options[which])
                    dialog.dismiss()
                }
                .show()
        }
        return preferences.getView()
    }

    fun gradient(
        @StringRes title: Int,
        @StringRes tips: Int,
        @StringRes hint: Int = 0,
        get: () -> String,
        set: (String) -> Unit
    ): View {
        val preferences = row(title, tips, get(), hint)
        preferences.preferencesButton.visibility = View.GONE
        preferences.getView().setOnClickListener {
            editNumber(title, tips, get(), numeric = false) { text ->
                val parts = text.trim().split(',').map { it.trim() }.filter { it.isNotEmpty() }
                if (parts.any { !isColor(it) }) {
                    Toast.makeText(context, R.string.color_error, Toast.LENGTH_SHORT).show()
                } else {
                    val stored = parts.joinToString(",")
                    set(stored)
                    preferences.setSummary(summaryOf(stored, hint))
                }
            }
        }
        return preferences.getView()
    }

    private fun isColor(value: String): Boolean = try {
        value.toColorInt()
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    private fun editNumber(
        @StringRes title: Int,
        @StringRes tips: Int,
        current: String,
        numeric: Boolean = true,
        onValid: (String) -> Unit
    ) {
        val editor = EditTextDialog(context)
            .setTitle(context.getString(title))
            .apply { if (tips != 0) setMessage(tips) }
            .setText(current)
        if (numeric) editor.setNumericInput()
        editor.show(onValid)
    }
}
