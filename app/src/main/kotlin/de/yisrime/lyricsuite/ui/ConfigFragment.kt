package de.yisrime.lyricsuite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar

abstract class ConfigFragment : Fragment() {
    private lateinit var _screen: ConfigScreen

    protected val screen: ConfigScreen get() = _screen

    @get:StringRes
    protected abstract val titleRes: Int

    abstract fun ConfigScreen.content()

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        _screen = ConfigScreen(context)
        val bar = AppBarLayout(context).apply {
            fitsSystemWindows = true
            addView(
                MaterialToolbar(context).apply { setTitle(titleRes) },
                AppBarLayout.LayoutParams(
                    AppBarLayout.LayoutParams.MATCH_PARENT,
                    AppBarLayout.LayoutParams.WRAP_CONTENT
                )
            )
        }
        val body = _screen.root.apply {
            layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
            ).apply { behavior = AppBarLayout.ScrollingViewBehavior() }
        }
        return CoordinatorLayout(context).apply {
            addView(bar)
            addView(body)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _screen.build { it.content() }
    }
}
