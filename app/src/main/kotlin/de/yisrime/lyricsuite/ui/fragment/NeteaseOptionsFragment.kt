package de.yisrime.lyricsuite.ui.fragment

import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.ConfigTools.config

class NeteaseOptionsFragment : ConfigFragment() {

    override val titleRes = R.string.fuck_wyy_about

    override fun ConfigScreen.content() {
        switch(R.string.fuck_fuck_wyy, R.string.fuck_fuck_wyy_tips, prop = config::fuckWyy2)
        switch(R.string.fuck_wyy, R.string.fuck_wyy_tips, prop = config::fuckWyy)
    }
}
