package de.yisrime.lyricsuite.ui.fragment

import androidx.navigation.fragment.findNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools

class LyricFragment : ConfigFragment() {

    override val titleRes = R.string.lyric_page

    override val showToolbar = false

    override fun ConfigScreen.content() {
        header(R.string.module_second)
        integer(
            R.string.lyric_width, 0..100, config::lyricWidth
        ) { invalidate() }
        if (config.lyricWidth != 0) {
            switch(
                R.string.fixed_lyric_width,
                R.string.fixed_lyric_width_tips,
                prop = config::fixedLyricWidth
            )
        }
        integer(
            R.string.lyric_size, 0..100, config::lyricSize,
            unit = R.string.unit_pixel
        )
        color(
            R.string.lyric_color_and_transparency,
            "",
            config::lyricColor
        )
        gradient(
            R.string.lyrics_are_gradient_and_transparent,
            prop = config::lyricGradientColor
        )
        gradient(
            R.string.lyrics_gradient_background_color_and_transparency,
            prop = config::lyricBackgroundColor
        )
        integer(
            R.string.lyric_background_radius, 0..100, config::lyricBackgroundRadius
        )
        integer(
            R.string.lyric_letter_spacing, 0..50, config::lyricLetterSpacing
        )
        integer(
            R.string.lyric_stroke_width, 0..400, config::lyricStrokeWidth
        )
        integer(R.string.lyric_speed, 0..20, config::lyricSpeed)
        header(R.string.more_options)
        action(R.string.margin_anim_page) { findNavController().navigate(R.id.margin_anim_nav) }
        action(R.string.icon_page) { findNavController().navigate(R.id.icon_nav) }
        action(R.string.extend_page) { findNavController().navigate(R.id.extend_nav) }
        // 非 MIUI/HyperOS 设备没有可适配项，入口直接不显示
        if (Tools.isMiui || Tools.isHyperOs) {
            action(R.string.system_special_page) { findNavController().navigate(R.id.system_special_nav) }
        }
    }
}
