package de.yisrime.lrcget.ui.fragment

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import cn.xiaowine.xkt.AcTool.restartApp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import cn.xiaowine.xkt.Tool.isNotNull
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.BackupTools
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcbar.tools.ActivityTools
import de.yisrime.lyricsuite.config.DisplayStore
import de.yisrime.lyricsuite.config.GetterStore
import de.yisrime.lrcget.ui.activity.DialogTransparentActivity
import de.yisrime.lrcget.ui.activity.LogActivity
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

    override fun ConfigScreen.content() {
        val context = requireContext()
        header(R.string.options_fetching_header)
        action(R.string.app_rules_fragment_label) { findNavController().navigate(R.id.app_nav) }
        switch(R.string.salt_use_flyme, R.string.salt_use_flyme_summary, prop = config::saltUseFlyme)
        action(R.string.regex_replace) {
            context.startActivity(Intent(context, DialogTransparentActivity::class.java))
        }
        action(R.string.lyric_setting) { findNavController().navigate(R.id.fetch_options_nav) }
        action(R.string.fuck_wyy_about) { findNavController().navigate(R.id.netease_options_nav) }
        header(R.string.options_general_header)
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

}

