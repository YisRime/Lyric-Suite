package de.yisrime.lrcget.config

import de.yisrime.lyricsuite.config.getterLazy

class Config {
    var alreadyShowWarning: Boolean by getterLazy(false)
    var showAllRules: Boolean by getterLazy(false)
    var outputRepeatedLyrics: Boolean by getterLazy(false)
    var allowSomeSoftwareToOutputAfterTheScreen: Boolean by getterLazy(false)
    var regexReplace: String by getterLazy("")
    var saltUseFlyme: Boolean by getterLazy(false)
    var fuckWyy: Boolean by getterLazy(false)
    var fuckWyy2: Boolean by getterLazy(false)
}
        