package de.yisrime.lyricsuite.ui.fragment

import androidx.navigation.fragment.findNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools

class LyricFragment : ConfigFragment() {

    override val titleRes = R.string.lyric_page

    override fun ConfigScreen.content() {
        header(R.string.module_second)
        switch(R.string.icon_switch, prop = config::iconSwitch)
        integer(
            R.string.lyric_width, R.string.lyric_width_tips, 0..100, config::lyricWidth
        ) { invalidate() }
        if (config.lyricWidth != 0) {
            switch(
                R.string.fixed_lyric_width,
                R.string.fixed_lyric_width_tips,
                prop = config::fixedLyricWidth
            )
        }
        integer(
            R.string.lyric_size, R.string.lyric_size_tips, 0..100, config::lyricSize,
            unit = R.string.unit_pixel
        )
        color(
            R.string.lyric_color_and_transparency,
            R.string.lyric_color_and_transparency_tips,
            "",
            config::lyricColor
        )
        gradient(
            R.string.lyrics_are_gradient_and_transparent,
            R.string.lyrics_are_gradient_and_transparent_tips,
            prop = config::lyricGradientColor
        )
        gradient(
            R.string.lyrics_gradient_background_color_and_transparency,
            R.string.lyrics_gradient_background_color_and_transparency_tips,
            hint = R.string.tips1,
            prop = config::lyricBackgroundColor
        )
        integer(
            R.string.lyric_background_radius,
            R.string.lyric_background_radius_tips,
            0..100,
            config::lyricBackgroundRadius
        )
        integer(
            R.string.lyric_letter_spacing,
            R.string.lyric_letter_spacing_tips,
            0..50,
            config::lyricLetterSpacing
        )
        integer(
            R.string.lyric_stroke_width, R.string.lyric_stroke_width_tips, 0..400, config::lyricStrokeWidth
        )
        integer(R.string.lyric_speed, R.string.lyric_speed_tips, 0..20, config::lyricSpeed)
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
