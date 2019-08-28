package dev.jtsalva.cloudmare

import android.content.Context
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet

class Dialog(val context: Context) {
    private val bottomSheet: MaterialDialog = MaterialDialog(context, BottomSheet(LayoutMode.WRAP_CONTENT))

    companion object {

        private val openDialogs = mutableMapOf<Int, MaterialDialog>()

        private fun setOpenDialog(activityHash: Int, dialog: MaterialDialog) {
            openDialogs[activityHash]?.dismiss()
            openDialogs[activityHash] = dialog
        }

        fun dismissOpenDialog(activityHash: Int) {
            openDialogs[activityHash]?.dismiss()
            openDialogs.remove(activityHash)
        }

    }

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
        val dialog = bottomSheet.show {
            title(text = title)
            if (message != "") message(text = message)

            positiveButton(text = positive) { dialog ->
                onResult(true)
            }

            negativeButton(text = negative) { dialog ->
                onResult(false)
            }
        }

        openDialogs[context.hashCode()] = dialog

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