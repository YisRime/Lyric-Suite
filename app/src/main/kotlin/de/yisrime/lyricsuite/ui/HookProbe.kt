package de.yisrime.lyricsuite.ui

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.widget.Toast
import de.yisrime.lrcbar.data.Data
import de.yisrime.lrcbar.tools.LogTools.log
import de.yisrime.lyricsuite.R

object HookProbe {
    var points: List<Data> = emptyList()
        private set

    private var listener: (() -> Unit)? = null

    fun clear() {
        points = emptyList()
    }
    private var registered = false

    private val receiver = object : BroadcastReceiver() {
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("Type") != "ReceiveClass") return
            val list = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getSerializableExtra("DataList", ArrayList::class.java)
            } else {
                intent.getSerializableExtra("DataList")
            } as? ArrayList<Data> ?: arrayListOf()
            if (list.isEmpty()) {
                "DataList is empty".log()
                Toast.makeText(context, R.string.not_found_hook, Toast.LENGTH_SHORT).show()
            } else {
                "DataList size: ${list.size}".log()
            }
            points = list
            listener?.invoke()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    fun start(context: Context) {
        if (registered) return
        val filter = IntentFilter("AppTestReceiver")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.applicationContext.registerReceiver(receiver, filter, Context.RECEIVER_EXPORTED)
        } else {
            context.applicationContext.registerReceiver(receiver, filter)
        }
        registered = true
    }

    fun observe(listener: (() -> Unit)?) {
        this.listener = listener
    }
}
