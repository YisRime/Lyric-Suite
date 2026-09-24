package de.yisrime.lrcget.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textview.MaterialTextView
import de.yisrime.lyricsuite.R
import de.yisrime.lrcget.tool.Tools.captureLogs
import de.yisrime.lrcget.tool.Tools.clearLogs

class LogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        findViewById<MaterialToolbar>(R.id.toolbar).setNavigationOnClickListener { finish() }

        val content = findViewById<MaterialTextView>(R.id.log_content)
        findViewById<MaterialTextView>(R.id.log_reload).setOnClickListener { load(content) }
        findViewById<MaterialTextView>(R.id.log_clear).setOnClickListener {
            if (clearLogs()) load(content) else content.setText(R.string.log_no_root)
        }
        load(content)
    }

    private fun load(content: MaterialTextView) {
        content.setText(R.string.log_loading)
        Thread {
            val logs = captureLogs()
            runOnUiThread {
                content.text = when {
                    logs == null -> getString(R.string.log_no_root)
                    logs.isBlank() -> getString(R.string.log_empty)
                    else -> logs.lines().filter { it.isNotBlank() }.reversed().joinToString("\n")
                }
            }
        }.start()
    }
}
