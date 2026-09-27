package de.yisrime.lyricsuite.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.MaterialToolbar
import de.yisrime.lyricsuite.R

abstract class ConfigFragment : Fragment() {
    private lateinit var _screen: ConfigScreen
    private lateinit var _toolbar: MaterialToolbar
    private lateinit var _title: TextView

    protected val screen: ConfigScreen get() = _screen

    @get:StringRes
    protected abstract val titleRes: Int

    // 标签根页（歌词/选项）不显示标题栏，直接呈现内容
    protected open val showToolbar: Boolean = true

    abstract fun ConfigScreen.content()

    final override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val context = requireContext()
        _screen = ConfigScreen(context)
        if (!showToolbar) {
            // 无标题栏：滚动视图自身让出状态栏，内容从页面顶部开始
            return (_screen.root as ViewGroup).apply {
                fitsSystemWindows = true
                clipToPadding = false
                setPadding(0, 0, 0, (24 * resources.displayMetrics.density).toInt())
            }
        }
        // 程序化构造的 MaterialToolbar 与 setTitle 在该主题下标题不渲染，
        // 改为 XML 工具栏 + 子 TextView 承载标题（普通 TextView 在本应用必然渲染）
        _toolbar = LayoutInflater.from(context)
            .inflate(R.layout.view_config_toolbar, null, false) as MaterialToolbar
        _title = _toolbar.findViewById(R.id.toolbar_title)
        _title.setText(titleRes)
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
        if (!showToolbar) return
        // 次级页统一带返回键；标签根页不属于次级页，不显示
        val nav = findNavController()
        if (nav.previousBackStackEntry != null) {
            _toolbar.setNavigationIcon(R.drawable.ic_back)
            _toolbar.setNavigationOnClickListener { nav.navigateUp() }
            // 子 TextView 不吃工具栏导航内边距，手动让出箭头宽度
            _title.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart = (28 * resources.displayMetrics.density).toInt()
            }
        }
    }
}
