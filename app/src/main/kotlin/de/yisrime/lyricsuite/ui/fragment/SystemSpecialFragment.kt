package de.yisrime.lyricsuite.ui.fragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools.shell

class SystemSpecialFragment : Fragment() {

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
        screen.header(R.string.miui_and_hyperos)
        screen.switch(R.string.miui_hide_network_speed, prop = config::mMiuiHideNetworkSpeed)
        screen.switch(R.string.miui_pad_optimize, prop = config::mMiuiPadOptimize)
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            screen.switch(R.string.hide_carrier, prop = config::hideCarrier)
        }
        screen.header(R.string.hyperos)
        screen.switch(R.string.automate_focused_notice, prop = config::automateFocusedNotice)
        screen.switch(R.string.hyperos_texture, prop = config::mHyperOSTexture) {
            screen.invalidate()
        }
        if (config.mHyperOSTexture) {
            screen.integer(
                R.string.hyperos_texture_radio,
                R.string.lyric_stroke_width_tips,
                0..1000,
                config::mHyperOSTextureRadio
            )
            screen.integer(
                R.string.hyperos_texture_corner,
                R.string.lyric_letter_spacing_tips,
                0..1000,
                config::mHyperOSTextureCorner
            )
            screen.color(
                R.string.hyperos_texture_color,
                R.string.lyric_color_and_transparency_tips,
                "#15818181",
                config::mHyperOSTextureBgColor
            )
        }
        screen.action(R.string.reset_system_ui) {
            screen.confirm(
                R.string.reset_system_ui,
                R.string.restart_systemui_tips,
                R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
