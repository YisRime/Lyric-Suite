package de.yisrime.lyricsuite.ui.fragment

import androidx.navigation.fragment.findNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.ActivityTestTools.getClass
import de.yisrime.lrcbar.tools.Tools.shell
import de.yisrime.lyricsuite.ui.HookProbe

class TestFragment : ConfigFragment() {

    override val titleRes = R.string.hook_page

    override fun ConfigScreen.content() {
        header(R.string.test_mode_tips)
        switch(R.string.test_mode, prop = config::testMode) { invalidate() }
        if (config.testMode) {
            switch(
                R.string.relax_conditions,
                R.string.relax_conditions_tips,
                prop = config::relaxConditions
            )
            action(R.string.get_hook, R.string.tips1) {
                HookProbe.clear()
                requireContext().getClass()
                findNavController().navigate(R.id.hook_picker_nav)
            }
        }
        action(R.string.reset_system_ui) {
            confirm(
                R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
