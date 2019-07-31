package dev.jtsalva.cloudmare

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

class Dialog(context: Context) {
    private val bottomSheet: MaterialDialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))

    fun error(title: String = "Oops",
              message: String = "Something went wrong",
              positive: String = "Try again",
              onAcknowledge: () -> Unit = {}) =
        bottomSheet.show {
            title(text = title)
            message(text = message)
            positiveButton(text = positive) { dialog ->
                onAcknowledge()
                dialog.dismiss()
            }
        }

    fun confirm(title: String = "Are you sure?",
                positive: String = "Yes",
                negative: String = "Cancel",
                onResult: (confirmed: Boolean) -> Unit) =
        bottomSheet.show {
            title(text = title)

            positiveButton(text = positive) { dialog ->
                onResult(true)
                dialog.dismiss()
            }

            negativeButton(text = negative) { dialog ->
                onResult(false)
                dialog.dismiss()
            }
        }
}