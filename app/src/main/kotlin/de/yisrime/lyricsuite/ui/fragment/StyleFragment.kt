package de.yisrime.lyricsuite.ui.fragment

import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools.shell

class StyleFragment : ConfigFragment() {

    override val titleRes = R.string.style_page

    override fun ConfigScreen.content() {
        header(R.string.module_second)
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
        action(R.string.reset_system_ui) {
            confirm(
                R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
