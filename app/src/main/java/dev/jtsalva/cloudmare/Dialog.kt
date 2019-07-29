package dev.jtsalva.cloudmare

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

class Dialog(private val context: Context) {
    fun confirm(title: String = "Are you sure?",
                positive: String = "Yes",
                negative: String = "Cancel",
                onResult: (confirmed: Boolean) -> Unit) =
        MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = title)

            positiveButton(text = positive) { dialog ->
                onResult(true)
                dialog.hide()
            }

            negativeButton(text = negative) { dialog ->
                onResult(false)
                dialog.hide()
            }
        }
}