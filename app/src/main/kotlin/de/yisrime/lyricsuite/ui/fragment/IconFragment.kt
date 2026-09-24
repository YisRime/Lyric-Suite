package de.yisrime.lyricsuite.ui.fragment

import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config

class IconFragment : ConfigFragment() {

    override val titleRes = R.string.icon_page

    override fun ConfigScreen.content() {
        switch(R.string.icon_switch, prop = config::iconSwitch) { invalidate() }
        resetSystemUiRow()
        if (!config.iconSwitch) return
        header(R.string.module_second)
        integer(
            R.string.icon_size, R.string.icon_size_tips, 0..100, config::iconSize,
            unit = R.string.unit_pixel
        )
        color(
            R.string.icon_color_and_transparency,
            R.string.icon_color_and_transparency_tips,
            "",
            config::iconColor
        )
        color(
            R.string.icon_background_color_and_transparency,
            R.string.icon_background_color_and_transparency_tips,
            "",
            config::iconBgColor
        )
        header(R.string.module_fourth)
        integer(
            R.string.icon_top_margins, R.string.icon_top_margins_tips, 0..100, config::iconTopMargins
        )
        integer(
            R.string.icon_bottom_margins,
            R.string.icon_bottom_margins_tips,
            0..100,
            config::iconBottomMargins
        )
        integer(
            R.string.icon_start_margins,
            R.string.icon_start_margins_tips,
            -2000..2000,
            config::iconStartMargins
        )
        header(R.string.module_third)
        switch(
            R.string.force_the_icon_to_be_displayed,
            prop = config::forceTheIconToBeDisplayed
        )
        text(
            R.string.change_all_icons, R.string.change_all_icons_tips, config::changeAllIcons
        )
    }
}
