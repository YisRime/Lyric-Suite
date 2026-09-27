package de.yisrime.lyricsuite.ui.fragment

import android.os.Build
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools

class SystemSpecialFragment : ConfigFragment() {

    override val titleRes = R.string.system_special_page

    override fun ConfigScreen.content() {
        // 非 MIUI/HyperOS 设备本页为空，入口在歌词设置页已按系统隐藏
        if (Tools.isMiui) {
            header(R.string.miui_and_hyperos)
            switch(R.string.miui_hide_network_speed, prop = config::mMiuiHideNetworkSpeed)
            switch(R.string.miui_pad_optimize, prop = config::mMiuiPadOptimize)
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                switch(R.string.hide_carrier, prop = config::hideCarrier)
            }
        }
        if (Tools.isHyperOs) {
            header(R.string.hyperos)
            switch(R.string.automate_focused_notice, prop = config::automateFocusedNotice)
            switch(R.string.hyperos_texture, prop = config::mHyperOSTexture) {
                invalidate()
            }
            if (config.mHyperOSTexture) {
                integer(
                    R.string.hyperos_texture_radio,
                    R.string.lyric_stroke_width_tips,
                    0..1000,
                    config::mHyperOSTextureRadio
                )
                integer(
                    R.string.hyperos_texture_corner,
                    R.string.lyric_letter_spacing_tips,
                    0..1000,
                    config::mHyperOSTextureCorner
                )
                color(
                    R.string.hyperos_texture_color,
                    R.string.lyric_color_and_transparency_tips,
                    "#15818181",
                    config::mHyperOSTextureBgColor
                )
            }
        }
    }
}
