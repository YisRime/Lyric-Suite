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

    private fun row(@StringRes title: Int, @StringRes summary: Int, value: String? = null): Preferences {
        val preferences = Preferences(context)
        preferences.preferencesTitle.setText(title)
        when {
            value != null -> preferences.setSummary(value)
            summary != 0 -> preferences.setSummary(summary)
            else -> preferences.preferencesSummary.visibility = View.GONE
        }
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
            editNumber(title, get().toString()) { text ->
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
            editNumber(title, get(), numeric = false) { text ->
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

    private fun isColor(value: String): Boolean = try {
        value.toColorInt()
        true
    } catch (_: IllegalArgumentException) {
        false
    }

    private fun editNumber(
        @StringRes title: Int,
        current: String,
        numeric: Boolean = true,
        onValid: (String) -> Unit
    ) {
        val editor = EditTextDialog(context)
            .setTitle(context.getString(title))
            .setText(current)
        if (numeric) editor.setNumericInput()
        editor.show(onValid)
    }
}
