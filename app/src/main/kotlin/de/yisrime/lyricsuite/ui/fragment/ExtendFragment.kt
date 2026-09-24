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

class ExtendFragment : Fragment() {

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
        val start = R.string.title_gravity_start
        val center = R.string.title_gravity_center
        val end = R.string.title_gravity_end
        screen.header(R.string.module_extend)
        screen.choice(
            R.string.view_location,
            screen.labels(start, end),
            config::viewLocation
        )
        screen.switch(R.string.hide_time, prop = config::hideTime)
        screen.switch(R.string.hide_notification_icon, prop = config::hideNotificationIcon)
        screen.switch(R.string.hide_lyric_when_lock_screen, prop = config::hideLyricWhenLockScreen)
        screen.switch(R.string.long_click_status_bar_stop, prop = config::longClickStatusBarStop)
        screen.switch(R.string.click_status_bar_to_hide_lyric, prop = config::clickStatusBarToHideLyric)
        screen.switch(R.string.slide_status_bar_cut_songs, prop = config::slideStatusBarCutSongs) {
            screen.invalidate()
            }
        if (config.slideStatusBarCutSongs) {
            screen.integer(
                R.string.slide_status_bar_cut_songs_x_radius,
                R.string.slide_status_bar_cut_songs_x_radius_tips,
                0..2000,
                config::slideStatusBarCutSongsXRadius
            )
            screen.integer(
                R.string.slide_status_bar_cut_songs_y_radius,
                R.string.slide_status_bar_cut_songs_y_radius_tips,
                0..100,
                config::slideStatusBarCutSongsYRadius
            )
        }
        screen.switch(
            R.string.limit_visibility_change,
            R.string.limit_visibility_change_tips,
            prop = config::limitVisibilityChange
        )
        screen.switch(R.string.timeout_restore, prop = config::timeoutRestore)
        screen.switch(R.string.dynamic_lyric_speed, prop = config::dynamicLyricSpeed)

        screen.header(R.string.module_fifth)
        screen.switch(R.string.title_switch, prop = config::titleSwitch) {
            screen.invalidate()
            }
        if (config.titleSwitch) {
            screen.switch(R.string.title_show_with_same_lyric, prop = config::titleShowWithSameLyric)
            screen.integer(
                R.string.title_delay_duration,
                R.string.title_delay_duration_tips,
                0..10000,
                config::titleDelayDuration
            )
            screen.color(
                R.string.title_color_and_transparency,
                R.string.lyric_color_and_transparency_tips,
                "#000000",
                config::titleColorAndTransparency
            )
            screen.integer(
                R.string.title_background_radius,
                R.string.lyric_background_radius_tips,
                0..100,
                config::titleBackgroundRadius
            )
            screen.integer(
                R.string.title_background_stroke_width,
                R.string.title_background_stroke_width_tips,
                0..30,
                config::titleBackgroundStrokeWidth
            )
            screen.color(
                R.string.title_background_stroke_color,
                R.string.lyric_color_and_transparency_tips,
                "#FFFFFF",
                config::titleBackgroundStrokeColorAndTransparency
            )
            screen.choice(
                R.string.title_gravity,
                screen.labels(start, center, end),
                config::titleGravity
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
