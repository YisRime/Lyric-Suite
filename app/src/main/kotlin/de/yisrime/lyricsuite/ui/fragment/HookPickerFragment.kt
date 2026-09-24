package de.yisrime.lyricsuite.ui.fragment

import android.os.Bundle
import android.view.View
import android.widget.Toast
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigFragment
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lyricsuite.ui.HookProbe
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.data.Data
import de.yisrime.lrcbar.tools.ActivityTestTools.showView

class HookPickerFragment : ConfigFragment() {

    override val titleRes = R.string.select_hook

    override fun ConfigScreen.content() {
        header(R.string.choose_page_tips)
        HookProbe.points.forEach { data ->
            entry(
                title = "${data.textViewClassName} ${data.textViewId}",
                summary = "idName: ${data.idName}; textSize: ${data.textSize}f\n" +
                        "${data.parentViewClassName} ${data.parentViewId}"
            ) {
                requireContext().showView(data)
                confirm(R.string.select_hook, affirmative = R.string.ok) { commit(data) }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HookProbe.observe { screen.invalidate() }
        view.postDelayed({
            if (HookProbe.points.isEmpty()) {
                Toast.makeText(requireContext(), R.string.broadcast_receive_timeout, Toast.LENGTH_SHORT)
                    .show()
            }
        }, TIMEOUT)
    }

    override fun onDestroyView() {
        HookProbe.observe(null)
        super.onDestroyView()
    }

    private fun commit(data: Data) {
        config.textViewClassName = data.textViewClassName
        config.textViewId = data.textViewId
        config.parentViewClassName = data.parentViewClassName
        config.parentViewId = data.parentViewId
        config.index = data.index
        config.textSize = data.textSize
    }

    companion object {
        private const val TIMEOUT = 2000L
    }
}
