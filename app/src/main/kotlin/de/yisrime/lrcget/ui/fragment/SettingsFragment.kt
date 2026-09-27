package de.yisrime.lrcget.ui.fragment

import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcbar.tools.ActivityTools
import de.yisrime.lyricsuite.config.DisplayStore
import de.yisrime.lyricsuite.config.GetterStore
import de.yisrime.lrcget.ui.activity.DialogTransparentActivity
import de.yisrime.lrcget.ui.activity.LogActivity
import de.yisrime.lrcget.ui.activity.TestActivity

class SettingsFragment : ConfigFragment() {

    override val titleRes = R.string.settings_fragment_label

    override fun ConfigScreen.content() {
        val context = requireContext()
        header(R.string.options_fetching_header)
        switch(R.string.salt_use_flyme, R.string.salt_use_flyme_summary, prop = config::saltUseFlyme)
        action(R.string.regex_replace) {
            context.startActivity(Intent(context, DialogTransparentActivity::class.java))
        }
        action(R.string.lyric_setting) { showLyricSettingsDialog(context) }
        action(R.string.fuck_wyy_about) { showFuckWyySettingsDialog(context) }
        header(R.string.options_general_header)
        action(R.string.app_rules_fragment_label) { findNavController().navigate(R.id.app_nav) }
        action(R.string.lyric_test) {
            context.startActivity(Intent(context, TestActivity::class.java))
        }
        action(R.string.clear_config) {
            confirm(R.string.clear_config, R.string.clear_config_tips, R.string.ok) {
                DisplayStore.clearAll()
                GetterStore.clearAll()
                restartAfterClear()
            }
        }
        action(R.string.log_page) {
            context.startActivity(Intent(context, LogActivity::class.java))
        }
    }

    private fun restartAfterClear() {
        Thread {
            Thread.sleep(500)
            ActivityTools.restartApp()
        }.start()
    }

    private fun showLyricSettingsDialog(context: Context) {
        showDialog(context, R.string.lyric_setting) {
            switch(R.string.output_repeated_lyrics, prop = config::outputRepeatedLyrics)
            switch(
                R.string.allow_some_software_to_output_after_the_screen,
                prop = config::allowSomeSoftwareToOutputAfterTheScreen
            )
        }
    }

    private fun showFuckWyySettingsDialog(context: Context) {
        showDialog(context, R.string.fuck_wyy_about) {
            switch(R.string.fuck_fuck_wyy, R.string.fuck_fuck_wyy_tips, prop = config::fuckWyy2)
            switch(R.string.fuck_wyy, R.string.fuck_wyy_tips, prop = config::fuckWyy)
        }
    }

    private fun showDialog(context: Context, @StringRes title: Int, rows: ConfigScreen.() -> Unit) {
        val screen = ConfigScreen(context)
        screen.build { it.rows() }
        MaterialAlertDialogBuilder(context)
            .setTitle(title)
            .setView(screen.root)
            .show()
    }
}
