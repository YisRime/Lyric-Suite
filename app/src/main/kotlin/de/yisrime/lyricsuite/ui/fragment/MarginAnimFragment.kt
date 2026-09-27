package de.yisrime.lyricsuite.ui.fragment

import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config

class MarginAnimFragment : ConfigFragment() {

    override val titleRes = R.string.margin_anim_page

    override fun ConfigScreen.content() {
        header(R.string.module_fourth)
        integer(
            R.string.lyric_top_margins, R.string.lyric_top_margins_tips, 0..100, config::lyricTopMargins
        )
        integer(
            R.string.lyric_bottom_margins,
            R.string.lyric_bottom_margins_tips,
            0..100,
            config::lyricBottomMargins
        )
        integer(
            R.string.lyric_start_margins,
            R.string.lyric_start_margins_tips,
            -2000..2000,
            config::lyricStartMargins
        )
        integer(
            R.string.lyric_end_margins, R.string.lyric_end_margins_tips, -2000..2000, config::lyricEndMargins
        )
        header(R.string.module_sixth)
        choice(
            R.string.lyrics_animation,
            labels(
                R.string.lyrics_animation_none,
                R.string.lyrics_animation_top,
                R.string.lyrics_animation_bottom,
                R.string.lyrics_animation_start,
                R.string.lyrics_animation_end,
                R.string.lyrics_animation_fade,
                R.string.lyrics_animation_scale_x_y,
                R.string.lyrics_animation_scale_x,
                R.string.lyrics_animation_scale_y,
                R.string.lyrics_animation_horizontalflip,
                R.string.lyrics_animation_verticalflip,
                R.string.lyrics_animation_random
            ),
            config::lyricAnimation
        )
        choice(
            R.string.lyrics_animation_interpolator,
            labels(
                R.string.lyrics_interpolator_linear,
                R.string.lyrics_interpolator_accelerate,
                R.string.lyrics_interpolator_decelerate,
                R.string.lyrics_interpolator_accelerate_decelerate,
                R.string.lyrics_interpolator_overshoot,
                R.string.lyrics_interpolator_bounce
            ),
            config::lyricInterpolator
        )
        integer(
            R.string.lyrics_animation_duration,
            R.string.lyric_animation_duration_tips,
            0..1000,
            config::animationDuration
        )
    }
}
