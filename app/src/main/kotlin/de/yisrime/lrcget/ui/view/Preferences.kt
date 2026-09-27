package de.yisrime.lrcget.ui.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.annotation.StringRes
import de.yisrime.lyricsuite.R
import com.google.android.material.materialswitch.MaterialSwitch

class Preferences(context: Context) {
    private val preferencesView: View =
        LayoutInflater.from(context).inflate(R.layout.items_md3_preferences, null)
    val preferencesButton: MaterialSwitch = preferencesView.findViewById(R.id.switchButton)
    val preferencesTitle: TextView = preferencesView.findViewById(R.id.switchTitle)
    private val preferencesSummary: TextView = preferencesView.findViewById(R.id.switchSummary)
    private val preferencesValue: TextView = preferencesView.findViewById(R.id.switchValue)

    // 重建为准
    init {
        preferencesButton.isSaveEnabled = false
    }

    fun getView(): View = preferencesView

    fun setViewClickToggleSwitch() {
        preferencesView.setOnClickListener {
            preferencesButton.isChecked = !preferencesButton.isChecked
        }
    }

    fun setValue(value: String?) {
        if (value.isNullOrBlank()) {
            preferencesValue.visibility = View.GONE
        } else {
            preferencesValue.visibility = View.VISIBLE
            preferencesValue.text = value
        }
    }

    fun setSummary(summary: String?) {
        if (summary.isNullOrBlank()) {
            preferencesSummary.visibility = View.GONE
        } else {
            preferencesSummary.visibility = View.VISIBLE
            preferencesSummary.text = summary
        }
    }

    fun setSummary(@StringRes resId: Int) {
        setSummary(preferencesView.context.getString(resId))
    }
}