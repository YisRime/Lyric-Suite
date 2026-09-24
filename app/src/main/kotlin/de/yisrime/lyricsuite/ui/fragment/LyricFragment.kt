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

class LyricFragment : Fragment() {

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
        val context = requireContext()
        screen.header(R.string.module_second)
        screen.integer(
            R.string.lyric_width, R.string.lyric_width_tips, 0..100, config::lyricWidth
        ) { screen.invalidate() }
        if (config.lyricWidth != 0) {
            screen.switch(
                R.string.fixed_lyric_width,
                R.string.fixed_lyric_width_tips,
                prop = config::fixedLyricWidth
            )
        }
        screen.integer(R.string.lyric_size, R.string.lyric_size_tips, 0..100, config::lyricSize)
        screen.color(
            R.string.lyric_color_and_transparency,
            R.string.lyric_color_and_transparency_tips,
            "",
            config::lyricColor
        )
        screen.gradient(
            R.string.lyrics_are_gradient_and_transparent,
            R.string.lyrics_are_gradient_and_transparent_tips,
            prop = config::lyricGradientColor
        )
        screen.gradient(
            R.string.lyrics_gradient_background_color_and_transparency,
            R.string.lyrics_gradient_background_color_and_transparency_tips,
            hint = R.string.tips1,
            prop = config::lyricBackgroundColor
        )
        screen.integer(
            R.string.lyric_background_radius,
            R.string.lyric_background_radius_tips,
            0..100,
            config::lyricBackgroundRadius
        )
        screen.integer(
            R.string.lyric_letter_spacing,
            R.string.lyric_letter_spacing_tips,
            0..50,
            config::lyricLetterSpacing
        )
        screen.integer(
            R.string.lyric_stroke_width, R.string.lyric_stroke_width_tips, 0..400, config::lyricStrokeWidth
        )
        screen.integer(R.string.lyric_speed, R.string.lyric_speed_tips, 0..20, config::lyricSpeed)

        screen.header(R.string.module_fourth)
        screen.integer(
            R.string.lyric_top_margins, R.string.lyric_top_margins_tips, 0..100, config::lyricTopMargins
        )
        screen.integer(
            R.string.lyric_bottom_margins,
            R.string.lyric_bottom_margins_tips,
            0..100,
            config::lyricBottomMargins
        )
        screen.integer(
            R.string.lyric_start_margins,
            R.string.lyric_start_margins_tips,
            -2000..2000,
            config::lyricStartMargins
        )
        screen.integer(
            R.string.lyric_end_margins, R.string.lyric_end_margins_tips, -2000..2000, config::lyricEndMargins
        )

        screen.header(R.string.module_sixth)
        screen.choice(
            R.string.lyrics_animation,
            screen.labels(
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
        screen.choice(
            R.string.lyrics_animation_interpolator,
            screen.labels(
                R.string.lyrics_interpolator_linear,
                R.string.lyrics_interpolator_accelerate,
                R.string.lyrics_interpolator_decelerate,
                R.string.lyrics_interpolator_accelerate_decelerate,
                R.string.lyrics_interpolator_overshoot,
                R.string.lyrics_interpolator_bounce
            ),
            config::lyricInterpolator
        )
        screen.integer(
            R.string.lyrics_animation_duration,
            R.string.lyric_animation_duration_tips,
            0..1000,
            config::animationDuration
        )
        screen.action(R.string.reset_system_ui) {
            screen.confirm(
                R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
