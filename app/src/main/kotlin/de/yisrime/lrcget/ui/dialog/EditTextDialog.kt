package de.yisrime.lrcget.ui.dialog

import android.content.Context
import androidx.annotation.StringRes
import android.text.InputType
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import de.yisrime.lyricsuite.R
import de.yisrime.lrcget.tool.Tools.dp2px
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextDialog(private val context: Context) {
    private val rootLayout = LinearLayout(context).apply {
        orientation = LinearLayout.VERTICAL
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        setPadding(dp2px(context, 24f), 0, dp2px(context, 24f), 0)
    }

    private val textInputLayout = TextInputLayout(context).apply {
        layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    private val editText = TextInputEditText(context)

    private var title: String = ""
    private var message: Int = 0

    init {
        textInputLayout.addView(editText)
        rootLayout.addView(textInputLayout)
    }

    fun setTitle(title: String): EditTextDialog {
        this.title = title
        return this
    }

    fun setMessage(@StringRes resId: Int): EditTextDialog {
        message = resId
        return this
    }

    fun setText(text: String): EditTextDialog {
        editText.setText(text)
        editText.setSelection(text.length)
        return this
    }

    fun setNumericInput(): EditTextDialog {
        editText.inputType =
            InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
        return this
    }

    fun setUnit(@StringRes resId: Int): EditTextDialog {
        textInputLayout.suffixText = context.getString(resId)
        return this
    }

    fun show(onConfirm: (String) -> Unit): AlertDialog {
        val dialog = MaterialAlertDialogBuilder(context).apply {
            setView(rootLayout)
            setTitle(title)
            if (message != 0) setMessage(message)
            setPositiveButton(R.string.ok) { _, _ ->
                onConfirm(editText.text.toString())
            }
            setNegativeButton(R.string.cancel, null)
        }.show()

        return dialog
    }
}