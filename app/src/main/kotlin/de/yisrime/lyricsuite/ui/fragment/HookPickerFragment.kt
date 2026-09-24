package de.yisrime.lyricsuite.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lyricsuite.ui.HookProbe
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.data.Data
import de.yisrime.lrcbar.tools.ActivityTestTools.showView

class HookPickerFragment : Fragment() {

    private lateinit var screen: ConfigScreen

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        screen = ConfigScreen(requireContext())
        return screen.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        HookProbe.observe { render() }
        render()
        view.postDelayed({
            if (HookProbe.points.isEmpty()) {
                Toast.makeText(requireContext(), R.string.broadcast_receive_timeout, Toast.LENGTH_SHORT).show()
            }
        }, TIMEOUT)
    }

    private fun render() {
        val list = HookProbe.points
        screen.build { screen ->
            screen.header(R.string.choose_page_tips)
            list.forEach { data ->
                screen.entry(
                    title = "${data.textViewClassName} ${data.textViewId}",
                    summary = "idName: ${data.idName}; textSize: ${data.textSize}f\n" +
                            "${data.parentViewClassName} ${data.parentViewId}"
                ) {
                    requireContext().showView(data)
                    screen.confirm(R.string.select_hook, affirmative = R.string.ok) {
                        commit(data)
                    }
                }
            }
        }
    }

    private fun commit(data: Data) {
        config.textViewClassName = data.textViewClassName
        config.textViewId = data.textViewId
        config.parentViewClassName = data.parentViewClassName
        config.parentViewId = data.parentViewId
        config.index = data.index
        config.textSize = data.textSize
    }

    override fun onDestroyView() {
        HookProbe.observe(null)
        super.onDestroyView()
    }

    companion object {
        private const val TIMEOUT = 2000L
    }
}
