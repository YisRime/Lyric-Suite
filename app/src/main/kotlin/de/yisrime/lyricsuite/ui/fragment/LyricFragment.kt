package de.yisrime.lyricsuite.ui.fragment

import androidx.navigation.fragment.findNavController
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.tools.Tools.shell

class LyricFragment : ConfigFragment() {

    override val titleRes = R.string.lyric_page

    override fun ConfigScreen.content() {
        // 纯索引：锚点最关键置顶，其余按使用频率排列
        action(R.string.hook_page) { findNavController().navigate(R.id.test_nav) }
        action(R.string.style_page) { findNavController().navigate(R.id.style_nav) }
        action(R.string.margin_anim_page) { findNavController().navigate(R.id.margin_anim_nav) }
        action(R.string.icon_page) { findNavController().navigate(R.id.icon_nav) }
        action(R.string.extend_page) { findNavController().navigate(R.id.extend_nav) }
        action(R.string.system_special_page) { findNavController().navigate(R.id.system_special_nav) }
        action(R.string.reset_system_ui) {
            confirm(
                R.string.reset_system_ui, R.string.restart_systemui_tips, R.string.ok
            ) { shell("killall com.android.systemui", true) }
        }
    }
}
