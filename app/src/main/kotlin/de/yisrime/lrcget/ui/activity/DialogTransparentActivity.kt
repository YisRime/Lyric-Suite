package de.yisrime.lrcget.ui.activity

import android.content.Context
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import de.yisrime.lyricsuite.R
import de.yisrime.lrcget.tool.ConfigTools.config
import de.yisrime.lrcget.ui.dialog.EditTextDialog

class DialogTransparentActivity : AppCompatActivity() {

    private fun openRegexReplaceDialog(context: Context) {
        val title = context.getString(R.string.regex_replace)
        val a = EditTextDialog(context)
            .setTitle(title)
            .setText(config.regexReplace)
            .show {
                config.regexReplace = it
            }
        a.setOnDismissListener {
            finish()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }

    override fun onStart() {
        super.onStart()
        openRegexReplaceDialog(this)

    }
}