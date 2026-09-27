package de.yisrime.lrcget.ui.fragment

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import cn.xiaowine.xkt.AcTool.restartApp
import cn.xiaowine.xkt.Tool.isNotNull
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.BackupTools
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcget.ui.activity.DialogTransparentActivity
import de.yisrime.lrcget.ui.activity.TestActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SettingsFragment : ConfigFragment() {

    private var recoveryPickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        it.isNotNull { uri ->
            if (BackupTools.handleReadDocument(requireActivity(), uri)) {
                MaterialAlertDialogBuilder(requireContext()).apply {
                    setTitle(R.string.recovery)
                    setCancelable(false)
                    setMessage(R.string.recovery_summary)
                    setPositiveButton(R.string.restart) { _, _ ->
                        restartApp()
                    }
                    show()
                }
            }
        }
    }

    private var backupPickerLauncher = registerForActivityResult(ActivityResultContracts.CreateDocument("application/json")) {
        it.isNotNull { uri ->
            BackupTools.handleCreateDocument(requireActivity(), uri)
        }
    }

    override val titleRes = R.string.settings_fragment_label

    override val showToolbar = false

    override fun ConfigScreen.content() {
        val context = requireContext()
        header(R.string.options_fetching_header)
        switch(R.string.salt_use_flyme, R.string.salt_use_flyme_summary, prop = config::saltUseFlyme)
        action(R.string.regex_replace) {
            context.startActivity(Intent(context, DialogTransparentActivity::class.java))
        }
        switch(R.string.output_repeated_lyrics, prop = config::outputRepeatedLyrics)
        switch(
            R.string.allow_some_software_to_output_after_the_screen,
            prop = config::allowSomeSoftwareToOutputAfterTheScreen
        )
        switch(R.string.fuck_fuck_wyy, R.string.fuck_fuck_wyy_tips, prop = config::fuckWyy2)
        switch(R.string.fuck_wyy, R.string.fuck_wyy_tips, prop = config::fuckWyy)
        header(R.string.options_general_header)
        action(R.string.app_rules_fragment_label) { findNavController().navigate(R.id.app_nav) }
        action(R.string.hook_page) { findNavController().navigate(R.id.test_nav) }
        action(R.string.lyric_test) {
            context.startActivity(Intent(context, TestActivity::class.java))
        }
        action(R.string.backup) {
            backupPickerLauncher.launch(
                "LyricSuite_Backup_${
                    SimpleDateFormat("yyyy-MM-dd-HH:mm", Locale.getDefault()).format(Date())
                }.json"
            )
        }
        action(R.string.recovery) { recoveryPickerLauncher.launch("application/json") }
    }
}
