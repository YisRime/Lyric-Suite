package de.yisrime.lyricsuite.ui.fragment

import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.tools.Tools.shell

class ExtendFragment : ConfigFragment() {

    override val titleRes = R.string.extend_page

    override fun ConfigScreen.content() {
        val start = R.string.title_gravity_start
        val center = R.string.title_gravity_center
        val end = R.string.title_gravity_end
        choice(
            R.string.view_location,
            labels(start, end),
            config::viewLocation
        )
        switch(R.string.hide_time, prop = config::hideTime)
        switch(R.string.hide_notification_icon, prop = config::hideNotificationIcon)
        switch(R.string.hide_lyric_when_lock_screen, prop = config::hideLyricWhenLockScreen)
        switch(R.string.long_click_status_bar_stop, prop = config::longClickStatusBarStop)
        switch(R.string.click_status_bar_to_hide_lyric, prop = config::clickStatusBarToHideLyric)
        switch(R.string.slide_status_bar_cut_songs, prop = config::slideStatusBarCutSongs) {
            invalidate()
            }
        if (config.slideStatusBarCutSongs) {
            integer(
                R.string.slide_status_bar_cut_songs_x_radius,
                0..2000,
                config::slideStatusBarCutSongsXRadius
            )
            integer(
                R.string.slide_status_bar_cut_songs_y_radius,
                0..100,
                config::slideStatusBarCutSongsYRadius
            )
        }
        switch(
            R.string.limit_visibility_change,
            R.string.limit_visibility_change_tips,
            prop = config::limitVisibilityChange
        )
        switch(R.string.timeout_restore, prop = config::timeoutRestore)
        switch(R.string.dynamic_lyric_speed, prop = config::dynamicLyricSpeed)

        header(R.string.module_fifth)
        switch(R.string.title_switch, prop = config::titleSwitch) {
            invalidate()
            }
        if (config.titleSwitch) {
            switch(R.string.title_show_with_same_lyric, prop = config::titleShowWithSameLyric)
            integer(
                R.string.title_delay_duration, 0..10000, config::titleDelayDuration
            )
            color(
                R.string.title_color_and_transparency,
                "#000000",
                config::titleColorAndTransparency
            )
            integer(
                R.string.title_background_radius, 0..100, config::titleBackgroundRadius
            )
            integer(
                R.string.title_background_stroke_width, 0..30, config::titleBackgroundStrokeWidth
            )
            color(
                R.string.title_background_stroke_color,
                "#FFFFFF",
                config::titleBackgroundStrokeColorAndTransparency
            )
            choice(
                R.string.title_gravity,
                labels(start, center, end),
                config::titleGravity
            )
        }
        action(R.string.reset_system_ui) {
            confirm(
                R.string.reset_system_ui,
                R.string.restart_systemui_tips,
                R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
