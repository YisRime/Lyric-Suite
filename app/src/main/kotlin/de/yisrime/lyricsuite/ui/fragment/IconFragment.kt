package de.yisrime.lyricsuite.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools.shell

class IconFragment : Fragment() {

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
        screen.switch(R.string.icon_switch, prop = config::iconSwitch) { screen.invalidate() }
        resetRow(screen)
        if (!config.iconSwitch) return
        screen.header(R.string.module_second)
        screen.integer(R.string.icon_size, R.string.icon_size_tips, 0..100, config::iconSize)
        screen.color(
            R.string.icon_color_and_transparency,
            R.string.icon_color_and_transparency_tips,
            "",
            config::iconColor
        )
        screen.color(
            R.string.icon_background_color_and_transparency,
            R.string.icon_background_color_and_transparency_tips,
            "",
            config::iconBgColor
        )
        screen.header(R.string.module_fourth)
        screen.integer(
            R.string.icon_top_margins, R.string.icon_top_margins_tips, 0..100, config::iconTopMargins
        )
        screen.integer(
            R.string.icon_bottom_margins,
            R.string.icon_bottom_margins_tips,
            0..100,
            config::iconBottomMargins
        )
        screen.integer(
            R.string.icon_start_margins,
            R.string.icon_start_margins_tips,
            -2000..2000,
            config::iconStartMargins
        )
        screen.header(R.string.module_third)
        screen.switch(
            R.string.force_the_icon_to_be_displayed,
            prop = config::forceTheIconToBeDisplayed
        )
        screen.text(
            R.string.change_all_icons, R.string.change_all_icons_tips, config::changeAllIcons
        )
    }

    private fun resetRow(screen: ConfigScreen) {
        screen.action(R.string.reset_system_ui) {
            screen.confirm(
                R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
