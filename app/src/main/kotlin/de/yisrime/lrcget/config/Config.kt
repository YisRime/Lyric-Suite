package de.yisrime.lrcget.config

import de.yisrime.lyricsuite.config.serialLazy

class Config {
    var alreadyShowWarning: Boolean by serialLazy(false)
    var showAllRules: Boolean by serialLazy(false)
    var outputRepeatedLyrics: Boolean by serialLazy(false)
    var allowSomeSoftwareToOutputAfterTheScreen: Boolean by serialLazy(false)
    var regexReplace: String by serialLazy("")
    var saltUseFlyme: Boolean by serialLazy(false)
    var fuckWyy: Boolean by serialLazy(false)
    var fuckWyy2: Boolean by serialLazy(false)
}
        