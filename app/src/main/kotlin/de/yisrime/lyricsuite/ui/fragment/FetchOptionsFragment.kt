package de.yisrime.lyricsuite.ui.fragment

import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcget.tool.ConfigTools.config

class FetchOptionsFragment : ConfigFragment() {

    override val titleRes = R.string.lyric_setting

    override fun ConfigScreen.content() {
        switch(R.string.output_repeated_lyrics, prop = config::outputRepeatedLyrics)
        switch(
            R.string.allow_some_software_to_output_after_the_screen,
            prop = config::allowSomeSoftwareToOutputAfterTheScreen
        )
    }
}
