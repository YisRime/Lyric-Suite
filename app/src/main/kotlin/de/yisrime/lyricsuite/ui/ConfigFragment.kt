package de.yisrime.lyricsuite.ui

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import de.yisrime.lyricsuite.R

abstract class ConfigFragment : Fragment() {
    private lateinit var _screen: ConfigScreen
    private lateinit var _toolbar: MaterialToolbar

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
        _toolbar = MaterialToolbar(context).apply {
            setTitle(titleRes)
            // 程序化工具栏的标题色在该主题下解析为白色，与行标题一致改用 Material 的 colorOnSurface
            val typed = TypedValue()
            context.theme.resolveAttribute(com.google.android.material.R.attr.colorOnSurface, typed, true)
            setTitleTextColor(ContextCompat.getColor(context, typed.resourceId))
        }
        val bar = AppBarLayout(context).apply {
            fitsSystemWindows = true
            addView(
                _toolbar,
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
        // 次级页统一带返回键；底部导航的三个标签根页不属于次级页，不显示
        val nav = findNavController()
        val topLevel = setOf(R.id.home_nav, R.id.lyric_nav, R.id.settings_nav)
        if (nav.currentDestination?.id !in topLevel && nav.previousBackStackEntry != null) {
            _toolbar.setTitle(titleRes)
            _toolbar.setNavigationIcon(R.drawable.ic_back)
            _toolbar.setNavigationOnClickListener { nav.navigateUp() }
        }
    }
}
