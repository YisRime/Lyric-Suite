package de.yisrime.lrcget.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import de.yisrime.lrcget.data.AppInfos

class AppRulesViewModel(private val state: SavedStateHandle) : ViewModel() {

    var dataLists: ArrayList<AppInfos> = ArrayList()
    var scrollY: Int
        get() = state["scrollY"] ?: 0
        set(value) {
            state["scrollY"] = value
        }
}