package dev.jtsalva.cloudmare

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

class Dialog(context: Context) {
    private val bottomSheet: MaterialDialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))

    fun dismiss() = bottomSheet.dismiss()

    fun error(title: String = "Oops",
              message: String = "Something went wrong",
              positive: String = "Try again",
              onAcknowledge: () -> Unit = {}): Dialog {
        bottomSheet.show {
            title(text = title)
            message(text = message)
            positiveButton(text = positive) { dialog ->
                dialog.dismiss()
                onAcknowledge()
            }
        }

        return this
    }

    fun confirm(title: String = "Are you sure?",
                message: String = "",
                positive: String = "Yes",
                negative: String = "Cancel",
                onResult: (confirmed: Boolean) -> Unit): Dialog {
        bottomSheet.show {
            title(text = title)
            if (message != "") message(text = message)

            positiveButton(text = positive) { dialog ->
                dialog.dismiss()
                onResult(true)
            }

            negativeButton(text = negative) { dialog ->
                dialog.dismiss()
                onResult(false)
            }
        }

        return this
    }

    fun validating(title: String = "Validating",
                   message: String = ""): Dialog {
        bottomSheet.show {
            cancelable(false)
            setCanceledOnTouchOutside(false)

            title(text = title)
            if (message != "") message(text = message)
        }

        return this
    }
}