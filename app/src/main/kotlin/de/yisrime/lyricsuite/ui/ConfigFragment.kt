package de.yisrime.lyricsuite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

abstract class ConfigFragment : Fragment() {
    private lateinit var _screen: ConfigScreen

    protected val screen: ConfigScreen get() = _screen

    abstract fun ConfigScreen.content()

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _screen = ConfigScreen(requireContext())
        return _screen.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _screen.build { it.content() }
    }
}
