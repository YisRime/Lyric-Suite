package de.yisrime.lyricsuite.ui.fragment

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import de.yisrime.lyricsuite.R
import de.yisrime.lyricsuite.ui.ConfigScreen
import de.yisrime.lrcbar.config.XposedOwnSP.config
import de.yisrime.lrcbar.data.Data
import de.yisrime.lrcbar.tools.ActivityTestTools.showView

class HookPickerFragment : Fragment() {

    private lateinit var screen: ConfigScreen
    private var receiver: BroadcastReceiver? = null

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
        screen.build { it.header(R.string.choose_page_tips) }
        register()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun register() {
        val listener = object : BroadcastReceiver() {
            @Suppress("DEPRECATION", "UNCHECKED_CAST")
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.getStringExtra("Type") != "ReceiveClass") return
                val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    intent.getSerializableExtra("DataList", ArrayList::class.java)
                } else {
                    intent.getSerializableExtra("DataList")
                } as? ArrayList<Data> ?: return
                requireActivity().runOnUiThread { render(list) }
            }
        }
        receiver = listener
        val filter = IntentFilter("AppTestReceiver")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(listener, filter, Context.RECEIVER_EXPORTED)
        } else {
            requireContext().registerReceiver(listener, filter)
        }
    }

    private fun render(list: ArrayList<Data>) {
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
        receiver?.let { runCatching { requireContext().unregisterReceiver(it) } }
        receiver = null
        super.onDestroyView()
    }
}
