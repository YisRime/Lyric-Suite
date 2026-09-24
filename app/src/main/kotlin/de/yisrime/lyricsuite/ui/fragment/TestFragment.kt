package de.yisrime.lyricsuite.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.data.Data
import de.yisrime.lrcbar.tools.ActivityTestTools.getClass
import de.yisrime.lrcbar.tools.Tools.shell

class TestFragment : Fragment() {

    private lateinit var screen: ConfigScreen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        screen = ConfigScreen(requireContext())
        return screen.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        screen.build { build(it) }
    }

    private fun build(screen: ConfigScreen) {
        screen.header(R.string.test_mode_tips)
        screen.switch(R.string.test_mode, prop = config::testMode) { screen.invalidate() }
        if (config.testMode) {
            screen.switch(
                R.string.relax_conditions,
                R.string.relax_conditions_tips,
                prop = config::relaxConditions
            )
            screen.action(R.string.get_hook, R.string.tips1) {
                requireContext().getClass()
                findNavController().navigate(R.id.hook_picker_nav)
            }
        }
        screen.action(R.string.reset_system_ui) {
            screen.confirm(
                R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
