package de.yisrime.lyricsuite.ui.fragment

import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigRows
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools.shell
import de.yisrime.lrcget.tool.Tools.dp2px

class SystemSpecialFragment : Fragment() {

    private lateinit var container: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val scroll = ScrollView(requireContext())
        this.container = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
        }
        scroll.addView(this.container)
        return scroll
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rebuild()
    }

    private fun rebuild() {
        val context = requireContext()
        val rows = ConfigRows(context)
        container.removeAllViews()
        container.addView(header(R.string.miui_and_hyperos))
        container.addView(
            rows.switch(
                R.string.miui_hide_network_speed,
                get = { config.mMiuiHideNetworkSpeed },
                set = { config.mMiuiHideNetworkSpeed = it }
            ))
        container.addView(
            rows.switch(
                R.string.miui_pad_optimize,
                get = { config.mMiuiPadOptimize },
                set = { config.mMiuiPadOptimize = it }
            ))
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
            container.addView(
                rows.switch(
                    R.string.hide_carrier,
                    get = { config.hideCarrier },
                    set = { config.hideCarrier = it }
                ))
        }
        container.addView(header(R.string.hyperos))
        container.addView(
            rows.switch(
                R.string.automate_focused_notice,
                get = { config.automateFocusedNotice },
                set = { config.automateFocusedNotice = it }
            ))
        container.addView(
            rows.switch(
                R.string.hyperos_texture,
                get = { config.mHyperOSTexture },
                set = {
                    config.mHyperOSTexture = it
                    rebuild()
                }
            ))
        if (config.mHyperOSTexture) {
            container.addView(
                rows.integer(
                    R.string.hyperos_texture_radio,
                    R.string.lyric_stroke_width_tips,
                    min = 0,
                    max = 1000,
                    get = { config.mHyperOSTextureRadio },
                    set = { config.mHyperOSTextureRadio = it }
                ))
            container.addView(
                rows.integer(
                    R.string.hyperos_texture_corner,
                    R.string.lyric_letter_spacing_tips,
                    min = 0,
                    max = 1000,
                    get = { config.mHyperOSTextureCorner },
                    set = { config.mHyperOSTextureCorner = it }
                ))
            container.addView(
                rows.color(
                    R.string.hyperos_texture_color,
                    R.string.lyric_color_and_transparency_tips,
                    fallback = "#15818181",
                    get = { config.mHyperOSTextureBgColor },
                    set = { config.mHyperOSTextureBgColor = it }
                ))
        }
        container.addView(
            rows.action(R.string.reset_system_ui) {
                rows.confirm(
                    R.string.reset_system_ui,
                    R.string.restart_systemui_tips,
                    R.string.ok
                ) { shell("killall com.android.systemui", true) }
            })
    }

    private fun header(title: Int): View = TextView(requireContext()).apply {
        setText(title)
        setTypeface(typeface, Typeface.BOLD)
        val padding = dp2px(requireContext(), 16f)
        setPadding(padding, padding, padding, padding / 2)
    }
}
