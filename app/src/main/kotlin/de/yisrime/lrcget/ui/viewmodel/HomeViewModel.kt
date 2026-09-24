package de.yisrime.lrcget.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class HomeViewModel(private val state: SavedStateHandle) : ViewModel() {

    var scrollY: Int
        get() = state["scrollY"] ?: 0
        set(value) {
            state["scrollY"] = value
        }

    var expanded: Boolean
        get() = state.get<Boolean>("expanded") != false
        set(value) {
            state.set("expanded", value)
        }
    var appRulesVersionValue: String?
        get() = state["appRulesVersionValue"]
        set(value) {
            state["appRulesVersionValue"] = value
        }
    var buildTimeValue: String?
        get() = state["buildTimeValue"]
        set(value) {
            state["buildTimeValue"] = value
        }
}