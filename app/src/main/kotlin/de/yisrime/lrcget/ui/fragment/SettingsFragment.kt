package de.yisrime.lrcget.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.databinding.FragmentSettingsBinding
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcbar.tools.ActivityTools
import de.yisrime.lrcbar.config.XposedOwnSP.config as barConfig
import de.yisrime.lrcget.ui.activity.DialogTransparentActivity
import de.yisrime.lrcget.ui.activity.LogActivity
import de.yisrime.lrcget.ui.activity.TestActivity

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val context = requireContext()
        ConfigScreen(context, binding.fragmentSettingLinearlayout).apply {
            switch(
                R.string.salt_use_flyme,
                R.string.salt_use_flyme_summary,
                prop = config::saltUseFlyme
            )
            action(R.string.regex_replace) {
                startActivity(Intent(context, DialogTransparentActivity::class.java))
            }
            action(R.string.lyric_setting) { showLyricSettingsDialog(context) }
            action(R.string.fuck_wyy_about) { showFuckWyySettingsDialog(context) }
            action(R.string.lyric_test) {
                startActivity(Intent(context, TestActivity::class.java))
            }
            action(R.string.clear_config) {
                confirm(R.string.clear_config, R.string.clear_config_tips, R.string.ok) {
                    barConfig.clear()
                    Thread {
                        Thread.sleep(500)
                        ActivityTools.restartApp()
                    }.start()
                }
            }
            action(R.string.log_page) {
                startActivity(Intent(context, LogActivity::class.java))
            }
        }
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
